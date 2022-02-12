module io.github.athingx.athing.thing.modular.aliyun {

    requires io.github.athingx.athing.thing.modular;
    requires io.github.athingx.athing.aliyun.thing.runtime;
    requires org.slf4j;
    requires com.google.gson;
    requires metainf.services;

    exports io.github.athingx.athing.thing.modular.aliyun;

    opens io.github.athingx.athing.thing.modular.aliyun.domain to com.google.gson;
    provides io.github.athingx.athing.standard.thing.boot.ThingBoot with io.github.athingx.athing.thing.modular.aliyun.ModularThingBoot;

}