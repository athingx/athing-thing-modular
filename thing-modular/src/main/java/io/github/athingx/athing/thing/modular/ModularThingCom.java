package io.github.athingx.athing.thing.modular;


import io.github.athingx.athing.standard.component.ThingCom;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;

/**
 * 设备模块组件
 */
public interface ModularThingCom extends ThingCom {

    /**
     * 更新模块版本
     *
     * @param moduleId 模块ID
     * @param version  模块版本（当前版本）
     * @return 更新Future
     */
    ListenableFuture<Void> update(String moduleId, String version);

    /**
     * 添加模块升级监听器
     *
     * @param listener 监听器
     */
    void appendListener(ModuleUpgradeListener listener);

    /**
     * 移除模块升级监听器
     *
     * @param listener 模块升级监听器
     */
    void removeListener(ModuleUpgradeListener listener);

}
