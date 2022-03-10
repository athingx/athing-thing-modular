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
        if (arguments.hasArguments("timeout_ms")) {
            option.setTimeoutMs(arguments.getArgument("timeout_ms", cLong));
        }
        if (arguments.hasArguments("connect_timeout_ms")) {
            option.setConnectTimeoutMs(arguments.getArgument("connect_timeout_ms", cLong));
        }
        return option;
    }

    @Override
    public Properties getProperties() {
        final Properties prop = ThingBoot.super.getProperties();
        try (final InputStream in = ThingModularBoot.class.getResourceAsStream("/io/github/athingx/athing/thing/modular/aliyun/thing-boot.properties")) {
            if (null != in) {
                prop.load(in);
            }
        } catch (Exception cause) {
            // ignore...
        }
        return prop;
    }

}
