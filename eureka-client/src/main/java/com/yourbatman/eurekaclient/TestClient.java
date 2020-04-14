package com.yourbatman.eurekaclient;

import com.netflix.discovery.shared.Applications;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClient {

    @Test
    public void fun1() {
        Map<String, AtomicInteger> map = new TreeMap<>();
        map.put("UP", new AtomicInteger(4));
        map.put("DOWN", new AtomicInteger(2));
        // map.put("OUT_OF_SERVICE", new AtomicInteger(1));

        System.out.println(Applications.getReconcileHashCode(map));
    }
}
