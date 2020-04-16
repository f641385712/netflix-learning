package com.yourbatman.eurekaclient.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.providers.CloudInstanceConfigProvider;
import com.netflix.appinfo.providers.MyDataCenterInstanceConfigProvider;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.guice.EurekaModule;
import org.junit.Test;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class TestGuice {

    // @Inject
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

    @Inject
    EurekaClient eurekaClient;

    @Test
    public void fun2() throws InterruptedException {
        // 启动DI容器
        Injector injector = Guice.createInjector(Modules.override(new EurekaModule()).with(new MyModule()));
        // 让其可以@Inject注入eurekaClient 提供使用
        // 说明：若你只想用纯API方式使用，此句是没有必要写的~~~~~
        injector.injectMembers(this);

        // 可以看到注入的和API获取到的是同一个实例
        EurekaClient injectorInstance = injector.getInstance(EurekaClient.class);
        System.out.println(eurekaClient == injectorInstance);

        TimeUnit.MINUTES.sleep(2);
    }

    private static class MyModule extends AbstractModule{

        @Override
        protected void configure() {
            bind(EurekaInstanceConfig.class).toProvider(MyDataCenterInstanceConfigProvider.class).in(Scopes.SINGLETON);
        }
    }
}
