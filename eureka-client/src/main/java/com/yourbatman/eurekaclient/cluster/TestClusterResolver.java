package com.yourbatman.eurekaclient.cluster;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.resolver.aws.ConfigClusterResolver;
import org.junit.Test;

import java.util.List;

public class TestClusterResolver {

    @Test
    public void fun1() {
        EurekaClientConfig config = new DefaultEurekaClientConfig();
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setAppName("YourBatman")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .build();

        ConfigClusterResolver clusterResolver = new ConfigClusterResolver(config, instanceInfo);
        List<AwsEndpoint> clusterEndpoints = clusterResolver.getClusterEndpoints();

        System.out.println(clusterEndpoints);
    }
}
