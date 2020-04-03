package com.yourbatman.configuration;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestBuilder {

    @Test
    public void fun1() throws ConfigurationException {
        BasicConfigurationBuilder builder = new BasicConfigurationBuilder(PropertiesConfiguration.class);
        Map<String, Object> params = new HashMap<>();
        // params.put("fileName", "YourBatman"); // 此处不能设置，因为它没有名为`fileName`的这个属性值，所以会抛错
        params.put("includesAllowed", false);
        builder.addParameters(params);

        // PropertiesBuilderParametersImpl parameters = new PropertiesBuilderParametersImpl();
        // parameters.setFileName("1.properties");
        // // parameters.setIncludesAllowed(true);
        // builder.configure(parameters);

        PropertiesConfiguration configuration = (PropertiesConfiguration) builder.getConfiguration();
        System.out.println(configuration.isIncludesAllowed()); // true设置成功，includesAllowed的值被成功设置为了false


        PropertiesConfiguration configuration2 = (PropertiesConfiguration) builder.getConfiguration();
        System.out.println(configuration == configuration2); //  true。 说明get多次是同一实例

        // 重置一下
        builder.reset();
        configuration2 = (PropertiesConfiguration) builder.getConfiguration();
        System.out.println(configuration == configuration2); // false。说明重置后，生成了新的configuration2实例
    }

    @Test
    public void fun2() {
        PropertiesBuilderParametersImpl parameters = new PropertiesBuilderParametersImpl();
        parameters.setFileName("1.properties");

        FileHandler fileHandler = parameters.getFileHandler();
        FileLocator fileLocator = fileHandler.getFileLocator();
        fileLocator = FileLocatorUtils.fullyInitializedLocator(fileLocator);

        System.out.println(fileLocator.getSourceURL());
        System.out.println(fileLocator.getBasePath());

        System.out.println(parameters.getParameters());
    }


    @Test
    public void fun3() throws ConfigurationException {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
        builder.setAutoSave(false);


        // 构建参数
        PropertiesBuilderParametersImpl parameters = new PropertiesBuilderParametersImpl();
        parameters.setFileName("1.properties");
        builder.configure(parameters);

        // // 直接get，会使用上面的parameter来生成实例
        // FileHandler fileHandler = builder.getFileHandler();
        // System.out.println(fileHandler);

        PropertiesConfiguration configuration = builder.getConfiguration();
        ConfigurationUtils.dump(configuration,System.out);


        Parameters params = new Parameters();
        builder.configure(params.fileBased()
                .setThrowExceptionOnMissing(true).setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                .setFileName("1.properties"));
    }

    @Test
    public void fun4() throws ConfigurationException {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
        Parameters params = new Parameters();

        // 配置一个FileBased参数即可
        FileBasedBuilderParameters builderParameters = params.fileBased().setThrowExceptionOnMissing(true)
                .setEncoding("UTF-8")
                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                .setFileName("1.properties");
        builder.configure(builderParameters);

        System.out.println(builderParameters.getClass());
        ConfigurationUtils.dump(builder.getConfiguration(),System.out);
    }


    @Test
    public void fun5() throws ConfigurationException {
        // 当然，绝大多数情况下，空构造new Configurations()即可
        // Parameters params = new Parameters();
        // Configurations configs = new Configurations(params);


        Configurations configs = new Configurations();
        PropertiesConfiguration configuration = configs.properties("1.properties");
        ConfigurationUtils.dump(configuration,System.out);
    }
}
