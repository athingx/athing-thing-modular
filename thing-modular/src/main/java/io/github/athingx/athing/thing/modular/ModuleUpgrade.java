package io.github.athingx.athing.thing.modular;

import io.github.oldmanpushcart.jpromisor.ListenableFuture;

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
    String getVersion();

    /**
     * 获取升级文件大小
     *
     * @return 升级文件大小
     */
    long getFileSize();

    /**
     * 获取升级文件
     *
     * @return 获取文件凭证
     */
    ListenableFuture<File> getFile();

}
