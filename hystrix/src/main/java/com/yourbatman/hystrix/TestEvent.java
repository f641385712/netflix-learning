package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.metric.HystrixCommandExecutionStarted;
import com.netflix.hystrix.metric.HystrixCommandStartStream;
import org.junit.Test;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class TestEvent {

    @Test
    public void fun1() {
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("demo");
        HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("demoThreadPool");
        HystrixCommandProperties.ExecutionIsolationStrategy isolationStrategy = HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

        HystrixCommandStartStream startStream = HystrixCommandStartStream.getInstance(commandKey);

        // 注册监听者
        startStream.observe()
                // .subscribeOn(Schedulers.io())
                // .observeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<HystrixCommandExecutionStarted>() {

                    @Override
                    public void onCompleted() {
                        System.out.println("数据发射完成啦");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("数据发射出错啦：" + e.getMessage());
                    }

                    @Override
                    public void onNext(HystrixCommandExecutionStarted hystrixCommand) {
                        System.out.printf("线程[%s] 数据发射start：%s %s %s %s",
                                Thread.currentThread().getName(),
                                hystrixCommand.getCommandKey(),
                                hystrixCommand.getThreadPoolKey(),
                                hystrixCommand.isExecutedInThread(),
                                hystrixCommand.getCurrentConcurrency());
                    }
                });


        // 写数据：会马上发射出去
        startStream.write(new HystrixCommandExecutionStarted(commandKey, threadPoolKey, isolationStrategy, 6));
    }

}
