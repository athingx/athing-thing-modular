package io.github.athingx.athing.aliyun.modular.impl;

import io.github.athingx.athing.standard.component.ThingCom;
import io.github.athingx.athing.standard.thing.boot.ThingBoot;
import io.github.athingx.athing.standard.thing.boot.ThingBootArgument;

import java.io.InputStream;
import java.util.Properties;

import static io.github.athingx.athing.standard.thing.boot.ThingBootArgument.Converter.cLong;


/**
 * 设备模块组件引导程序
 */
public class ModularThingBoot implements ThingBoot {

    @Override
    public ThingCom[] boot(String productId, String thingId, ThingBootArgument arguments) {
        return new ThingCom[]{
                new ModularThingComImpl(toOption(arguments))
        };
    }

    private ModularOption toOption(ThingBootArgument arguments) {
        final ModularOption option = new ModularOption();
        if (arguments.hasArguments("timeout")) {
            option.setTimeoutMs(arguments.getArgument("timeout", cLong));
        }
        if (arguments.hasArguments("connect-timeout")) {
            option.setConnectTimeoutMs(arguments.getArgument("connect-timeout", cLong));
        }
        return option;
    }

    @Override
    public Properties getProperties() {
        final Properties prop = ThingBoot.super.getProperties();
        try (final InputStream in = ModularThingBoot.class.getResourceAsStream("/io/github/athingx/athing/aliyun/modular/impl/modular-thing-boot.properties")) {
            if (null != in) {
                prop.load(in);
            }
        } catch (Exception cause) {
            // ignore...
        }
        return prop;
    }

}
