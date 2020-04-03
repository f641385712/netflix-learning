package com.yourbatman.eurekaclient.cluster;

import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.StaticClusterResolver;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestCluster {

    @Test
    public void fun1() throws MalformedURLException {
        ClusterResolver<EurekaEndpoint> clusterResolver = StaticClusterResolver.fromURL("beijing", new URL("http://localhost:8471/eureka/"));

        System.out.println(clusterResolver.getRegion());
        System.out.println(clusterResolver.getClusterEndpoints());
    }
}
