package com.yourbatman.eurekaclient.guice;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

@ImplementedBy(Dog.class)
// @Singleton // 不能标记在接口上，只能标记在Dog实现类上
public interface Animal {
    void run();
}
