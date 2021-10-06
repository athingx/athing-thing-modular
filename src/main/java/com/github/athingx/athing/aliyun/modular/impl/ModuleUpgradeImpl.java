package com.github.athingx.athing.aliyun.modular.impl;

import com.github.athingx.athing.aliyun.modular.ModuleUpgrade;
import com.github.athingx.athing.aliyun.modular.ProcessStep;
import com.github.athingx.athing.aliyun.modular.domain.Meta;
import com.github.athingx.athing.aliyun.modular.util.FileUtils;
import com.github.athingx.athing.aliyun.modular.util.HttpUtils;
import com.github.athingx.athing.standard.thing.op.executor.ThingExecutor;
import com.github.athingx.athing.standard.thing.op.executor.ThingFuture;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

/**
 * 模块升级包实现
 */
class ModuleUpgradeImpl implements ModuleUpgrade, ProcessStep {

    private final Meta meta;
    private final ModularOption option;
    private final ThingExecutor executor;
    private final AtomicReference<ThingFuture<File>> futureRef = new AtomicReference<>();

    public ModuleUpgradeImpl(Meta meta, ModularOption option, ThingExecutor executor) {
        this.meta = meta;
        this.option = option;
        this.executor = executor;
    }

    @Override
    public String getModuleId() {
        return meta.getModuleId();
    }

    @Override
    public String getUpgradeVersion() {
        return meta.getVersion();
    }

    @Override
    public long getUpgradeFileSize() {
        return meta.getSize();
    }


    /**
     * 下载升级包文件
     *
     * @return 升级包文件
     * @throws ModuleUpgradeProcessException 下载失败
     */
    private File download() throws ModuleUpgradeProcessException {
        try {

            // 创建临时文件
            final File file = File.createTempFile(
                    format("athing-modular_%s_%s",
                            meta.getModuleId(),
                            meta.getUpgradeMD5()
                    ),
                    ".push"
            );

            final AtomicInteger currentRef = new AtomicInteger(10);

            // 下载文件
            HttpUtils.download(
                    new URL(meta.getUpgradeURL()),
                    option.getConnectTimeoutMs(),
                    option.getTimeoutMs(),
                    file,
                    process -> {
                        final int step = process / 2;
                        final int current = currentRef.get();
                        if (step >= current) {
                            processing(current, "downloading...");
                            currentRef.set(current + 10);
                        }

                    });

            processing(STEP_DOWNLOAD_COMPLETED, "download completed!");
            return file;

        } catch (Exception cause) {
            throw new ModuleUpgradeProcessException(
                    getModuleId(),
                    STEP_DOWNLOAD_FAILURE,
                    cause
            );
        }
    }

    /**
     * 校验升级包文件
     *
     * @param file 升级包文件
     * @return 升级包文件
     * @throws ModuleUpgradeProcessException 校验失败
     */
    private File checksum(File file) throws ModuleUpgradeProcessException {
        try {
            final String expect = meta.getUpgradeCHS().toUpperCase();
            final String actual = FileUtils.md5(file).toUpperCase();
            if (!Objects.equals(expect, actual)) {
                throw new ModuleUpgradeProcessException(
                        getModuleId(),
                        STEP_CHECKSUM_FAILURE,
                        format("checksum failure, expect: %s but actual: %s", expect, actual)
                );
            }

            processing(STEP_CHECKSUM_COMPLETED, "checksum completed!");
            return file;
        } catch (ModuleUpgradeProcessException cause) {
            throw cause;
        } catch (Exception cause) {
            throw new ModuleUpgradeProcessException(
                    getModuleId(),
                    STEP_CHECKSUM_FAILURE,
                    cause
            );
        }
    }

    @Override
    public synchronized ThingFuture<File> getUpgradeFile() {

        if (futureRef.get() != null) {
            return futureRef.get();
        }

        futureRef.set(executor.submit(() -> checksum(download())));
        return futureRef.get();
    }

    /**
     * 升级进度报告
     *
     * @param step 步骤
     * @param desc 描述
     */
    protected void processing(int step, String desc) {

    }

}
