package com.github.athingx.athing.aliyun.modular.api;

import com.github.athingx.athing.standard.thing.ThingFuture;

import java.io.File;

/**
 * 模块升级包
 */
public interface ModuleUpgrade {

    /**
     * 获取模块ID
     *
     * @return 模块ID
     */
    String getModuleId();

    /**
     * 获取升级版本
     *
     * @return 升级版本
     */
    String getUpgradeVersion();

    /**
     * 获取升级文件大小
     *
     * @return 升级文件大小
     */
    long getUpgradeFileSize();

    /**
     * 获取升级文件
     *
     * @return 获取文件凭证
     */
    ThingFuture<File> getUpgradeFile();

}
