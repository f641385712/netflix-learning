package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class TestCommand {

    @Test
    public void fun1() throws InterruptedException {
        MyServiceCommand command = new MyServiceCommand(0);
        // String result = command.execute();
        Observable<String> observable = command.toObservable();
        observable.subscribe(d -> System.out.println(d));
        // System.out.println(result);
        TimeUnit.SECONDS.sleep(1);
    }
}


class MyServiceCommand extends HystrixCommand<String> {

    private int type;

    protected MyServiceCommand(int type) {
        super(HystrixCommandGroupKey.Factory.asKey("MyService"));
        this.type = type;
    }

    @Override
    protected String run() throws Exception {
        if (type == 0) { // 模拟程序运行时异常
            throw new NullPointerException("空指针异常");
        } else if (type == 1) { // 模拟抛出的是HystrixBadRequestException异常
            throw new HystrixBadRequestException("HystrixBadRequestException异常");
        } else if (type == 2) { // 模拟执行超时 3s
            TimeUnit.SECONDS.sleep(3);
        }
        return "YourBatman";
    }

    //com.netflix.hystrix.exception.HystrixRuntimeException: MyServiceCommand failed and no fallback available.
    @Override
    protected String getFallback() {
        System.out.println("fallabck回滚方法执行了...");
        return "this is fallabck msg";
    }
}