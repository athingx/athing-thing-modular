package io.github.athingx.athing.thing.modular.aliyun;

import io.github.athingx.athing.standard.thing.boot.ThingBoot;
import io.github.athingx.athing.thing.modular.ModularThingCom;
import io.github.athingx.athing.thing.modular.ModuleUpgrade;
import io.github.athingx.athing.thing.modular.ModuleUpgradeListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ModularThingComTestCase extends ThingSupport {

    /**
     * 手动测试用，
     * 阿里云平台主动推送升级包
     *
     * @throws Exception 测试失败
     */
    @Test
    public void test$thing$modular$push_upgrade() throws Exception {

        final ModularThingCom component = thing.getUniqueThingCom(ModularThingCom.class);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<ModuleUpgrade> upgradeRef = new AtomicReference<>();

        final ModuleUpgradeListener listener = upgrade -> {
            upgrade.getFile().sync();
            upgradeRef.set(upgrade);
            latch.countDown();
        };

        component.appendListener(listener);

        try {
            component.update("resource", "1.0.0").sync();
            latch.await();

            final ModuleUpgrade upgrade = upgradeRef.get();
            Assert.assertEquals("resource", upgrade.getModuleId());
            Assert.assertEquals("1.0.1", upgrade.getVersion());
            Assert.assertTrue(upgrade.getFile().get().exists());
            Assert.assertTrue(upgrade.getFile().get().isFile());

        } finally {
            component.removeListener(listener);
        }

    }

    @Test
    public void test$thing$modular$boot() {
        final ThingBoot boot = new ModularThingBoot();
        Assert.assertEquals("${project.groupId}", boot.getProperties().getProperty(ThingBoot.PROP_GROUP));
        Assert.assertEquals("${project.artifactId}", boot.getProperties().getProperty(ThingBoot.PROP_ARTIFACT));
        Assert.assertEquals("${project.version}", boot.getProperties().getProperty(ThingBoot.PROP_VERSION));
        Assert.assertEquals("aliyun", boot.getProperties().getProperty(ThingBoot.PROP_PLATFORM_LIMIT));
    }

}
