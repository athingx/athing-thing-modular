package com.github.athingx.athing.aliyun.modular.domain;

import com.google.gson.annotations.SerializedName;

public class Push {

    @SerializedName("id")
    private String token;

    @SerializedName("data")
    private Meta meta;

    public String getToken() {
        return token;
    }

    public Meta getMeta() {
        return meta;
    }
}
