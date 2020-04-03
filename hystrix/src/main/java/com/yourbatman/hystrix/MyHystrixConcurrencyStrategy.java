package com.yourbatman.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new MyCallable<>(callable, null);
    }


    private static class MyCallable<T> implements Callable<T>{

        private final Callable<T> delegate;
        private final Map<String, String> contextMap;

        public MyCallable(Callable<T> delegate, Map<String, String> contextMap) {
            this.delegate = delegate;
            this.contextMap = contextMap != null ? contextMap : new HashMap();
        }

        @Override
        public T call() throws Exception {

            try {
                return delegate.call();
            }finally {

            }
        }
    }
}
