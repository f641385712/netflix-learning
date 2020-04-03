package com.yourbatman.eurekaclient;

import com.netflix.discovery.TimedSupervisorTask;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestExecutor {

    @Test
    public void fun1() {
        String mainThreadName = Thread.currentThread().getName();
        System.out.println("----------主线程[" + mainThreadName + "]开始----------");

        // 自己定义一个同步执行器
        Executor syncExecutor = (Runnable command) -> command.run();

        // 提交任务
        syncExecutor.execute(() -> {
            String currThreadName = Thread.currentThread().getName();
            System.out.println("线程[" + currThreadName + "] 我是同步执行的...");
        });
    }

    @Test
    public void fun2() throws InterruptedException {
        String mainThreadName = Thread.currentThread().getName();
        System.out.println("----------主线程[" + mainThreadName + "]开始----------");

        // 自己定义一个同步执行器
        Executor syncExecutor = (Runnable command) -> new Thread(command).start();

        // 提交任务
        syncExecutor.execute(() -> {
            String currThreadName = Thread.currentThread().getName();
            System.out.println("线程[" + currThreadName + "] 我是异步执行的...");
        });

        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void fun3() throws InterruptedException, ExecutionException {
        String mainThreadName = Thread.currentThread().getName();
        System.out.println("----------主线程[" + mainThreadName + "]开始----------");

        ExecutorService executorService = new MyExecutorService();

        Instant start = Instant.now();
        Future<?> submit = executorService.submit(createTask(3));
        System.out.println("结果为：" + submit.get());
        Instant end = Instant.now();
        System.out.println("总耗时为：" + Duration.between(start, end).getSeconds());

        executorService.shutdown();
    }

    // period：任务执行耗时 单位s
    private Runnable createTask(int period) {
        return () -> {
            try {
                TimeUnit.SECONDS.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String currThreadName = Thread.currentThread().getName();
            System.out.println("线程[" + currThreadName + "] 我是异步执行的，耗时" + period + "s");
        };
    }

    private static class MyExecutorService extends AbstractExecutorService {

        @Override
        public void shutdown() {
            System.out.println("关闭执行器，释放资源");
        }

        @Override
        public List<Runnable> shutdownNow() {
            System.out.println("立刻关闭执行器，释放资源");
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        // 执行任务（本处使用异步执行）
        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }

    @Test
    public void fun4() throws InterruptedException {
        String mainThreadName = Thread.currentThread().getName();
        System.out.println("----------主线程[" + mainThreadName + "]开始----------");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        TimedSupervisorTask supervisorTask = new TimedSupervisorTask("demo", scheduler, executor,
                2, TimeUnit.SECONDS, // 超时时间2s  任务默认2s执行1次
                6, // 最多只让翻倍12倍  最长延迟2 * 6 = 12s（也就是最长延迟12s）
                () -> {
                    System.out.println("执行任务。当前时间：" + LocalTime.now());
                    // 模拟每次执行都超时
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        scheduler.schedule(supervisorTask, 2L, TimeUnit.SECONDS);

        TimeUnit.MINUTES.sleep(5);
    }
}
