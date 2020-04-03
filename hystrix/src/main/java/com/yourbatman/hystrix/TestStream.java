package com.yourbatman.hystrix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.config.HystrixConfigurationStream;
import com.netflix.hystrix.metric.consumer.CumulativeCommandEventCounterStream;
import com.netflix.hystrix.metric.consumer.HealthCountsStream;
import com.netflix.hystrix.metric.consumer.RollingCommandEventCounterStream;
import com.netflix.hystrix.metric.consumer.RollingCommandMaxConcurrencyStream;
import com.netflix.hystrix.metric.sample.HystrixUtilizationStream;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestStream {

    @Test
    public void fun1() throws InterruptedException {
        // 查看command、线程池的使用情况
        HystrixUtilizationStream utilizationStream = HystrixUtilizationStream.getInstance();
        // utilizationStream.observeThreadPoolUtilization()
        utilizationStream.observe().subscribe(d -> System.out.println(toJsonString(d)));

        // 查看配置情况
        HystrixConfigurationStream configStream = HystrixConfigurationStream.getInstance();
        configStream.observe().subscribe(d -> {
            System.out.println(d);
        });

        // 累计统计流
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("CommandHelloWorld");
        HystrixPropertiesCommandDefault properties = new HystrixPropertiesCommandDefault(commandKey, HystrixCommandProperties.Setter());
        CumulativeCommandEventCounterStream counterStream = CumulativeCommandEventCounterStream.getInstance(commandKey, properties);
        counterStream.observe().subscribe(d -> System.out.println(toJsonString(d)));

        // 最大并发流
        RollingCommandMaxConcurrencyStream concurrencyStream = RollingCommandMaxConcurrencyStream.getInstance(commandKey, properties);
        concurrencyStream.observe().subscribe(d -> System.out.println(toJsonString(d)));


        // RollingCommandEventCounterStream commandEventCounterStream = RollingCommandEventCounterStream.getInstance(commandKey, properties);
        // commandEventCounterStream.observe().subscribe(d -> System.out.println(toJsonString(d)));
        HealthCountsStream healthCountsStream = HealthCountsStream.getInstance(commandKey, properties);
        healthCountsStream.observe().subscribe(d -> System.out.println(toJsonString(d)));

        // 发送事件（发送多次）
        CommandHelloWorld helloWorld = new CommandHelloWorld("YoutBatman");
        helloWorld.execute();

        helloWorld = new CommandHelloWorld("YoutBatman");
        helloWorld.queue();

        // 走fallabck
        helloWorld = new CommandHelloWorld(null);
        helloWorld.queue();


        // 因为配置5秒钟才能打印一次
        TimeUnit.SECONDS.sleep(5);

    }

    private static final String toJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
