package com.yourbatman.eurekaclient;

import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.endpoint.EndpointUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestEndpoint {

    @Test
    public void fun1() {
        // 默认配置实现 是从Archaius管理的配置文件里读取配置
        DefaultEurekaClientConfig config = new DefaultEurekaClientConfig();
        List<String> serviceUrls = EndpointUtils.getServiceUrlsFromConfig(config, "zone1", true);
        System.out.println(serviceUrls);

        Map<String, List<String>> serviceUrlsMap = EndpointUtils.getServiceUrlsMapFromConfig(config, "zone1", true);
        System.out.println(serviceUrlsMap);
    }

    @Test
    public void fun2() throws InterruptedException {
        ScheduledExecutorService ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


        ScheduledExecutorService.schedule(() -> System.out.println("llllll"), 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(10);
    }
}
