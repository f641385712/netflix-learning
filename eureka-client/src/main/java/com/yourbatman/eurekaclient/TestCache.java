package com.yourbatman.eurekaclient;

import com.netflix.discovery.util.StringCache;
import org.junit.Test;

public class TestCache {

    @Test
    public void fun1() {
        String str1 = new String("YoutBatman");
        String str2 = new String("YoutBatman");
        System.out.println(str1 == str2);


        StringCache cache = new StringCache();
        String cachedStr1 = cache.cachedValueOf(str1);
        String cachedStr2 = cache.cachedValueOf(str2);
        System.out.println(cachedStr1 == cachedStr2);
    }

    @Test
    public void fun2() {
        String appName = StringCache.intern("YoutBatman");
    }
}
