package com.yourbatman.eurekaclient.transport;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import com.netflix.discovery.CommonConstants;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.internal.util.Archaius1Utils;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.resolver.ClosableResolver;
import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.DefaultEndpoint;
import com.netflix.discovery.shared.resolver.EndpointRandomizer;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.ResolverUtils;
import com.netflix.discovery.shared.resolver.StaticClusterResolver;
import com.netflix.discovery.shared.resolver.aws.ApplicationsResolver;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.transport.DefaultEurekaTransportConfig;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import com.netflix.discovery.shared.transport.EurekaHttpClients;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient;
import com.netflix.discovery.shared.transport.decorator.ServerStatusEvaluators;
import com.netflix.discovery.shared.transport.decorator.SessionedEurekaHttpClient;
import com.netflix.discovery.shared.transport.jersey.JerseyEurekaHttpClientFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestTransport {

    @Test
    public void fun0() {
        int i = (int) (4 * 0.66);
        System.out.println(i);
    }

    @Test
    public void fun1() {
        TransportClientFactory factory = JerseyEurekaHttpClientFactory.newBuilder()
                .withClientName("YoutBatman-Client") //必填
                .build();

        MyEurekaHttpClientFactory myEurekaHttpClientFactory = new MyEurekaHttpClientFactory(factory, Arrays.asList(8761, 8762, 8763, 8764));
        SessionedEurekaHttpClient client = new SessionedEurekaHttpClient("demo", myEurekaHttpClientFactory, 2000);

        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account")
                .build();
        while (true) {
            // TimeUnit.SECONDS.sleep(1);
            // 每次执行请求之前：都会看一下client是否需要完整的重新连一次
            try {
                client.register(instanceInfo);
            } catch (Exception e) {
            }
        }
    }

    private static class MyEurekaHttpClientFactory implements EurekaHttpClientFactory {

        private TransportClientFactory factory;
        // 规定端口号 用于每次创建时候随机选择
        private List<Integer> ports;

        private MyEurekaHttpClientFactory(TransportClientFactory factory, List<Integer> ports) {
            this.factory = factory;
            this.ports = ports;
        }

        @Override
        public EurekaHttpClient newClient() {
            Random random = new Random();
            int portIndex = random.nextInt(ports.size());
            return factory.newClient(new DefaultEndpoint("http://localhost:" + ports.get(portIndex) + "/eureka/"));
        }

        @Override
        public void shutdown() {
            System.out.println("释放资源....");
        }
    }

    @Test
    public void fun2() {
        // 说明：第一参数属于parentNamespace，一般可填写eureka，这样配置就为eureka.transport.xxx = xxx
        // 若填写null，那么固定就为transport.xxx = xxx即可
        // 本文为了统一，均加上eureka前缀（推荐），因为实际上也是这么来用的
        EurekaTransportConfig transportConfig = new DefaultEurekaTransportConfig("eureka", Archaius1Utils.initConfig(CommonConstants.CONFIG_FILE_NAME));
        ClusterResolver<EurekaEndpoint> clusterResolver = new StaticClusterResolver("亚洲",
                new DefaultEndpoint("http://localhost:8761/eureka/"),
                new DefaultEndpoint("http://localhost:8762/eureka/"),
                new DefaultEndpoint("http://localhost:8763/eureka/"),
                new DefaultEndpoint("http://localhost:8764/eureka"));


        TransportClientFactory factory = JerseyEurekaHttpClientFactory.newBuilder()
                .withClientName("YoutBatman-Client") //必填
                .build();
        EurekaHttpClientFactory eurekaHttpClientFactory = RetryableEurekaHttpClient.createFactory("demo",
                transportConfig, clusterResolver, factory, ServerStatusEvaluators.legacyEvaluator());

        EurekaHttpClient client = eurekaHttpClientFactory.newClient();
        try {
            EurekaHttpResponse<Void> response = client.register(InstanceInfo.Builder.newBuilder()
                    .setInstanceId("account-001")
                    .setHostName("localhost")
                    .setIPAddr("127.0.0.1")
                    .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                    .setAppName("account") // 大小写无所谓
                    .build());
            System.out.println(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            EurekaHttpResponse<Void> response = client.register(InstanceInfo.Builder.newBuilder()
                    .setInstanceId("account-001")
                    .setHostName("localhost")
                    .setIPAddr("127.0.0.1")
                    .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                    .setAppName("account") // 大小写无所谓
                    .build());
            System.out.println(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fun3() throws InterruptedException {
        EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        EurekaTransportConfig transportConfig = new DefaultEurekaTransportConfig("eureka", Archaius1Utils.initConfig(CommonConstants.CONFIG_FILE_NAME));
        TransportClientFactory transportClientFactory = JerseyEurekaHttpClientFactory.newBuilder()
                .withClientName("YoutBatman-Client") //必填
                .build();
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account")
                .build();

        // // 因为使用default的话，不需要用到它，所以暂时赋值为null。下个示例会给它赋真实值
        // ApplicationsResolver.ApplicationsSource applicationsSource = null;
        // 匿名实现一个
        ApplicationsResolver.ApplicationsSource applicationsSource = (int stalenessThreshold, TimeUnit timeUnit) -> {
            Applications applications = new Applications();

            // account应用  并且向里面添加实例
            Application accountApp = new Application("account");
            for (int i = 1; i <= 5; i++) {
                accountApp.addInstance(InstanceInfo.Builder.newBuilder()
                        .setInstanceId("account-00" + i)
                        .setHostName("localhost" + i)
                        .setIPAddr("127.0.0." + i)
                        .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                        .setAppName("account")
                        .build());
            }
            applications.addApplication(accountApp);
            return applications;
        };
        EndpointRandomizer randomizer = ResolverUtils::randomize;

        // 构建一个启动解析器
        ClosableResolver<AwsEndpoint> bootstrapResolver = EurekaHttpClients.newBootstrapResolver(clientConfig, transportConfig,
                transportClientFactory, instanceInfo, applicationsSource, randomizer);
        System.out.println(bootstrapResolver.getRegion());
        bootstrapResolver.getClusterEndpoints().forEach(e -> System.out.println(e.getRegion() + ":" + e.getZone() + ":" + e.getServiceUrl()));


        // 因为Archaius默认的60s更新一下配置文件内容，所以此处值稍微大一点才会看到效果
        // TimeUnit.SECONDS.sleep(100);
        // System.out.println("动态修改过配置文件后的结果如下：");
        // bootstrapResolver.getClusterEndpoints().forEach(e -> System.out.println(e.getRegion() + ":" + e.getZone() + ":" + e.getServiceUrl()));

    }


    @Test
    public void fun4() {
        EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
        EurekaTransportConfig transportConfig = new DefaultEurekaTransportConfig("eureka", Archaius1Utils.initConfig(CommonConstants.CONFIG_FILE_NAME));
        TransportClientFactory transportClientFactory = JerseyEurekaHttpClientFactory.newBuilder()
                .withClientName("YoutBatman-Client") //必填
                .build();
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account")
                .build();

        // 匿名实现一个
        // 模拟该注册表是从Server端拉下来的（实际用本地写死模拟）
        ApplicationsResolver.ApplicationsSource applicationsSource = (int stalenessThreshold, TimeUnit timeUnit) -> {
            Applications applications = new Applications();

            // account应用  并且向里面添加实例
            Application accountApp = new Application("account");
            for (int i = 1; i <= 5; i++) {
                accountApp.addInstance(InstanceInfo.Builder.newBuilder()
                        .setInstanceId("account-00" + i)
                        .setHostName("localhost" + i)
                        .setPort(8740 + i)
                        .setIPAddr("127.0.0." + i)
                        .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                        .setAppName("account")
                        // 若自动生成实例，则此属性会取值这个配置：EurekaInstanceConfig#getVirtualHostName()
                        // 此处我就不配置了，写死在此处吧
                        // 注意：setVIPAddress()里面是有逻辑的，建议读者可以点进去看看源码，就知道如何配置它了
                        // 可写死字符串，也可以使用${}引用任意的Archaius管理的配置项
                        // 这里一般就可以放一个NG的地址值(内网域名)，ng后面就可以挂多个eureka Server了
                        .setVIPAddress("${eureka.region}.domain.com")
                        .build());
            }
            applications.addApplication(accountApp);
            // 这一步必不可少（请参照DiscoveryClient的实现）
            applications.shuffleInstances(clientConfig.shouldFilterOnlyUpInstances());
            return applications;
        };
        EndpointRandomizer randomizer = ResolverUtils::randomize;

        // 构建一个启动解析器
        ClosableResolver<AwsEndpoint> bootstrapResolver = EurekaHttpClients.newBootstrapResolver(clientConfig, transportConfig,
                transportClientFactory, instanceInfo, applicationsSource, randomizer);
        System.out.println(bootstrapResolver.getRegion());
        bootstrapResolver.getClusterEndpoints().forEach(e -> System.out.println(e.getRegion() + ":" + e.getZone() + ":" + e.getServiceUrl()));

    }

}
