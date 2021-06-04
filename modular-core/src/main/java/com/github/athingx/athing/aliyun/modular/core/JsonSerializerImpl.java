package com.github.athingx.athing.aliyun.modular.core;

import com.github.athingx.athing.aliyun.modular.core.util.GsonUtils;
import com.github.athingx.athing.aliyun.thing.runtime.messenger.JsonSerializer;

public class JsonSerializerImpl implements JsonSerializer {

    public static final JsonSerializer serializer = new JsonSerializerImpl();

    @Override
    public String toJson(Object object) {
        return GsonUtils.gson.toJson(object);
    }

}
