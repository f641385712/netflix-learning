package com.yourbatman.hystrix;

import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;

public class MyHystrixMetricsPublisher extends HystrixMetricsPublisher {

    public MyHystrixMetricsPublisher() {
        System.out.println("MyHystrixMetricsPublisher被实例化了...");
    }
}
