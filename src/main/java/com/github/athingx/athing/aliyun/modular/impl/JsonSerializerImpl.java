package com.github.athingx.athing.aliyun.modular.impl;

import com.github.athingx.athing.aliyun.modular.util.GsonUtils;
import com.github.athingx.athing.aliyun.thing.runtime.linker.JsonSerializer;

class JsonSerializerImpl implements JsonSerializer {

    public static final JsonSerializer serializer = new JsonSerializerImpl();

    @Override
    public String toJson(Object object) {
        return GsonUtils.gson.toJson(object);
    }

}
