package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestMetrics {

    @Test
    public void fun1() throws InterruptedException {
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("CommandHelloWorld");
        HystrixCommandGroupKey commandGroupKey = HystrixCommandGroupKey.Factory.asKey("MyAppGroup");
        HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("MyAppGroup");
        HystrixPropertiesCommandDefault properties = new HystrixPropertiesCommandDefault(commandKey, HystrixCommandProperties.Setter());

        // command指标信息
        HystrixCommandMetrics commandMetrics = HystrixCommandMetrics.getInstance(commandKey, commandGroupKey, threadPoolKey, properties);

        // 发送事件（发送多次）
        CommandHelloWorld helloWorld = new CommandHelloWorld("YoutBatman");
        helloWorld.execute();
        helloWorld = new CommandHelloWorld("YoutBatman");
        helloWorld.queue();
        // 走fallabck
        helloWorld = new CommandHelloWorld(null);
        helloWorld.queue();


        // 打印指标信息
        TimeUnit.SECONDS.sleep(1); // 需要留给指标收集的时间
        System.out.println("===========commandMetrics信息===========");
        System.out.println(commandMetrics.getRollingCount(HystrixEventType.SUCCESS));
        System.out.println(commandMetrics.getRollingCount(HystrixEventType.FAILURE));
        System.out.println(commandMetrics.getRollingCount(HystrixEventType.FALLBACK_SUCCESS));

        System.out.println(commandMetrics.getCumulativeCount(HystrixEventType.SUCCESS));
        System.out.println(commandMetrics.getCumulativeCount(HystrixEventType.FAILURE));
        System.out.println(commandMetrics.getCumulativeCount(HystrixEventType.FALLBACK_SUCCESS));


        System.out.println(commandMetrics.getHealthCounts());
        System.out.println(commandMetrics.getExecutionTimeMean());
    }
}
