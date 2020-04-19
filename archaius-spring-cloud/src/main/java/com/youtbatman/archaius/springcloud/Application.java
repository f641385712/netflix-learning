package com.youtbatman.archaius.springcloud;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.apache.commons.configuration.AbstractConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // 读取配置
        AbstractConfiguration config = ConfigurationManager.getConfigInstance();
        System.out.println(config.getString("my.name"));
        System.out.println(config.getString("my.age"));

        DynamicStringProperty name = DynamicPropertyFactory.getInstance().getStringProperty("my.name", "defualtName");
        DynamicIntProperty age = DynamicPropertyFactory.getInstance().getIntProperty("my.age", 0);
        System.out.println(name);
        System.out.println(age);

        context.close();
    }

}
