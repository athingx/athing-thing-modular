package io.github.athingx.athing.thing.modular.aliyun;

import com.google.gson.Gson;
import io.github.athingx.athing.aliyun.thing.runtime.ThingRuntime;
import io.github.athingx.athing.aliyun.thing.runtime.linker.ThingLinker;
import io.github.athingx.athing.standard.thing.Thing;
import io.github.athingx.athing.standard.thing.ThingLifeCycle;
import io.github.athingx.athing.standard.thing.boot.Inject;
import io.github.athingx.athing.standard.thing.op.executor.ThingExecutor;
import io.github.athingx.athing.thing.modular.ThingModularCom;
import io.github.athingx.athing.thing.modular.ModuleUpgrade;
import io.github.athingx.athing.thing.modular.ModuleUpgradeListener;
import io.github.athingx.athing.thing.modular.aliyun.domain.Meta;
import io.github.athingx.athing.thing.modular.aliyun.domain.Process;
import io.github.athingx.athing.thing.modular.aliyun.domain.Push;
import io.github.athingx.athing.thing.modular.aliyun.domain.Update;
import io.github.athingx.athing.thing.modular.aliyun.util.GsonUtils;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static io.github.athingx.athing.thing.modular.aliyun.UpgradeProcessor.Step.STEP_UPGRADES_COMPLETED;
import static io.github.athingx.athing.thing.modular.aliyun.UpgradeProcessor.Step.STEP_UPGRADES_FAILURE;
import static java.lang.String.format;

class ThingModularComImpl implements ThingModularCom, ThingLifeCycle {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonUtils.gson;
    private final ModularOption option;
    private final Set<ModuleUpgradeListener> listeners = new LinkedHashSet<>();

    @Inject
    private ThingRuntime runtime;

    public ThingModularComImpl(ModularOption option) {
        this.option = option;
    }

    @Override
    public ListenableFuture<Void> update(String moduleId, String version) {
        final Thing thing = runtime.getThing();
        final ThingLinker linker = runtime.getThingLinker();
        final String token = linker.generateToken();
        return linker.publish(format("/ota/device/inform/%s", thing.path()), new Update(token, moduleId, version))
                .onSuccess(v -> logger.info("{}/modular report version success, token={};modular={};version={};", thing, token, moduleId, version))
                .onFailure(e -> logger.warn("{}/modular report version failure, token={};modular={};version={};", thing, token, moduleId, version, e));
    }

    @Override
    public void appendListener(ModuleUpgradeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ModuleUpgradeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    // 模块升级通知到升级监听器
    private void upgradeModule(ModuleUpgrade upgrade) throws Exception {
        final Set<ModuleUpgradeListener> clones;
        synchronized (listeners) {
            clones = new LinkedHashSet<>(listeners);
        }
        for (final ModuleUpgradeListener listener : clones) {
            listener.upgrade(upgrade);
        }
    }

    @Override
    public void onLoaded(Thing thing) throws Exception {

        final ThingLinker linker = runtime.getThingLinker();
        final ThingExecutor executor = thing.getThingOp().getThingExecutor();

        // 订阅PUSH
        linker.subscribe(format("/ota/device/upgrade/%s", thing.path()), (topic, json) -> {

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

                // 执行模块升级动作
                upgradeModule(upgrade);
                logger.info("{}/modular upgrade success, module={};version={};", thing, meta.getModuleId(), meta.getVersion());

                // 报告升级成功结果
                processor.processing(STEP_UPGRADES_COMPLETED, "upgrade completed!");

                // 报告最新模块版本
                update(meta.getModuleId(), meta.getVersion());

            } catch (Exception cause) {
                logger.warn("{}/modular upgrade failure, modular={};version={};", thing, meta.getModuleId(), meta.getVersion(), cause);
                processor.processing(STEP_UPGRADES_FAILURE, format("upgrade failure: %s", cause.getLocalizedMessage()));
            }

        }).sync();

    }

    /**
     * 升级处理器实现，
     * 用于上报升级的进度
     */
    private class UpgradeProcessorImpl implements UpgradeProcessor {

        private final Meta meta;
        private volatile int current;

        private UpgradeProcessorImpl(Meta meta) {
            this.meta = meta;
        }

        @Override
        public synchronized void processing(int step, String desc) {

            // 如果当前步骤已经错误，或者已经大于上报步骤，则不执行上报
            if (current < 0 || current >= step) {
                return;
            }

            // 上报当前进度
            current = step;
            final Thing thing = runtime.getThing();
            final ThingLinker linker = runtime.getThingLinker();
            final String token = linker.generateToken();
            final String moduleId = meta.getModuleId();
            linker.publish(format("/ota/device/progress/%s/", thing.path()), new Process(token, moduleId, step, desc))
                    .onSuccess(v -> logger.debug("{}/modular report process success, token={};modular={};step={};", thing, token, moduleId, step))
                    .onFailure(e -> logger.debug("{}/modular report process failure, token={};modular={};step={};", thing, token, moduleId, step, e))
                    .awaitUninterruptible();

        }

    }

}
