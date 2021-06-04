package com.github.athingx.athing.aliyun.modular.api;

/**
 * 模块升级监听器
 */
public interface ModuleUpgradeListener {

    /**
     * 模块升级
     *
     * @param token     令牌（平台推送）
     * @param upgrade   模块升级包
     * @param committer 提交器
     * @throws Exception 升级失败
     */
    void upgrade(String token, ModuleUpgrade upgrade, Committer committer) throws Exception;

}
