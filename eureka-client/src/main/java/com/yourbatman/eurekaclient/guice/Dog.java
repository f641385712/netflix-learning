package com.yourbatman.eurekaclient.guice;

public class Dog implements Animal {
    @Override
    public void run() {
        System.out.println("dog run...");
    }
}
