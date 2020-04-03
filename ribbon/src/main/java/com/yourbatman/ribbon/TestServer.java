package com.yourbatman.ribbon;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ConfigurationBasedServerList;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.PollingServerListUpdater;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ServerStats;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 测试{@link Server}
 *
 * @author yourbatman
 * @date 2020/3/12 0:54
 */
public class TestServer {

    @Test
    public void fun1() {
        Server server = new Server("www.yourbatman.com", 886);

        System.out.println(server.getId()); // www.yourbatman.com:886
        System.out.println(server.getHost()); // www.yourbatman.com
        System.out.println(server.getPort()); // 886
        System.out.println(server.getHostPort()); // www.yourbatman.com:886
        System.out.println(server.getScheme()); // null

        server.setId("localhost:8080");
        System.out.println(server.getId()); // localhost:8080
        System.out.println(server.getHost()); // localhost
        System.out.println(server.getPort()); // 8080
        System.out.println(server.getHostPort()); // localhost:8080
        System.out.println(server.getScheme()); // null

        server.setId("https://www.baidu.com");
        System.out.println(server.getId()); // www.baidu.com:443
        System.out.println(server.getHost()); // www.baidu.com
        System.out.println(server.getPort()); // 443
        System.out.println(server.getHostPort()); // www.baidu.com:443
        System.out.println(server.getScheme()); // https
    }

    @Test
    public void fun2() {
        // 准备配置
        IClientConfig config = new DefaultClientConfigImpl();
        // config.set(CommonClientConfigKey.valueOf("listOfServers"), "www.baidu.com,http://yourbatman.com:8080");
        config.set(CommonClientConfigKey.ListOfServers, "    www.baidu.com,http://yourbatman.com:8080    ");

        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);

        serverList.getInitialListOfServers().forEach(server -> {
            System.out.println(server.getId());
            System.out.println(server.getHost());
            System.out.println(server.getPort());
            System.out.println(server.getHostPort());
            System.out.println(server.getScheme());
            System.out.println("-----------------------------");
        });
    }


    @Test
    public void fun100() {
        DefaultClientConfigImpl config = DefaultClientConfigImpl.getClientConfigWithDefaultValues("account");

        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);

        serverList.getInitialListOfServers().forEach(server -> {
            System.out.println(server.getId());
            System.out.println(server.getHost());
            System.out.println(server.getPort());
            System.out.println(server.getHostPort());
            System.out.println(server.getScheme());
            System.out.println("-----------------------------");
        });
    }


    @Test
    public void fun101() {
        DefaultClientConfigImpl config = DefaultClientConfigImpl.getEmptyConfig();
        config.loadDefaultValues(); // 注意：这句必须显示调用，否则配置里无值

        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);

        serverList.getInitialListOfServers().forEach(server -> {
            System.out.println(server.getId());
            System.out.println(server.getHost());
            System.out.println(server.getPort());
            System.out.println(server.getHostPort());
            System.out.println(server.getScheme());
            System.out.println("-----------------------------");
        });
    }


    @Test
    public void fun3() {
        // 准备配置
        DefaultClientConfigImpl config = DefaultClientConfigImpl.getClientConfigWithDefaultValues("account");
        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);

        serverList.getInitialListOfServers().forEach(server -> {
            System.out.println(server.getId());
            System.out.println(server.getHost());
            System.out.println(server.getPort());
            System.out.println(server.getHostPort());
            System.out.println(server.getScheme());
            System.out.println("-----------------------------");
        });
    }

    @Test
    public void fun4() throws InterruptedException {
        ServerStats serverStats = new ServerStats();
        // 缓冲区大小最大1000。 若QPS是200，5s能装满它  这个QPS已经很高了
        serverStats.setBufferSize(1000);
        // 5秒收集一次数据
        serverStats.setPublishInterval(5000);
        // 请务必调用此初始化方法
        serverStats.initialize(new Server("YourBatman", 80));

        // 多个线程持续不断的发送请求
        request(serverStats);
        // 监控ServerStats状态
        monitor(serverStats);

        // hold主线程
        TimeUnit.SECONDS.sleep(10000);
    }

    // 单独线程模拟刷页面，获取监控到的数据
    private void monitor(ServerStats serverStats) {
        new Thread(() -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(() -> {
                System.out.println("=======时间：" + serverStats.getResponseTimePercentileTime() + "，统计值如下=======");
                System.out.println("请求总数(持续累计)：" + serverStats.getTotalRequestsCount());
                System.out.println("平均响应时间：" + serverStats.getResponseTimeAvg());
                System.out.println("最小响应时间：" + serverStats.getResponseTimeMin());
                System.out.println("最大响应时间：" + serverStats.getResponseTimeMax());


                System.out.println("样本大小(取样本)：" + serverStats.getResponseTimePercentileNumValues());
                System.out.println("样本下的平均响应时间：" + serverStats.getResponseTimeAvgRecent());
                System.out.println("样本下的响应时间中位数：" + serverStats.getResponseTime50thPercentile());
                System.out.println("样本下的响应时间90分位数：" + serverStats.getResponseTime90thPercentile());
            }, 5, 5, TimeUnit.SECONDS);
        }).start();
    }


    // 模拟请求（开启5个线程，每个线程都持续不断的请求）
    private void request(ServerStats serverStats) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    // 请求之前 记录活跃请求数
                    serverStats.incrementActiveRequestsCount();
                    serverStats.incrementNumRequests();
                    long rt = doSomething();
                    // 请求结束， 记录响应耗时
                    serverStats.noteResponseTime(rt);
                    serverStats.decrementActiveRequestsCount();
                }
            }).start();
        }
    }

    // 模拟请求耗时，返回耗时时间
    private long doSomething() {
        try {
            int rt = randomValue(10, 200);
            TimeUnit.MILLISECONDS.sleep(rt);
            return rt;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    // 本地使用随机数模拟数据收集
    private int randomValue(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }


}
