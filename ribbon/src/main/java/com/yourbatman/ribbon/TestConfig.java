package com.yourbatman.ribbon;

import com.netflix.client.SimpleVipAddressResolver;
import com.netflix.client.VipAddressResolver;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.client.util.Resources;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.utils.ScheduledThreadPoolExectuorWithDynamicSize;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestConfig {

    @Test
    public void fun1() {
        IClientConfigKey key1 = CommonClientConfigKey.valueOf("YourBatman");
        IClientConfigKey key2 = CommonClientConfigKey.valueOf("YourBatman");
        System.out.println(key1.key());
        System.out.println(key1 == key2);
    }

    // 打印所有的配置的默认值
    @Test
    public void fun2() throws Exception {
        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
        // clientConfig.loadDefaultValues();
        clientConfig.loadProperties("account");

        // 打印所有的默认值键值对
        Field field = DefaultClientConfigImpl.class.getDeclaredField("properties");
        field.setAccessible(true);
        Map<String, Object> defaultProperties = (Map<String, Object>) field.get(clientConfig);
        System.out.println("共" + defaultProperties.size() + "个默认配置项，默认值如下：");
        defaultProperties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    // IClientConfig配置管理的使用
    @Test
    public void fun3() {
        IClientConfig.Builder.newBuilder().build();

    }


    @Test
    public void fun4() {
        // 准备配置对象IClientConfig
        // IClientConfig config = new DefaultClientConfigImpl();
        // config.set(CommonClientConfigKey.valueOf("foo"), "YourBatman");
        // config.set(CommonClientConfigKey.valueOf("port"), 80);
        // config.set(CommonClientConfigKey.valueOf("foobar"), "Jay");

        Configuration configuration = ConfigurationManager.getConfigInstance();
        configuration.setProperty("foo", "YourBatman");
        // configuration.setProperty("port",80); // 这样报错，必须是字符串，尴尬
        configuration.setProperty("port", "80");
        configuration.setProperty("foobar", "Jay");

        String vipArr = "${foo}.bar:${port},${foobar}:80,localhost:8080";
        VipAddressResolver vipAddressResolver = new SimpleVipAddressResolver();
        String vipAddredd = vipAddressResolver.resolve(vipArr, null);
        System.out.println(vipAddredd);
    }


    @Test
    public void fun5() {
        // 静态方法构建
        IClientConfig config = DefaultClientConfigImpl.getClientConfigWithDefaultValues("YourBatman");
        System.out.println(config.getClientName());
        System.out.println(config.get(CommonClientConfigKey.ConnectTimeout));
        System.out.println("-----------------------");
        // Builder构建
        config = IClientConfig.Builder.newBuilder("YourBatman")
                .withConnectTimeout(8000)
                .withReadTimeout(10000)
                .build();
        System.out.println(config.getClientName());
        System.out.println(config.get(CommonClientConfigKey.ConnectTimeout));
        System.out.println("-----------------------");

        // new方式构建
        config = new DefaultClientConfigImpl();
        System.out.println("load前：");
        System.out.println(config.getClientName());
        System.out.println(config.get(CommonClientConfigKey.ConnectTimeout));

        config.loadProperties("YourBatman");
        System.out.println("load后：");
        System.out.println(config.getClientName());
        System.out.println(config.get(CommonClientConfigKey.ConnectTimeout));
    }

    @Test
    public void fun6() throws InterruptedException {
        // =========准备一个动态属性==========
        DynamicIntProperty poolCoreSize = DynamicPropertyFactory.getInstance().getIntProperty("myThreadPoolCoreSize", 2);
        ThreadFactory threadFactory = new ThreadFactory() {
            private AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("myThreadPrefix-" + count.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        };

        ScheduledThreadPoolExectuorWithDynamicSize exectuor = new ScheduledThreadPoolExectuorWithDynamicSize(poolCoreSize, threadFactory);

        // 启动3个定时任务（默认coreSize是2哦）
        for (int i = 1; i <= 3; i++) {
            int index = i;
            exectuor.scheduleAtFixedRate(() -> {
                String currThreadName = Thread.currentThread().getName();
                int corePoolSize = exectuor.getCorePoolSize();
                System.out.printf("我是%s号任务，线程名是[%s]，线程池核心数是：%s\n", index, currThreadName, corePoolSize);
            }, index, 3, TimeUnit.SECONDS);
        }

        // 阻塞主线程
        TimeUnit.MINUTES.sleep(100);
    }


    @Test
    public void fun7(){
        URL resource = Resources.getResource("config.properties");
        System.out.println(resource);
    }
}
