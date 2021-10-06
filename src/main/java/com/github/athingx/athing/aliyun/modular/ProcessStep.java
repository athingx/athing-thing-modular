package com.github.athingx.athing.aliyun.modular;

/**
 * 过程步骤
 * <pre>
 *     [0 , 50] : 下载升级包
 *     (50, 60] : 校验升级包
 *     (60, 70] : 烧录升级包
 *     (70, 00] : 升级完成
 * </pre>
 */
public interface ProcessStep {

    /**
     * 下载完成
     */
    int STEP_DOWNLOAD_COMPLETED = 50;

    /**
     * 校验完成
     */
    int STEP_CHECKSUM_COMPLETED = 60;

    /**
     * 烧录完成
     */
    int STEP_WRITINGS_COMPLETED = 70;

    /**
     * 升级完成
     */
    int STEP_UPGRADES_COMPLETED = 100;

    /**
     * 升级失败
     */
    int STEP_UPGRADES_FAILURE = -1;

    /**
     * 下载失败
     */
    int STEP_DOWNLOAD_FAILURE = -2;

    /**
     * 校验失败
     */
    int STEP_CHECKSUM_FAILURE = -3;

    /**
     * 烧录失败
     */
    int STEP_WRITINGS_FAILURE = -4;

}
