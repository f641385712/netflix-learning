package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperties;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperty;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesChainedProperty;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesCommandDefault;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Test1 {

    @Test
    public void fun1() throws InterruptedException {
        HystrixPlugins instance = HystrixPlugins.getInstance();

        HystrixDynamicProperties dynamicProperties = instance.getDynamicProperties();
        System.out.println(dynamicProperties.getClass());

        // dynamicProperties.getString()
        HystrixDynamicProperty<String> nameProperty = HystrixDynamicProperties.Util.getProperty(dynamicProperties, "name", "defaultValue", String.class);
        nameProperty.addCallback(() -> {
            String name = nameProperty.getName(); // 属性名
            System.out.println("属性" + name + "发生了变更");
        });
        System.out.println(nameProperty.get());

        // hold住主线程
        while (true) {
            TimeUnit.SECONDS.sleep(60); // 因为默认是60秒check一次
            System.out.println(nameProperty.get());
        }
    }

    @Test
    public void fun2() {
        HystrixDynamicProperty<String> property = HystrixPropertiesChainedProperty.forString()
                .add("hystrix.command.myApp.personName", null)
                .add("hystrix.command.default.personName", "name-default")
                .build();
        System.out.println(property.get());
    }

    @Test
    public void fun3() {
        HystrixCommandProperties.ExecutionIsolationStrategy semaphore = HystrixCommandProperties.ExecutionIsolationStrategy.valueOf("SEMAPHORE");
        System.out.println(semaphore);
    }

    @Test
    public void fun4() {
        // 使用API方式定制配置
        HystrixCommandProperties.Setter setter = HystrixCommandProperties.Setter();
        setter.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                .withExecutionTimeoutEnabled(true)
                .withExecutionTimeoutInMilliseconds(3000);

        // HystrixPropertiesStrategy
        HystrixCommandProperties hystrixProperties = new HystrixPropertiesCommandDefault(HystrixCommandKey.Factory.asKey("MyInstanceName"), setter);

        // ... 省略应用hystrixProperties的步骤喽~
    }

    @Test
    public void fun5() {
        // 请注意：这里传null会抛出异常，Hystrix很多代码的健壮性其实是非常不够的，这是它的缺点，需要批评
        // HystrixCommandProperties commandProperties = new HystrixPropertiesCommandDefault(HystrixCommandKey.Factory.asKey("myApp"), null);
        HystrixCommandProperties commandProperties = new HystrixPropertiesCommandDefault(HystrixCommandKey.Factory.asKey("myApp"), HystrixCommandProperties.Setter());

        // 很明显，这里打印的肯定就是属性的默认值喽
        System.out.println(commandProperties.circuitBreakerEnabled().get());
        System.out.println(commandProperties.executionIsolationStrategy().get());
        System.out.println(commandProperties.executionTimeoutEnabled().get());
        System.out.println(commandProperties.executionTimeoutInMilliseconds().get());
    }

    public strictfp final synchronized static void main(String[] args) {

    }

}
