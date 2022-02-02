package io.github.athingx.athing.aliyun.modular;

/**
 * 模块升级监听器
 */
public interface ModuleUpgradeListener {

    /**
     * 模块升级
     *
     * @param upgrade   模块升级包
     * @throws Exception 升级失败
     */
    void upgrade(ModuleUpgrade upgrade) throws Exception;

}
