package com.github.athingx.athing.aliyun.modular.core;

/**
 * 模块组件选项
 */
public class ModularOption {

    /**
     * 下载升级文件连接超时时间
     */
    private long connectTimeoutMs;

    /**
     * 下载升级文件超时时间
     */
    private long timeoutMs;

    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

}
