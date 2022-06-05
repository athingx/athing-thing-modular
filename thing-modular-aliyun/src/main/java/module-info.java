module io.github.athingx.athing.thing.modular.aliyun {

    requires transitive io.github.athingx.athing.thing.modular;
    requires io.github.athingx.athing.aliyun.thing.runtime;
    requires com.google.gson;
    requires org.slf4j;

    exports io.github.athingx.athing.thing.modular.aliyun;

    opens io.github.athingx.athing.thing.modular.aliyun.domain to com.google.gson;

}