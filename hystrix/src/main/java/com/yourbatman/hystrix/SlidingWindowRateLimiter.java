package com.yourbatman.hystrix;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滑动窗口算法限流器
 * 实现Runnable方法：用于控制滑动动作，重置桶的值以及总量值
 * 它的精髓就是在滑动
 *
 * @author yourbatman
 * @date 2020/3/1 22:17
 */
public class SlidingWindowRateLimiter implements RateLimiter, Runnable {

    // 每秒钟最多允许5个请求，这是默认值  你也可以通过构造器指定
    private static final int DEFAULT_ALLOWED_VISIT_PER_SECOND = 5;
    private final long maxVisitPerSecond;

    // 默认把1s划分为10个桶，这是默认值
    private static final int DEFAULT_BUCKET = 10;
    private final int bucket;

    // 每个桶对应的当前的请求数。数组长度和bucket数量一样
    // 桶是固定的大小。但是桶里面的内容会不断变化：因为会滑动
    private final AtomicInteger[] countPerBucket;

    // 总请求数
    private AtomicInteger count;
    private volatile int index;

    // 构造器
    public SlidingWindowRateLimiter() {
        this(DEFAULT_BUCKET, DEFAULT_ALLOWED_VISIT_PER_SECOND);
    }

    public SlidingWindowRateLimiter(int bucket, long maxVisitPerSecond) {
        this.bucket = bucket;
        this.maxVisitPerSecond = maxVisitPerSecond;
        countPerBucket = new AtomicInteger[bucket];
        for (int i = 0; i < bucket; i++)
            countPerBucket[i] = new AtomicInteger();
        count = new AtomicInteger(0);
    }


    // 是否超过限制：当前QPS总数是否超过了最大值（默认每秒5个嘛）
    // 注意：这里应该是>=。因为其实如果桶内访问数量已经等于5了，就应该限制住外面的再进来
    @Override
    public boolean isOverLimit() {
        return currentQPS() >= maxVisitPerSecond;
    }

    @Override
    public int currentQPS() {
        return count.get();
    }

    // 访问一次，次数+1（只要请求进来了就+1），并且告知是否超载
    // 请注意：放在指定的桶哦
    @Override
    public boolean visit() {
        countPerBucket[index].incrementAndGet();
        count.incrementAndGet();
        return isOverLimit();
    }


    // =========模拟线程访问=========
    @Override
    public void run() {
        System.out.println("~~~~~~~~~~~~~~~~~窗口向后滑动一下~~~~~~~~~~~~~~~~~");
        // 桶内的指针向前滑动一下：表示后面的visite请求应该打到下一个桶内了
        index = (index + 1) % bucket;
        // 初始化新桶。并且拿出旧值
        int val = countPerBucket[index].getAndSet(0);
        // 这个步骤一定不要忘了：因为废弃了一个桶，所以总值要减去~~~~
        if (val == 0L) { // 这个桶等于0，说明这个时刻没有流量进来
            System.out.println("~~~~~~~~~~~~~~~~~窗口没能释放出流量，继续保持限流~~~~~~~~~~~~~~~~~");

        } else {
            count.addAndGet(-val);
            System.out.println("~~~~~~~~~~~~~~~~~窗口释放出了[" + val + "]个访问名额，你可以访问了喽~~~~~~~~~~~~~~~~~");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter();
        // 使用一个线程滑动定时滑动这个窗口：100ms滑动一次（一般保持个桶的跨度保持一致）
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(rateLimiter, 100, 100, TimeUnit.MILLISECONDS);

        // 此处我使用单线程访问，你可以改造成多线程版本
        while (true) {
            String currThreadName = Thread.currentThread().getName();
            boolean overLimit = rateLimiter.isOverLimit();
            if (overLimit) {
                System.out.printf("线程[%s]====被限流了====，因为访问次数已超过阈值[%s]\n", currThreadName, rateLimiter.currentQPS());
            } else {
                rateLimiter.visit();
                System.out.printf("线程[%s]访问成功，当前访问总数[%s]\n", currThreadName, rateLimiter.currentQPS());
            }

            Thread.sleep(10);
        }
    }
}