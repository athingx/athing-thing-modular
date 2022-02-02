package io.github.athingx.athing.aliyun.modular.impl.domain;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.aliyun.thing.runtime.linker.TokenData;

/**
 * 更新模块版本
 */
public class Update implements TokenData {

    @SerializedName("id")
    private final String token;

    @SerializedName("params")
    private final Param param;

    public Update(String token, String moduleId, String version) {
        this.token = token;
        this.param = new Param(moduleId, version);
    }

    @Override
    public String getToken() {
        return token;
    }

    private static class Param {

        @SerializedName("module")
        private final String moduleId;

        @SerializedName("version")
        private final String version;

        private Param(String moduleId, String version) {
            this.moduleId = moduleId;
            this.version = version;
        }
    }

}
