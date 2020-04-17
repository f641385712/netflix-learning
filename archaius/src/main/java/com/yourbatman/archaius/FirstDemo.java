package com.yourbatman.archaius;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FirstDemo {

    @Test
    public void fun1() throws InterruptedException {
        DynamicIntProperty myAge = DynamicPropertyFactory.getInstance().getIntProperty("my.age", 18);
        System.out.println(myAge);
        System.out.println(myAge.get());

        TimeUnit.SECONDS.sleep(80);
        System.out.println("动态修改后的值为：");
        System.out.println(myAge);
        System.out.println(myAge.get());
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("archaius.configurationSource.additionalUrls",
                "file:\\D:\\workspaces-mine\\learning\\netflix-learning\\mydemo.properties");

        DynamicIntProperty myAge = DynamicPropertyFactory.getInstance().getIntProperty("my.age", 18);
        System.out.println(myAge);
        System.out.println(myAge.get());
    }

}
