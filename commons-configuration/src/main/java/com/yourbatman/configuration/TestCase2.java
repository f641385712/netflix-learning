package com.yourbatman.configuration;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.reloading.FileHandlerReloadingDetector;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingEvent;
import org.junit.Test;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 测试2.x
 */
public class TestCase2 {

    @Test
    public void fun1() throws ConfigurationException {
        Configurations configs = new Configurations();

        // 没有带参的构造器了，只能通过Configurations构建出来
        // Configuration configuration = new PropertiesConfiguration("1.properties");
        PropertiesConfiguration config = configs.properties("1.properties");
        // configs.xml();
        new SystemConfiguration();
        new EnvironmentConfiguration();

        System.out.println(config.getString("common.name"));
    }

    @Test
    public void fun2() throws ConfigurationException {
        Configurations configs = new Configurations();
        PropertiesConfiguration config = configs.properties("1.properties");

        // ConfigurationEvent内置有很多事件类型可使用
        // 若不满足条件，请你自定义事件类型
        config.addEventListener(ConfigurationEvent.ADD_PROPERTY, event -> {
            Object source = event.getSource(); // 事件源
            EventType<? extends Event> eventType = event.getEventType();
            if (!event.isBeforeUpdate()) {
                System.out.println("事件源：" + source.getClass());
                System.out.println("事件type类型：" + eventType);
            }
        });

        // 添加属性 触发事件
        config.addProperty("common.addition", "additionOne");
        System.out.println(config.getString("common.addition"));
    }

    @Test
    public void fun3() throws ConfigurationException {
        Configurations configs = new Configurations();

        // 此处用FileLocator定位到资源，因为后面监听还要使用
        // FileLocatorUtils.DEFAULT_LOCATION_STRATEGY -> 支持各种策略查找文件 从User Home、Classpath等等组合查找
        FileLocator fileLocator = FileLocatorUtils.fileLocator().fileName("1.properties").create();
        URL sourceUrl = FileLocatorUtils.locate(fileLocator);
        PropertiesConfiguration config = configs.properties(sourceUrl);
        // 上面效果同下，只不过一个手动，一个自动
        // PropertiesConfiguration config = configs.properties("1.properties");

        // 一定要告诉Detector它是要监控哪个文件才行，并且同步到哪个config文件里
        FileHandler fileHandler = new FileHandler(config);
        fileHandler.setFileLocator(FileLocatorUtils.fileLocator().sourceURL(sourceUrl).create());
        FileHandlerReloadingDetector detector = new FileHandlerReloadingDetector(fileHandler);

        // reloading逻辑的控制器
        ReloadingController reloadingController = new ReloadingController(detector);

        // 注册监听器：监听重新加载事件
        reloadingController.addEventListener(ReloadingEvent.ANY, event -> {
            EventType<? extends Event> eventType = event.getEventType();
            System.out.println("事件类型：" + eventType);
            System.out.println("配置文件重载...额外数据是：" + event.getData());
            ConfigurationUtils.dump(config, System.out);
            System.out.println("\n ================================ ");

            // 这个非常坑，还必须手动reset，不然第二次就不生效喽
            reloadingController.resetReloadingState();
        });

        // 检测
        otherThreadGet(config, reloadingController);
        // hold住main线程，不让程序终止
        while (true) {
        }

    }


    private void otherThreadGet(PropertiesConfiguration configuration, ReloadingController reloadingController) {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reloadingController.checkForReloading("这是额外数据，没有可为null");
                configuration.getString("commmon.name");
            }
        }).start();

    }

    @Test
    public void fun4() throws ConfigurationException {
        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
        builder.getFileHandler();


        PropertiesConfiguration configuration = builder.getConfiguration();

    }

}
