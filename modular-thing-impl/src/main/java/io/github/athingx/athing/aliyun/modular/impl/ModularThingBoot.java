package io.github.athingx.athing.aliyun.modular.impl;

import io.github.athingx.athing.standard.component.ThingCom;
import io.github.athingx.athing.standard.thing.boot.ThingBoot;
import io.github.athingx.athing.standard.thing.boot.ThingBootArgument;

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

}
