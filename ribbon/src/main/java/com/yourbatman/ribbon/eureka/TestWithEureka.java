package com.yourbatman.ribbon.eureka;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Applications;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.niws.loadbalancer.DefaultNIWSServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.niws.loadbalancer.EurekaNotificationServerListUpdater;
import com.netflix.niws.loadbalancer.NIWSDiscoveryPing;
import org.junit.Test;

import javax.inject.Provider;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestWithEureka {

    @Test
    public void fun1() throws InterruptedException {
        ILoadBalancer lb = createLoadBalancer("account");
        for (int i = 0; i < 5; i++) {
            System.out.println(lb.chooseServer(null));
        }

        TimeUnit.MINUTES.sleep(1);
    }

    @Test
    public void fun2() throws InterruptedException {
        ILoadBalancer lb = createLoadBalancer("account");

        new Thread(() -> {
            while (true) {
                System.out.println("实例总数：" + lb.getReachableServers().size());
                System.out.println(lb.chooseServer(null));

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        TimeUnit.MINUTES.sleep(1);
    }

    private ILoadBalancer createLoadBalancer(String appName) {
        IClientConfig config = new DefaultClientConfigImpl();
        IRule rule = new RoundRobinRule();
        IPing ping = new NIWSDiscoveryPing();

        // 从Eureka Server里去拿服务列表  需要准备EurekaClient客户端
        EurekaClient eurekaClient = createEurekaClient();
        Provider<EurekaClient> eurekaClientProvider = () -> eurekaClient;
        ServerList<DiscoveryEnabledServer> serverList = new DiscoveryEnabledNIWSServerList(appName, eurekaClientProvider);
        ServerListFilter<DiscoveryEnabledServer> serverListFilter = new DefaultNIWSServerListFilter<>();
        // 使用Eureka事件监听去 更新服服务列表
        // 此处使用的空构造器，它内部使用的便是“全局唯一的那个”Eureka Client（就上面创建的那个）  也是可以的
        // ServerListUpdater serverListUpdater = new EurekaNotificationServerListUpdater();
        // 若你不放心，使用这个亦可
        ServerListUpdater serverListUpdater = new EurekaNotificationServerListUpdater(eurekaClientProvider);

        return new DynamicServerListLoadBalancer<>(
                config, rule, ping, serverList, serverListFilter, serverListUpdater);
    }

    // 由于本示例并不需要注册自己，只需要拉取。所以我把注册自己关掉（参见配置）
    private EurekaClient createEurekaClient() {
        EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();

        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("AAAAAAAAAA") // 本应用名称，随便写个就成
                .build();
        ApplicationInfoManager manager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        return new DiscoveryClient(manager, clientConfig);
    }

    @Test
    public void testCreateEurekaClient() {
        EurekaClient eurekaClient = createEurekaClient();

        // 测试：去拿一次全部的注册表信息
        Applications applications = eurekaClient.getApplications();
        System.out.println("服务实例总数：" + applications.size());
        List<InstanceInfo> accountInstances = applications.getInstancesByVirtualHostName("account");
        accountInstances.forEach(i -> System.out.println(i));
    }

    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setZone(zone);
        return server;
    }

}
