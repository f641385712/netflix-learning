package com.yourbatman.hystrix;

/**
 * 限流器
 *
 * @author yourbatman
 * @date 2020/3/1 22:16
 */
public interface RateLimiter {

    // 是否要限流
    boolean isOverLimit();

    // 当前QPS总数值（也就是窗口期内的访问总量）
    int currentQPS();

    // touch一下：增加一次访问量
    boolean visit();
}