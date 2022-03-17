package io.github.athingx.athing.thing.modular.aliyun;

import io.github.athingx.athing.standard.component.ThingCom;
import io.github.athingx.athing.standard.thing.boot.ThingBoot;
import io.github.athingx.athing.standard.thing.boot.ThingBootArgument;

import java.io.InputStream;
import java.util.Properties;

import static io.github.athingx.athing.standard.thing.boot.ThingBootArgument.Converter.cLong;


/**
 * 设备模块组件引导程序
 */
public class ThingModularBoot implements ThingBoot {

    @Override
    public ThingCom[] boot(String productId, String thingId, ThingBootArgument arguments) {
        return new ThingCom[]{
                new ThingModularComImpl(toOption(arguments))
        };
    }

    private ModularOption toOption(ThingBootArgument arguments) {
        final ModularOption option = new ModularOption();
        if(null != arguments) {
            arguments.optionArgument("timeout_ms", cLong, option::setTimeoutMs);
            arguments.optionArgument("connect_timeout_ms", cLong, option::setConnectTimeoutMs);
        }
        return option;
    }

    @Override
    public Properties getProperties() {
        return new Properties(){{
           put(PROP_GROUP, "io.github.athingx.athing.thing.modular");
           put(PROP_ARTIFACT, "thing-modular-aliyun");
        }};
    }

}
