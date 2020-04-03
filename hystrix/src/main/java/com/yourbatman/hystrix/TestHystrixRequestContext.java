package com.yourbatman.hystrix;

import com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestHystrixRequestContext {

    @Test
    public void fun1() {
        HystrixRequestContext.initializeContext();

        HystrixRequestContext contextForCurrentThread = HystrixRequestContext.getContextForCurrentThread();

        System.out.println(contextForCurrentThread.getClass());
        // contextForCurrentThread.close();
        contextForCurrentThread.shutdown();
    }


    @Test
    public void fun2() throws InterruptedException {
        HystrixRequestContext.initializeContext();


        // 启动子线程完成具体业务逻辑
        new Thread(() -> {
            // 子线程里需要拿到请求上下文处理逻辑
            HystrixRequestContext contextForCurrentThread = HystrixRequestContext.getContextForCurrentThread();
            // ... // 处理业务逻辑
            System.out.println("当前Hystrix请求上下文是：" + contextForCurrentThread);
        }).start();

        HystrixRequestContext.getContextForCurrentThread().close();
        TimeUnit.SECONDS.sleep(1);
    }

    private static final HystrixRequestVariableDefault<String> NAME_VARIABLE = new HystrixRequestVariableDefault<>();

    @Test
    public void fun3() throws InterruptedException {
        HystrixRequestContext.initializeContext();
        HystrixRequestContext mainContext = HystrixRequestContext.getContextForCurrentThread();
        // 设置变量：让其支持传递到子线程 or 线程池
        NAME_VARIABLE.set("YoutBatman");

        // 子线程的Runnable任务，必须使用`HystrixContextRunnable`才能得到上面设置的值哦
        new Thread(new HystrixContextRunnable(() -> {
            HystrixRequestContext contextForCurrentThread = HystrixRequestContext.getContextForCurrentThread();
            System.out.println(contextForCurrentThread == mainContext);
            System.out.println("当前线程绑定的变量值是：" + NAME_VARIABLE.get());
        })).start();

        TimeUnit.SECONDS.sleep(1);
        HystrixRequestContext.getContextForCurrentThread().close();
    }



    private static class MyThreadLocal<T> extends ThreadLocal<T>{



    }
}



