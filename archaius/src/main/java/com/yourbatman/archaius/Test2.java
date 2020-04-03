package com.yourbatman.archaius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.config.AbstractDynamicPropertyListener;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationBackedDynamicPropertySupportImpl;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicContextualProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicMapProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringListProperty;
import com.netflix.config.DynamicStringMapProperty;
import com.netflix.config.SimpleDeploymentContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Test2 {

    @Test
    public void fun1() {
        // 为方便打印，禁用调用系统属性们
        System.setProperty(ConfigurationManager.DISABLE_DEFAULT_SYS_CONFIG, "true");
        System.setProperty(ConfigurationManager.DISABLE_DEFAULT_ENV_CONFIG, "true");
        // config.removeConfiguration(ConfigurationManager.SYS_CONFIG_NAME);
        // config.removeConfiguration(ConfigurationManager.ENV_CONFIG_NAME);

        // 自定义Configuration实现
        // System.setProperty("archaius.default.configuration.class", "com.xxxx.XXXConfiuration");

        ConcurrentCompositeConfiguration config = (ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance();
        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n=================================");

        Properties properties = new Properties();
        properties.put("age", 18);
        // ConfigurationManager.loadPropertiesFromConfiguration();
        ConfigurationManager.loadProperties(properties);
        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n=================================");
    }

    @Test
    public void fun2() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration("config.properties");

        ConfigurationBackedDynamicPropertySupportImpl dynamicPropertySupport = new ConfigurationBackedDynamicPropertySupportImpl(config);
        // 添加一个PropertyListener监听器，监听属性的增加、set（修改）、清空
        dynamicPropertySupport.addConfigurationListener(new AbstractDynamicPropertyListener() {
            @Override
            public void handlePropertyEvent(String name, Object value, EventType eventType) {
                System.out.printf("事件类型：%s key是 %s 修改后的值是 %s\n", eventType, name, value);
                System.out.println("-------------------------------------");
            }

            // 请注意：这个事件对应AbstractConfiguration.EVENT_CLEAR，若你自己不实现，父类是空实现的哦
            @Override
            public void clear(Object source, boolean beforeUpdate) {
                //EventType里并没有对应它的实现，所以这样是行不通的
                // if (!beforeUpdate) {
                //     handlePropertyEvent(null, null, EventType.CLEAR);
                // }
                if (source instanceof Configuration && beforeUpdate) { // 必须是before，因为after就没值啦
                    System.out.println("清空所有的事件，清空的值情况如下：");
                    ConfigurationUtils.dump((Configuration) source, System.out);
                    System.out.println("\n-------------------------------------");
                }
            }
        });

        config.addProperty("age", 18);
        config.setProperty("age", 20);
        config.clearProperty("name"); // 事件：AbstractConfiguration.EVENT_CLEAR_PROPERTY
        config.clear(); // 事件：AbstractConfiguration.EVENT_CLEAR
    }

    @Test
    public void fun3() {
        DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();

        // 请务必在得到factory之后再执行部署上下文的设置
        SimpleDeploymentContext deploymentContext = new SimpleDeploymentContext();

        // // 输出：5
        // deploymentContext.setDeploymentEnvironment("prod");
        // deploymentContext.setDeploymentRegion("us-east-1");

        // // 输出：2(默认值) -> 两个条件必须全都匹配才行
        // deploymentContext.setDeploymentEnvironment("prod");

        // // 输出：10 ->[]内的值只需要一个匹配即可
        // deploymentContext.setDeploymentEnvironment("test");

        ConfigurationManager.setDeploymentContext(deploymentContext);
        DynamicContextualProperty<Integer> contextualProperty = factory.getContextualProperty("jsonValue", 1000);
        System.out.println(contextualProperty.getValue());
    }

    @Test
    public void fun4() {
        DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();

        // 普通的 -> 直接使用工厂获取实例即可
        DynamicIntProperty ageProperty = factory.getIntProperty("age", 0);
        System.out.println(ageProperty.get());
        System.out.println("------------------------------------");


        // List（set同理）
        DynamicStringListProperty hobbiesProperty = new DynamicStringListProperty("hobbies", "");
        List<String> hobbies = hobbiesProperty.get();
        System.out.println(hobbies);
        System.out.println("------------------------------------");

        // Map
        // 此处使用#号做分隔 是因为JSON串得使用逗号喽
        DynamicStringMapProperty personsProperty = new DynamicStringMapProperty("persons", "", "#");

        List<String> personStrs = personsProperty.get();
        Map<String, String> map = personsProperty.getMap();
        System.out.println(personStrs);
        map.forEach((k, v) -> System.out.println(k + "->" + v));
        System.out.println("------------------------------------");
    }

    @Test
    public void fun5(){
        DynamicMapProperty<String, Person> personsProperty = new DynamicPersonMapProperty("persons", "", "#");
        Map<String, Person> persons = personsProperty.getMap();
        List<String> value = personsProperty.getValue();
        System.out.println(value);
        System.out.println(persons);
    }

    @Getter
    @Setter
    @ToString
    private static class Person {
        private String name;
        private Integer age;
    }

    private static class DynamicPersonMapProperty extends DynamicMapProperty<String, Person> {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        public DynamicPersonMapProperty(String propName, String defaultValue, String mapEntryDelimiterRegex) {
            super(propName, defaultValue, mapEntryDelimiterRegex);
        }
        @Override
        protected String getKey(String key) {
            return key;
        }
        // 反序列化为Person对象
        @Override
        protected Person getValue(String value) {
            try {
                return MAPPER.readValue(value, Person.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

    }


}
