package com.yourbatman.eurekaclient;

import com.netflix.discovery.util.RateLimiter;
import org.junit.Test;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class TestRateLimiter {

    @Test
    public void fun1() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(TimeUnit.SECONDS);

        // 桶里最多放5个令牌
        // 放令牌的速率是 5个/秒，这样放满仅需1秒（没有消耗的情况下）
        int reqCount = 1;
        while (true) {
            boolean acquire = limiter.acquire(5, 5);
            if (acquire) {
                System.out.println(reqCount++ + "【成功】 -- " + LocalTime.now());
            } else {
                System.out.println(reqCount++ + "失败 -- " + LocalTime.now());
            }

            // 这样子差不多1s内能够发送10次请求
            // 请求的速率是每秒10个请求，是你放的2倍
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

}
