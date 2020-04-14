package com.yourbatman.eurekaclient.cluster;

import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.DefaultEndpoint;
import com.netflix.discovery.shared.resolver.EndpointRandomizer;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.ResolverUtils;
import com.netflix.discovery.shared.resolver.StaticClusterResolver;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.resolver.aws.ZoneAffinityClusterResolver;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestCluster {

    @Test
    public void fun1() throws MalformedURLException {
        ClusterResolver<EurekaEndpoint> clusterResolver = StaticClusterResolver.fromURL("beijing", new URL("http://localhost:8471/eureka/"));

        System.out.println(clusterResolver.getRegion());
        System.out.println(clusterResolver.getClusterEndpoints());
    }

    @Test
    public void fun2() {
        EndpointRandomizer randomizer = ResolverUtils::randomize;

        List<EurekaEndpoint> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new DefaultEndpoint("http://localhost:" + i + "/eureka/"));
        }
        System.out.println("打乱前：" + transfer2PortOnly(list));
        System.out.println("打乱后：" + transfer2PortOnly(randomizer.randomize(list)));
        System.out.println("打乱后：" + transfer2PortOnly(randomizer.randomize(list)));
        System.out.println("打乱后：" + transfer2PortOnly(randomizer.randomize(list)));
        System.out.println("打乱后：" + transfer2PortOnly(randomizer.randomize(list)));
    }

    // 为了便于观察结果差异
    private List<Integer> transfer2PortOnly(List<EurekaEndpoint> list) {
        return list.stream().map(x -> x.getPort()).collect(Collectors.toList());
    }

    @Test
    public void fun3() {
        // 该解析器所属的region是亚洲
        ClusterResolver<AwsEndpoint> delegateResolver = new StaticClusterResolver("亚洲", new AwsEndpoint("http://localhost:8080/eureka/", "亚洲", "东北"),
                new AwsEndpoint("http://localhost:8080/eureka/", "亚洲", "南部"),
                new AwsEndpoint("http://10.1.9.56:8080/eureka/", "亚洲", "南部"),
                new AwsEndpoint("http://localhost:8080/eureka/", "亚洲", "东部"),

                // 另外一个region区域  也有一个zone叫南部
                new AwsEndpoint("http://localhost:8080/eureka/", "美洲", "南部"),
                new AwsEndpoint("http://localhost:8080/eureka/", "美洲", "东部"),
                new AwsEndpoint("http://localhost:8080/eureka/", "美洲", "西海岸")
        );

        // ZoneAffinityClusterResolver clusterResolver = new ZoneAffinityClusterResolver(delegateResolver, "南部", true, ResolverUtils::randomize);
        ZoneAffinityClusterResolver clusterResolver = new ZoneAffinityClusterResolver(delegateResolver, "南部", false, ResolverUtils::randomize);

        System.out.println("所属区域：" + clusterResolver.getRegion());
        System.out.println("端点们：");
        clusterResolver.getClusterEndpoints().stream().forEach(e -> System.out.println(e.getRegion() + " -> " + e.getZone()));

    }
}
