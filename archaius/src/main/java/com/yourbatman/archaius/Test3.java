package com.yourbatman.archaius;

import com.google.common.base.Predicate;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DefaultContextualPredicate;
import com.netflix.config.DeploymentContext;
import com.netflix.config.DynamicContextualProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.SimpleDeploymentContext;
import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Test3 {

    @Test
    public void fun1() {
        // Function解释：过来的name一定叫"YourBatman"，age一定是"18"岁
        Predicate<Map<String, Collection<String>>> predicate = new DefaultContextualPredicate(key -> {
            if (key.equals("name"))
                return "YourBatman";
            if (key.equals("age"))
                return "18";
            return null;
        });

        // 输入：名称必须是这三个中的一个，而年龄必须是16到20岁之间
        Map<String, Collection<String>> input = new HashMap<>();
        input.put("name", Arrays.asList("Peter", "YourBatman", "Tiger"));
        input.put("age", Arrays.asList("16", "17", "18", "19", "20"));

        System.out.println(predicate.test(input));


        // 输入：名字必须精确的叫Peter
        input = new HashMap<>();
        input.put("name", Arrays.asList("Peter"));
        input.put("age", Arrays.asList("16", "17", "18", "19", "20"));
        System.out.println(predicate.test(input));
    }

    @Test
    public void fun2() {
        DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();

        DynamicContextualProperty<String> contextualProperty = factory.getContextualProperty("applicationName", "defaultName");
        System.out.println(contextualProperty.getValue()); // YourBatman
    }

    @Test
    public void fun3() {
        // 通过SimpleDeploymentContext手动设置部署环境参数
        SimpleDeploymentContext deploymentContext = new SimpleDeploymentContext();
        deploymentContext.setDeploymentRegion("ali");
        deploymentContext.setDeploymentEnvironment("prod");
        ConfigurationManager.setDeploymentContext(deploymentContext);

        DynamicContextualProperty<String> contextualProperty = new DynamicContextualProperty<>("applicationName", "defaultName");
        System.out.println(contextualProperty.getValue()); // YourBatman-ali-prod
    }


    @Test
    public void fun4() {
        // 调用一下，让Configuration完成初始化
        AbstractConfiguration configInstance = ConfigurationManager.getConfigInstance();
        configInstance.addProperty(DeploymentContext.ContextKey.region.getKey(), "ten");
        configInstance.addProperty(DeploymentContext.ContextKey.environment.getKey(), "test");

        // 效果同上。但推荐用上者
        // System.setProperty(DeploymentContext.ContextKey.region.getKey(),"ten");
        // System.setProperty(DeploymentContext.ContextKey.environment.getKey(),"test");

        DynamicContextualProperty<String> contextualProperty = new DynamicContextualProperty<>("applicationName", "defaultName");
        System.out.println(contextualProperty.getValue()); // YourBatman-ten-test
    }


    @Test
    public void fun5() {
        System.setProperty(DeploymentContext.ContextKey.environment.getKey(), "prod");
        System.setProperty("@myDiyParam", "China");

        DynamicContextualProperty<String> contextualProperty = new DynamicContextualProperty<>("applicationName", "defaultName");
        System.out.println(contextualProperty.getValue()); // YourBatman-myDiy-prod
    }

}
