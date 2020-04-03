package com.yourbatman.configuration;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

/**
 * 测试1.x
 */
public class TestCase1 {

    @Test
    public void fun1() throws ConfigurationException {
        Configuration config = new PropertiesConfiguration("1.properties");
        System.out.println(config.getString("common.name"));
    }

}
