package io.github.athingx.athing.aliyun.modular.impl.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 元数据
 */
public class Meta {

    @SerializedName("size")
    private long size;

    @SerializedName("version")
    private String version;

    @SerializedName("url")
    private String upgradeURL;

    @SerializedName("md5")
    private String upgradeMD5;

    @SerializedName("sign")
    private String upgradeCHS;

    @SerializedName("module")
    private String moduleId;

    public long getSize() {
        return size;
    }

    public String getVersion() {
        return version;
    }

    public String getUpgradeURL() {
        return upgradeURL;
    }

    public String getUpgradeMD5() {
        return upgradeMD5;
    }

    public String getUpgradeCHS() {
        return upgradeCHS;
    }

    public String getModuleId() {
        return moduleId;
    }
}
