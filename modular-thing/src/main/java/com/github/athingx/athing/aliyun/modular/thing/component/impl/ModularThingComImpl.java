package com.github.athingx.athing.aliyun.modular.thing.component.impl;

import com.github.athingx.athing.aliyun.modular.thing.component.ModularThingCom;
import com.github.athingx.athing.aliyun.modular.thing.component.ModuleUpgradeListener;
import com.github.athingx.athing.aliyun.modular.thing.component.ProcessStep;
import com.github.athingx.athing.aliyun.modular.thing.component.impl.domain.Meta;
import com.github.athingx.athing.aliyun.modular.thing.component.impl.domain.Post;
import com.github.athingx.athing.aliyun.modular.thing.component.impl.domain.Push;
import com.github.athingx.athing.aliyun.modular.thing.component.impl.domain.Process;
import com.github.athingx.athing.aliyun.modular.thing.util.GsonUtils;
import com.github.athingx.athing.aliyun.thing.runtime.ThingRuntime;
import com.github.athingx.athing.aliyun.thing.runtime.ThingRuntimes;
import com.github.athingx.athing.aliyun.thing.runtime.linker.ThingLinker;
import com.github.athingx.athing.aliyun.thing.runtime.mqtt.ThingMqtt;
import com.github.athingx.athing.standard.thing.Thing;
import com.github.athingx.athing.standard.thing.boot.Initializing;
import com.github.athingx.athing.standard.thing.op.executor.ThingExecutor;
import com.github.athingx.athing.standard.thing.op.executor.ThingFuture;
import com.github.athingx.athing.standard.thing.op.executor.ThingPromise;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.athingx.athing.aliyun.modular.thing.component.impl.JsonSerializerImpl.serializer;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 默认设备模块组件
 */
public class ModularThingComImpl implements ModularThingCom, Initializing, ProcessStep {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonUtils.gson;
    private final ModularOption option;

    private Thing thing;
    private ThingMqtt mqtt;
    private ThingExecutor executor;
    private ThingLinker linker;
    private volatile ModuleUpgradeListener listener;

    /**
     * 默认设备模块组件
     *
     * @param option 模块选项
     */
    public ModularThingComImpl(ModularOption option) {
        this.option = option;
    }

    @Override
    public ThingFuture<Void> post(String moduleId, String version) {
        return linker.post(
                serializer,
                String.format("/ota/device/inform/%s/%s", thing.getProductId(), thing.getThingId()),
                token -> new Post(token, moduleId, version)
        );
    }

    @Override
    public void setModuleUpgradeListener(ModuleUpgradeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onInitialized(Thing thing) throws Exception {
        final ThingRuntime runtime = ThingRuntimes.getThingRuntime(thing);
        this.thing = thing;
        this.mqtt = runtime.getThingMqtt();
        this.linker = runtime.getThingLinker();
        this.executor = thing.getThingOp().getThingExecutor();

        logger.info("{}/modular init completed, connect-timeout={};timeout={};",
                thing,
                option.getConnectTimeoutMs(),
                option.getTimeoutMs()
        );

        // 订阅PUSH
        subscribePush().sync();

    }

    /**
     * 报告升级进度
     *
     * @param meta  元数据
     * @param cause 升级异常
     */
    private void postUpgradeProcess(Meta meta, Throwable cause) {

        // 如果是明确的模块升级进度异常，则采用异常的信息回馈
        if (cause instanceof ModuleUpgradeProcessException) {
            final ModuleUpgradeProcessException mupCause = (ModuleUpgradeProcessException) cause;
            postUpgradeProcess(meta, mupCause.getStep(), mupCause.getDesc());
            return;
        }

        // 如果是其他为止异常，则按升级失败处理
        postUpgradeProcess(meta, STEP_UPGRADES_FAILURE, "upgrade failure!");

    }

    /**
     * 报告升级进度
     *
     * @param meta 元数据
     * @param step 步骤
     * @param desc 描述
     */
    private void postUpgradeProcess(Meta meta, int step, String desc) {
        linker.post(
                serializer,
                String.format("/ota/device/progress/%s/%s", thing.getProductId(), thing.getThingId()),
                token -> new Process(token, meta.getModuleId(), step, desc)
        ).awaitUninterruptible();
    }

    /**
     * 订阅模块升级PUSH
     *
     * @return 订阅存根
     */
    private ThingFuture<Void> subscribePush() {
        return mqtt.subscribe(String.format("/ota/device/upgrade/%s/%s", thing.getProductId(), thing.getThingId()), (topic, message) -> {

            final Push push = gson.fromJson(message.getStringData(UTF_8), Push.class);
            final Meta meta = push.getMeta();

            // 模块升级承诺
            final ThingPromise<Void> upgradeP = executor.promise(promise ->
                    promise.self()
                            .onFailure(future -> {
                                logger.warn("{}/modular/{} upgrade failure!", thing, meta.getModuleId(), future.getException());
                                postUpgradeProcess(meta, future.getException());
                            })
                            .onSuccess(future -> {
                                logger.info("{}/modular/{} upgrade success!", thing, meta.getModuleId());
                                postUpgradeProcess(meta, STEP_UPGRADES_COMPLETED, "upgrade completed!");
                                post(meta.getModuleId(), meta.getVersion());
                            }));

            // 模块升级履约
            executor.promise(upgradeP, promise -> {
                // 如果没有配置监听器，则升级失败
                final ModuleUpgradeListener listener = this.listener;
                if (null == listener) {
                    throw new ModuleUpgradeProcessException(
                            meta.getModuleId(),
                            STEP_UPGRADES_FAILURE,
                            "not upgradeable, none upgrade listener found!"
                    );
                }

                // 开始升级
                listener.upgrade(
                        push.getToken(),
                        new ModuleUpgradeImpl(meta, option, executor) {

                            @Override
                            protected void processing(int step, String desc) {
                                postUpgradeProcess(meta, step, desc);
                            }

                        },
                        new CommitterImpl(meta, promise)
                );
            });

        });
    }

}
