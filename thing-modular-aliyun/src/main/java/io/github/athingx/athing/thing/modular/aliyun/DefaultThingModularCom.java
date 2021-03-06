package io.github.athingx.athing.thing.modular.aliyun;

import com.google.gson.Gson;
import io.github.athingx.athing.aliyun.thing.runtime.ThingRuntime;
import io.github.athingx.athing.aliyun.thing.runtime.linker.ThingLinker;
import io.github.athingx.athing.standard.thing.Thing;
import io.github.athingx.athing.standard.thing.ThingComListener;
import io.github.athingx.athing.standard.thing.executor.ThingExecutor;
import io.github.athingx.athing.thing.modular.ModuleUpgrade;
import io.github.athingx.athing.thing.modular.ModuleUpgradeListener;
import io.github.athingx.athing.thing.modular.ThingModularCom;
import io.github.athingx.athing.thing.modular.aliyun.domain.Meta;
import io.github.athingx.athing.thing.modular.aliyun.domain.Process;
import io.github.athingx.athing.thing.modular.aliyun.domain.Push;
import io.github.athingx.athing.thing.modular.aliyun.domain.Update;
import io.github.athingx.athing.thing.modular.aliyun.util.GsonUtils;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.athingx.athing.thing.modular.aliyun.UpgradeProcessor.Step.STEP_UPGRADES_COMPLETED;
import static io.github.athingx.athing.thing.modular.aliyun.UpgradeProcessor.Step.STEP_UPGRADES_FAILURE;

public class DefaultThingModularCom implements ThingModularCom, ThingComListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonUtils.gson;
    private final ModularOption option;
    private final ThingRuntime runtime;
    private final Set<ModuleUpgradeListener> listeners = ConcurrentHashMap.newKeySet();

    public DefaultThingModularCom(final Thing thing, final ModularOption option) {
        this.option = option;
        this.runtime = ThingRuntime.getInstance(thing);
    }

    @Override
    public ListenableFuture<Void> update(String moduleId, String version) {
        final Thing thing = runtime.getThing();
        final ThingLinker linker = runtime.getThingLinker();
        final String token = linker.generateToken();
        return linker.publish("/ota/device/inform/%s".formatted(thing.getPath()), new Update(token, moduleId, version))
                .onSuccess(v -> logger.info("{}/modular report version success, token={};modular={};version={};", thing, token, moduleId, version))
                .onFailure(e -> logger.warn("{}/modular report version failure, token={};modular={};version={};", thing, token, moduleId, version, e));
    }

    @Override
    public void appendListener(ModuleUpgradeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ModuleUpgradeListener listener) {
        listeners.remove(listener);
    }

    // ????????????????????????????????????
    private void upgradeModule(ModuleUpgrade upgrade) throws Exception {
        for (final ModuleUpgradeListener listener : listeners) {
            listener.upgrade(upgrade);
        }
    }

    @Override
    public void onLoaded(Thing thing) throws Exception {

        final ThingLinker linker = runtime.getThingLinker();
        final ThingExecutor executor = thing.getExecutor();

        // ??????PUSH
        linker.subscribe("/ota/device/upgrade/%s".formatted(thing.getPath()), (topic, json) -> {

            final Push push = gson.fromJson(json, Push.class);
            final Meta meta = push.getMeta();
            final UpgradeProcessor processor = new UpgradeProcessorImpl(meta);

            if (listeners.isEmpty()) {
                logger.warn("{}/modular give up upgrade: none-listener, modular={};version={};", thing, meta.getModuleId(), meta.getVersion());
                processor.processing(STEP_UPGRADES_FAILURE, "upgrade failure: none upgrade-listener");
                return;
            }

            final ModuleUpgrade upgrade = new ModuleUpgradeImpl(meta, option, executor, processor);
            try {

                // ????????????????????????
                upgradeModule(upgrade);
                logger.info("{}/modular upgrade success, module={};version={};", thing, meta.getModuleId(), meta.getVersion());

                // ????????????????????????
                processor.processing(STEP_UPGRADES_COMPLETED, "upgrade completed!");

                // ????????????????????????
                update(meta.getModuleId(), meta.getVersion());

            } catch (Exception cause) {
                logger.warn("{}/modular upgrade failure, modular={};version={};", thing, meta.getModuleId(), meta.getVersion(), cause);
                processor.processing(STEP_UPGRADES_FAILURE, "upgrade failure: %s".formatted(cause.getLocalizedMessage()));
            }

        }).sync();

    }

    /**
     * ????????????????????????
     * ???????????????????????????
     */
    private class UpgradeProcessorImpl implements UpgradeProcessor {

        private final Meta meta;
        private volatile int current;

        private UpgradeProcessorImpl(Meta meta) {
            this.meta = meta;
        }

        @Override
        public synchronized void processing(int step, String desc) {

            // ????????????????????????????????????????????????????????????????????????????????????
            if (current < 0 || current >= step) {
                return;
            }

            // ??????????????????
            current = step;
            final Thing thing = runtime.getThing();
            final ThingLinker linker = runtime.getThingLinker();
            final String token = linker.generateToken();
            final String moduleId = meta.getModuleId();
            linker.publish("/ota/device/progress/%s/".formatted(thing.getPath()), new Process(token, moduleId, step, desc))
                    .onSuccess(v -> logger.debug("{}/modular report process success, token={};modular={};step={};", thing, token, moduleId, step))
                    .onFailure(e -> logger.debug("{}/modular report process failure, token={};modular={};step={};", thing, token, moduleId, step, e))
                    .awaitUninterruptible();

        }

    }

}
