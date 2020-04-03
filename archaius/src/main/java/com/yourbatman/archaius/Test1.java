package com.yourbatman.archaius;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.DynamicURLConfiguration;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PollListener;
import com.netflix.config.WatchedConfigurationSource;
import com.netflix.config.WatchedUpdateListener;
import com.netflix.config.WatchedUpdateResult;
import com.netflix.config.sources.URLConfigurationSource;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Test1 {

    @Test
    public void fun1() {
        DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();
        DynamicStringProperty nameProperty = propertyFactory.getStringProperty("name", "defaultName");
        System.out.println(nameProperty.get());
    }

    @Test
    public void fun2() throws InterruptedException {
        DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


        DynamicStringProperty nameProperty = propertyFactory.getStringProperty("name", "defaultName");
        nameProperty.addCallback(() -> System.out.println("name属性值发生变化："));

        // 10秒钟读一次
        while (true) {
            System.out.println(nameProperty.get());
            TimeUnit.SECONDS.sleep(50);
        }
    }

    @Test
    public void fun3() throws InterruptedException {
        PropertiesConfiguration config = new PropertiesConfiguration();

        // 开启轮询，只有文件内容有变化就实时同步
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(3000, 5000, false);
        scheduler.startPolling(new URLConfigurationSource(), config);
        scheduler.addPollListener((eventType, result, exception) -> {
            if (eventType == PollListener.EventType.POLL_SUCCESS) {
                System.out.println("线程名称：" + Thread.currentThread().getName());

                System.out.println("新增属性们：" + result.getAdded());
                System.out.println("删除属性们：" + result.getDeleted());
                System.out.println("修改属性们：" + result.getChanged());
                System.out.println("完成属性们：" + result.getComplete());
            }
        });

        while (true) {
            ConfigurationUtils.dump(config, System.out);
            System.out.println();
            TimeUnit.SECONDS.sleep(10);
        }
    }

    @Test
    public void fun4() throws InterruptedException {
        DynamicURLConfiguration config = new DynamicURLConfiguration();

        while (true) {
            ConfigurationUtils.dump(config, System.out);
            System.out.println();
            TimeUnit.SECONDS.sleep(10);
        }
    }

    @Test
    public void fun5() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "YourBatman");

        DynamicWatchedConfiguration config = new DynamicWatchedConfiguration(new MyWatchedConfigurationSource(data));
        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n");

        // 改变属性，会发现底层的Map也会改
        data.put("age", 18);
        config.updateConfiguration(WatchedUpdateResult.createFull(data));
        ConfigurationUtils.dump(config, System.out);
    }

    private static class MyWatchedConfigurationSource implements WatchedConfigurationSource {

        private final Map<String, Object> data;
        private final List<WatchedUpdateListener> listeners;

        public MyWatchedConfigurationSource(Map<String, Object> data) {
            this.data = data;
            listeners = new CopyOnWriteArrayList<>();
        }

        @Override
        public void addUpdateListener(WatchedUpdateListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeUpdateListener(WatchedUpdateListener listener) {
            listeners.remove(listener);
        }

        @Override
        public Map<String, Object> getCurrentData() throws Exception {
            return data;
        }
    }

}
