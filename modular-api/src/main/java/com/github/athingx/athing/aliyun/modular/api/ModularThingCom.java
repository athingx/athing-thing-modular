package com.github.athingx.athing.aliyun.modular.api;

import com.github.athingx.athing.standard.component.ThingCom;
import com.github.athingx.athing.standard.thing.ThingFuture;

/**
 * 设备模块组件
 */
public interface ModularThingCom extends ThingCom {

    /**
     * 报告模块版本
     *
     * @param moduleId 模块ID
     * @param version  模块版本（当前版本）
     * @return 报告存根
     */
    ThingFuture<Void> post(String moduleId, String version);

    /**
     * 设置模块升级监听器
     *
     * @param listener 监听器
     */
    void setModuleUpgradeListener(ModuleUpgradeListener listener);

}
