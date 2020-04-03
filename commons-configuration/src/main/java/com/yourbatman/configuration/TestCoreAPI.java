package com.yourbatman.configuration;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.apache.commons.configuration2.io.CombinedLocationStrategy;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystemLocationStrategy;
import org.apache.commons.configuration2.io.ProvidedURLLocationStrategy;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCoreAPI {

    @Test
    public void fun1() throws ConfigurationException {
        Configurations configs = new Configurations();
        PropertiesConfiguration config = configs.properties("1.properties");

        // 监听ADD_PROPERTY添加属性事件
        config.addEventListener(ConfigurationEvent.ADD_PROPERTY, event -> {
            if (!event.isBeforeUpdate()) {
                System.out.printf("成功添加属性：%s = %s", event.getPropertyName(), event.getPropertyValue());
            }
        });


        config.addProperty("name", "YourBatman");
    }

    @Test
    public void fun2() {
        // // 1.x
        // URL file1 = ConfigurationUtils.locate("1.properties");
        // System.out.println(file1);
        //
        // // 2.x
        // URL file2 = FileLocatorUtils.locate(FileLocatorUtils.fileLocator().fileName("1.properties").create());
        // System.out.println(file2);


        // 定义定位文件的顺序
        List<FileLocationStrategy> subs = Arrays.asList(
                new ProvidedURLLocationStrategy(),
                new FileSystemLocationStrategy(),
                new ClasspathLocationStrategy());
        FileLocationStrategy strategy = new CombinedLocationStrategy(subs);
    }

    @Test
    public void fun3() throws ConfigurationException {
        // Configurations configs = new Configurations();
        // FileLocator fileLocator = FileLocatorUtils.fileLocator().fileName("1.properties").create();
        // fileLocator = FileLocatorUtils.fullyInitializedLocator(fileLocator);
        // PropertiesConfiguration config = configs.properties(fileLocator.getSourceURL());

        PropertiesConfiguration config = new PropertiesConfiguration();

        // 把config和文件关联上
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", "1.properties");
        FileHandler fileHandler = new FileHandler(config, FileHandler.fromMap(map));

        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n==============上为load之前==============");
        fileHandler.load(); // 通过fileHandler也可以给Configuration赋值
        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n==============上为load之后==============");
        fileHandler.load();
        ConfigurationUtils.dump(config, System.out);
        System.out.println("\n==============上为再load一次的结果==============");

        // fileHandler.save(); // 说明：写也是增量的写，调用多次会写多份

        // config.clear(); // 会发送ConfigurationEvent.CLEAR事件哦
        // fileHandler.load();
        // ConfigurationUtils.dump(config, System.out);
        // System.out.println("\n==============上为clear清空后再load的结果==============");
    }
}
