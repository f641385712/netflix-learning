package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.junit.Test;
import rx.Observable;

public class TestInvoke {

    @Test
    public void fun1() {
        MyCommand command = new MyCommand();
        Integer result = command.execute();
        System.out.println(result);
    }


    private static class MyCommand extends HystrixCommand<Integer> {

        protected MyCommand() {
            super(HystrixCommandGroupKey.Factory.asKey("MyCommandGroup"));
        }

        // 目标方法
        @Override
        protected Integer run() throws Exception {
            return 1;
        }

        // fallback方法逻辑，并不是必须提供的哦
        @Override
        protected Integer getFallback() {
            return -1;
        }
    }

    @Test
    public void fun2() {
        MyObservableCommand command = new MyObservableCommand();
        // Observable<Integer> observe = command.observe();
        Observable<Integer> observe = command.toObservable();

        System.out.println("==========订阅者之前的一句话==========");
        observe.subscribe(d -> System.out.println(d));
        System.out.println("==========订阅者之后的一句话==========");
    }


    private static class MyObservableCommand extends HystrixObservableCommand<Integer> {

        protected MyObservableCommand() {
            super(HystrixCommandGroupKey.Factory.asKey("MyCommandGroup"));
        }

        // 一次性发送5个返回值，持续不断
        @Override
        protected Observable<Integer> construct() {
            // return Observable.just(1, 2, 3, 4);
            return Observable.create(subscriber -> {
                subscriber.onStart();
                for (int i = 1; i <= 4; i++) {
                    System.out.println("开始发射数据：" + i);
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            });
        }

        @Override
        protected Observable<Integer> resumeWithFallback() {
            return Observable.just(-1);
        }
    }


}
