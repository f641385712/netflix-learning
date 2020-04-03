package com.yourbatman.configuration;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Test;

import java.io.InputStream;

public class TestNew {

    @Test
    public void fun1() throws ConfigurationException {
        // InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("1.json");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("1.json");

        JSONConfiguration configuration = new JSONConfiguration();
        configuration.read(inputStream);
        ConfigurationUtils.dump(configuration,System.out);
    }


}
