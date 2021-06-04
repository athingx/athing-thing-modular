package com.github.athingx.athing.aliyun.modular.core.domain;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("id")
    private final String token;

    @SerializedName("params")
    private final Param param;

    public Post(String token, String moduleId, String version) {
        this.token = token;
        this.param = new Param(moduleId, version);
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
