package com.github.athingx.athing.aliyun.modular.thing.boot;

import com.github.athingx.athing.aliyun.modular.thing.component.impl.ModularOption;
import com.github.athingx.athing.aliyun.modular.thing.component.impl.ModularThingComImpl;
import com.github.athingx.athing.standard.api.ThingCom;
import com.github.athingx.athing.standard.thing.boot.BootArguments;
import com.github.athingx.athing.standard.thing.boot.ThingComBoot;

import static com.github.athingx.athing.standard.thing.boot.BootArguments.Converter.cLong;

/**
 * 设备模块组件引导程序
 */
public class ModularThingComBoot implements ThingComBoot {

    @Override
    public ThingCom[] boot(String productId, String thingId, BootArguments arguments) {
        return new ThingCom[]{
                new ModularThingComImpl(toOption(arguments))
        };
    }

    private ModularOption toOption(BootArguments arguments) {
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
