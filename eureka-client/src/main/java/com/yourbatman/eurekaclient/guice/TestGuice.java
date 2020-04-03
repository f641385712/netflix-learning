package com.yourbatman.eurekaclient.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.junit.Test;

import javax.inject.Inject;

public class TestGuice {

    @Inject
    private Animal animal;
    // private Provider<Animal> animal;

    @Test
    public void fun1() {
        Injector injector = Guice.createInjector();
        // Injector injector = Guice.createInjector(new MainModule());
        // Module finalModule = Modules.override(new MainModule()).with(new ServerModule());
        // 为当前实例注入容器内的对象
        injector.injectMembers(this);

        System.out.println(animal);
        System.out.println(injector.getInstance(Animal.class));
        System.out.println(injector.getInstance(Animal.class));
        animal.run();
    }
}
