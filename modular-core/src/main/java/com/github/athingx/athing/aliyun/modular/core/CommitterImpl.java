package com.github.athingx.athing.aliyun.modular.core;

import com.github.athingx.athing.aliyun.modular.api.Committer;
import com.github.athingx.athing.aliyun.modular.api.ProcessStep;
import com.github.athingx.athing.aliyun.modular.core.domain.Meta;
import com.github.athingx.athing.aliyun.thing.runtime.executor.ThingPromise;

/**
 * 升级提交器实现
 */
public class CommitterImpl implements Committer, ProcessStep {

    private final Meta meta;
    private final ThingPromise<Void> promise;

    public CommitterImpl(Meta meta, ThingPromise<Void> promise) {
        this.meta = meta;
        this.promise = promise;
    }

    @Override
    public boolean commit() {
        return promise.trySuccess();
    }

    @Override
    public boolean rollback(int step, String desc) {
        return promise.tryException(new ModuleUpgradeProcessException(
                meta.getModuleId(),
                step,
                desc
        ));
    }

    @Override
    public boolean rollback(int step, Throwable cause) {
        return promise.tryException(new ModuleUpgradeProcessException(
                meta.getModuleId(),
                step,
                cause
        ));
    }

}
