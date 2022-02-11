package io.github.athingx.athing.thing.modular.aliyun;

import io.github.athingx.athing.thing.modular.ModuleUpgrade;
import io.github.athingx.athing.thing.modular.aliyun.domain.Meta;
import io.github.athingx.athing.thing.modular.aliyun.util.FileUtils;
import io.github.athingx.athing.thing.modular.aliyun.util.HttpUtils;
import io.github.oldmanpushcart.jpromisor.ListenableFuture;
import io.github.oldmanpushcart.jpromisor.Promisor;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

/**
 * 模块升级包实现
 */
class ModuleUpgradeImpl implements ModuleUpgrade, UpgradeProcessor.Step {

    private final Meta meta;
    private final ModularOption option;
    private final Executor executor;
    private final UpgradeProcessor processor;
    private final AtomicReference<ListenableFuture<File>> futureRef = new AtomicReference<>();

    public ModuleUpgradeImpl(Meta meta, ModularOption option, Executor executor, UpgradeProcessor processor) {
        this.meta = meta;
        this.option = option;
        this.executor = executor;
        this.processor = processor;
    }

    public ModuleUpgradeImpl(Meta meta, ModularOption option, Executor executor) {
        this(meta, option, executor, (step, desc) -> {

        });
    }

    @Override
    public String getModuleId() {
        return meta.getModuleId();
    }

    @Override
    public String getVersion() {
        return meta.getVersion();
    }

    @Override
    public long getFileSize() {
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
                    String.format("athing-modular_%s_%s",
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
                            this.processor.processing(current, "downloading...");
                            currentRef.set(current + 10);
                        }

                    });

            processor.processing(STEP_DOWNLOAD_COMPLETED, "download completed!");
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

            processor.processing(STEP_CHECKSUM_COMPLETED, "checksum completed!");
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
    public synchronized ListenableFuture<File> getFile() {
        if (futureRef.get() != null) {
            return futureRef.get();
        }

        final ListenableFuture<File> future = new Promisor()
                .fulfill(executor, () -> {
                    return checksum(download());
                })
                .onFailure(cause -> {
                    if (cause instanceof final ModuleUpgradeProcessException mupE) {
                        processor.processing(mupE.getStep(), mupE.getDesc());
                    } else {
                        processor.processing(STEP_DOWNLOAD_FAILURE, cause.getLocalizedMessage());
                    }
                });

        futureRef.set(future);
        return future;
    }

}
