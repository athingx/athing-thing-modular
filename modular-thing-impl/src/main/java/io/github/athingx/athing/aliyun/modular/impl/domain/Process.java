package io.github.athingx.athing.aliyun.modular.impl.domain;

import com.google.gson.annotations.SerializedName;
import io.github.athingx.athing.aliyun.thing.runtime.linker.TokenData;

/**
 * 升级进度
 */
public class Process implements TokenData {

    @SerializedName("id")
    private final String token;

    @SerializedName("params")
    private final Param param;

    public Process(String token, String moduleId, int step, String desc) {
        this.token = token;
        this.param = new Param(moduleId, step, desc);
    }

    @Override
    public String getToken() {
        return token;
    }

    private static class Param {

        @SerializedName("module")
        private final String moduleId;

        @SerializedName("step")
        private final int step;

        @SerializedName("desc")
        private final String desc;

        public Param(String moduleId, int step, String desc) {
            this.moduleId = moduleId;
            this.step = step;
            this.desc = desc;
        }

    }

}
