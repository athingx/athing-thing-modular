package com.github.athingx.athing.aliyun.modular.boot;

import com.github.athingx.athing.aliyun.modular.core.ModularOption;
import com.github.athingx.athing.aliyun.modular.core.ModularThingComImpl;
import com.github.athingx.athing.standard.component.ThingCom;
import com.github.athingx.athing.standard.thing.boot.BootArguments;
import com.github.athingx.athing.standard.thing.boot.ThingComBoot;
import org.kohsuke.MetaInfServices;

import static com.github.athingx.athing.standard.thing.boot.BootArguments.Converter.cLong;

@MetaInfServices
public class BootImpl implements ThingComBoot {

    private static final String OPT_CONNECT_TIMEOUT = "connect-timeout";
    private static final String OPT_TIMEOUT = "timeout";

    @Override
    public ThingCom bootUp(String productId, String thingId, BootArguments arguments) {
        final ModularOption option = new ModularOption();

        // 下载升级包连接超时时间，默认：1分钟
        option.setConnectTimeoutMs(arguments.getArgument(OPT_CONNECT_TIMEOUT, cLong, 1000L * 60));

        // 下载升级包超时时间，默认：3分钟
        option.setTimeoutMs(arguments.getArgument(OPT_TIMEOUT, cLong, 3L * 1000 * 60));

        return new ModularThingComImpl(option);
    }

}
