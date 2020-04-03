package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestFallback {

    @Test
    public void fun1() throws InterruptedException {
        // 10秒钟大于20个请求 失败数超过50%就触发熔断
        // 35个请求，还可以看到半开状态哦~~
        for (int i = 0; i < 45; i++) {
            String name = i % 2 == 0 ? null : "demo"; // 用于模拟50%的错误率
            FallabckDemo demo = new FallabckDemo(name);
            demo.execute();

            // 因为10秒内要至少放20个请求进去
            // 因为第一个请求先发出再休眠，所以此处取值500ms是没有问题的
            TimeUnit.MILLISECONDS.sleep(500);
        }

    }

    // 演示线程池不足
    @Test
    public void fun2() throws InterruptedException {
        // 10秒钟大于20个请求 失败数超过50%就触发熔断
        // 35个请求，还可以看到半开状态哦~~
        for (int i = 0; i < 45; i++) {
            String name = i % 2 == 0 ? null : "demo"; // 用于模拟50%的错误率
            FallabckDemo demo = new FallabckDemo(name);
            demo.queue();
        }

    }

    @Test
    public void fun3() {
        FallabckDemo demo = new FallabckDemo("name");
        String result = demo.execute();
        System.out.println(result);
    }


    private static class FallabckDemo extends HystrixCommand<String> {

        private final String name;

        public FallabckDemo(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("fallbackDemoGroup"));
            this.name = name;
        }

        @Override
        protected String run() throws InterruptedException {
            System.out.printf("健康信息：%s，断路器是否打开：%s\n", getMetrics().getHealthCounts(), circuitBreaker.isOpen());
            if (name == null) {
                throw new NullPointerException();
            }

            // TimeUnit.SECONDS.sleep(2);
            return "Hello " + name + "!";
        }

        @Override
        protected String getFallback() {
            Throwable e = getExecutionException(); // 导致目标方法执行失败的异常类型
            if (!(e instanceof NullPointerException)) {
                System.out.printf("异常类型：%s，信息：%s\n", e.getClass().getSimpleName(), e.getMessage());
            }
            return "this is fallback msg";
        }
    }
}

