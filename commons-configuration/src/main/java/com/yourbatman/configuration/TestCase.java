package com.yourbatman.configuration;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommonsLog
public class TestCase {

    @Test
    public void fun1() throws ConfigurationException {
        Configuration configuration = new PropertiesConfiguration("1.properties");
        // PropertiesConfiguration configuration = new PropertiesConfiguration("1.properties");

        System.out.println(configuration.getString("common.name"));
        System.out.println(configuration.getString("common.fullname"));
        System.out.println(configuration.getInt("common.age"));
        System.out.println(configuration.getString("common.addr"));
        System.out.println(configuration.getLong("common.count"));

        // 打印include的内容
        System.out.println(configuration.getString("java.version"));

        System.out.println();
        System.out.println("=====使用subset方法得到一个子配置类=====");
        Configuration subConfig = configuration.subset("common");
        subConfig.getKeys().forEachRemaining((k) -> {
            System.out.println(k + "-->" + subConfig.getString(k));
        });
    }

    @Test
    public void fun2() {
        Map<String, Object> source = new HashMap<>();
        source.put("common.name", "YourBatman");
        source.put("common.age", 18);

        Configuration configuration = new MapConfiguration(source);
        System.out.println(configuration.getString("common.name"));
        System.out.println(configuration.getInt("common.age"));
    }


    @Test
    public void fun3() {
        Configuration configuration = new SystemConfiguration();
        System.out.println(configuration.getString("user.home"));
    }

    @Test
    public void fun4() throws ConfigurationException {
        PropertiesConfiguration configuration = new PropertiesConfiguration("1.properties");
        // 注册一个监听器
        configuration.addConfigurationListener(event -> {
            Object source = event.getSource(); // 事件源
            int type = event.getType();
            if (!event.isBeforeUpdate()) { // 只关心update后的事件，否则会执行两次哦，请务必注意
                System.out.println("事件源：" + source.getClass());
                System.out.println("事件type类型：" + type);

                // 处理你自己的逻辑
            }
        });

        // 增加一个属性，会同步触发监听器去执行
        configuration.addProperty("common.addition", "additionOne");
        System.out.println(configuration.getString("common.addition"));
    }

    @Test
    public void fun5() throws ConfigurationException {
        PropertiesConfiguration configuration = new PropertiesConfiguration("1.properties");

        // 监听到配置文件被重新加载了就输出一条日志喽~
        configuration.addConfigurationListener(event -> {
            // 只监听到重新加载事件
            if (event.getType() == PropertiesConfiguration.EVENT_RELOAD) {
                System.out.println("配置文件重载...");
                configuration.getKeys().forEachRemaining(k -> {
                    System.out.println("/t " + k + "-->" + configuration.getString(k));
                });
            }
        });

        // 使用文件改变重载策略：让改变文件能热加载
        FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
        reloadingStrategy.setRefreshDelay(3000L); // 设置最小事件间隔，单位是毫秒
        configuration.setReloadingStrategy(reloadingStrategy);

        // 使用另外一个线程模拟去get
        otherThreadGet(configuration);

        // hold住main线程，不让程序终止
        while (true) {
        }
    }

    private void otherThreadGet(PropertiesConfiguration configuration) {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                configuration.getString("commmon.name");
            }
        }).start();

    }


    @Test
    public void fun6() throws ConfigurationException {
        Configuration configuration = new PropertiesConfiguration("1.properties");

        System.out.println(configuration.getProperty("common.fullname"));
        System.out.println(configuration.getString("common.fullname"));
        System.out.println(" ================ ");

        ConfigurationUtils.dump(configuration, System.out);
        // System.out.println(ConfigurationUtils.toString(configuration));
    }


    @Test
    public void fun7() {
        URL url = ConfigurationUtils.locate("userHome.properties");
        System.out.println(url.toString());
    }

}
