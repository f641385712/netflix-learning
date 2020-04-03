package com.yourbatman.ribbon;

import com.google.common.base.Optional;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.PollingServerListUpdater;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ServerStats;
import com.netflix.loadbalancer.ZoneAffinityPredicate;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestPredicate {

    @Test
    public void fun1() throws InterruptedException {
        // 准备一批服务器
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));

        // 指定当前的zone
        DeploymentContext deploymentContext = ConfigurationManager.getDeploymentContext();
        deploymentContext.setValue(DeploymentContext.ContextKey.zone, "华北");

        // 准备断言器
        ZoneAffinityPredicate predicate = new ZoneAffinityPredicate();

        while (true) {
            // 以轮询方式选择Server
            Optional<Server> serverOptional = predicate.chooseRoundRobinAfterFiltering(serverList);
            Server server = serverOptional.get();
            String zone = server.getZone();
            System.out.println("区域：" + zone + "，序号是：" + server.getPort());

            TimeUnit.SECONDS.sleep(5);
        }
    }


    @Test
    public void fun5() throws InterruptedException {
        LoadBalancerStats lbs = new LoadBalancerStats("YoutBatman");

        // 添加Server
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));
        lbs.updateServerList(serverList);

        Map<String, List<Server>> zoneServerMap = new HashMap<>();
        // 模拟向每个Server发送请求  记录ServerStatus数据
        serverList.forEach(server -> {
            ServerStats serverStat = lbs.getSingleServerStat(server);
            request(serverStat);

            // 顺便按照zone分组
            String zone = server.getZone();
            if (zoneServerMap.containsKey(zone)) {
                zoneServerMap.get(zone).add(server);
            } else {
                List<Server> servers = new ArrayList<>();
                servers.add(server);
                zoneServerMap.put(zone, servers);
            }
        });
        lbs.updateZoneServerMapping(zoneServerMap);
        // 从lbs里拿到一些监控数据
        monitor(lbs);

        TimeUnit.SECONDS.sleep(500);
    }


    // 单独线程模拟刷页面，获取监控到的数据
    private void monitor(LoadBalancerStats lbs) {
        List<String> zones = Arrays.asList("华南", "华东", "华北");
        new Thread(() -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(() -> {
                // 打印当前可用区
                // 获取可用区
                Set<String> availableZones = ZoneAvoidanceRule.getAvailableZones(lbs, 0.2d, 0.99999d);
                System.out.println("=====当前可用区为：" + availableZones);

                zones.forEach(zone -> {
                    System.out.printf("区域[" + zone + "]概要：");
                    int instanceCount = lbs.getInstanceCount(zone);
                    int activeRequestsCount = lbs.getActiveRequestsCount(zone);
                    double activeRequestsPerServer = lbs.getActiveRequestsPerServer(zone);
                    // ZoneSnapshot zoneSnapshot = lbs.getZoneSnapshot(zone);

                    System.out.printf("实例总数：%s，活跃请求总数：%s，平均负载：%s\n", instanceCount, activeRequestsCount, activeRequestsPerServer);
                    // System.out.println(zoneSnapshot);
                });
                System.out.println("======================================================");
            }, 5, 5, TimeUnit.SECONDS);
        }).start();
    }

    private void monitor(LoadBalancerStats lbs, ZoneAffinityServerListFilter serverListFilter) {
        List<String> zones = Arrays.asList("华南", "华东", "华北");
        new Thread(() -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(() -> {
                // 打印当前可用区
                // 获取可用区
                Set<String> availableZones = ZoneAvoidanceRule.getAvailableZones(lbs, 0.2d, 0.99999d);
                System.out.println("=====当前可用区为：" + availableZones);

                List<Server> filteredListOfServers = serverListFilter.getFilteredListOfServers(new ArrayList(lbs.getServerStats().keySet()));
                System.out.println("=====过滤后可用的服务列表：" + filteredListOfServers);

                zones.forEach(zone -> {
                    System.out.printf("区域[" + zone + "]概要：");
                    int instanceCount = lbs.getInstanceCount(zone);
                    int activeRequestsCount = lbs.getActiveRequestsCount(zone);
                    double activeRequestsPerServer = lbs.getActiveRequestsPerServer(zone);
                    // ZoneSnapshot zoneSnapshot = lbs.getZoneSnapshot(zone);

                    System.out.printf("实例总数：%s，活跃请求总数：%s，平均负载：%s\n", instanceCount, activeRequestsCount, activeRequestsPerServer);
                    // System.out.println(zoneSnapshot);
                });
                System.out.println("======================================================");
            }, 5, 5, TimeUnit.SECONDS);
        }).start();
    }


    // 请注意：请必须保证Server的id不一样，否则放不进去List的（因为Server的equals hashCode方法仅和id有关）
    // 所以此处使用index作为port，以示区分
    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setZone(zone);
        return server;
    }


    // 多线程，模拟请求
    private void request(ServerStats serverStats) {
        new Thread(() -> {
            // 每10ms发送一个请求（每个请求处理10-200ms的时间），持续不断
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(() -> {
                new Thread(() -> {
                    // 请求之前 记录活跃请求数
                    serverStats.incrementActiveRequestsCount();
                    serverStats.incrementNumRequests();
                    long rt = doSomething();
                    // 请求结束， 记录响应耗时
                    serverStats.noteResponseTime(rt);
                    serverStats.decrementActiveRequestsCount();
                }).start();
            }, 10, 10, TimeUnit.MILLISECONDS);
        }).start();
        // for (int i = 0; i < 5; i++) {
        //     new Thread(() -> {
        //         while (true) {
        //             // 请求之前 记录活跃请求数
        //             serverStats.incrementActiveRequestsCount();
        //             serverStats.incrementNumRequests();
        //             long rt = doSomething();
        //             // 请求结束， 记录响应耗时
        //             serverStats.noteResponseTime(rt);
        //             serverStats.decrementActiveRequestsCount();
        //         }
        //     }).start();
        // }
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


    @Test
    public void fun6() throws InterruptedException {
        LoadBalancerStats lbs = new LoadBalancerStats("YoutBatman");

        // 添加Server
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));
        lbs.updateServerList(serverList);

        Map<String, List<Server>> zoneServerMap = new HashMap<>();
        // 模拟向每个Server发送请求  记录ServerStatus数据
        serverList.forEach(server -> {
            ServerStats serverStat = lbs.getSingleServerStat(server);
            request(serverStat);

            // 顺便按照zone分组
            String zone = server.getZone();
            if (zoneServerMap.containsKey(zone)) {
                zoneServerMap.get(zone).add(server);
            } else {
                List<Server> servers = new ArrayList<>();
                servers.add(server);
                zoneServerMap.put(zone, servers);
            }
        });
        lbs.updateZoneServerMapping(zoneServerMap);

        // 从lbs里拿到一些监控数据
        monitor(lbs);

        TimeUnit.SECONDS.sleep(500);
    }


    @Test
    public void fun7() {
        // 准备一批服务器
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));
        serverList.add(createServer("华北", 5));
        serverList.add(createServer("华北", 6));
        serverList.add(createServer("华北", 7));
        serverList.add(createServer("华北", 8));
        serverList.add(createServer("华北", 9));
        serverList.add(createServer("华北", 10));
        serverList.add(createServer("华北", 11));
        serverList.add(createServer("华北", 12));

        // 指定当前的zone
        DeploymentContext deploymentContext = ConfigurationManager.getDeploymentContext();
        deploymentContext.setValue(DeploymentContext.ContextKey.zone, "华北");

        // 准备断言器（组合模式）
        CompositePredicate compositePredicate = CompositePredicate
                .withPredicate(new ZoneAffinityPredicate()) // primary选择一个按zone过滤的断言器
                // 自定义一个fallabck过滤器
                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .addFallbackPredicate(new AbstractServerPredicate() {
                    @Override
                    public boolean apply(@Nullable PredicateKey input) {
                        int port = input.getServer().getPort();
                        return port % 10 > 5;
                    }
                })
                // 我最少要20台机器，但经过主Predicate过滤后只剩12台了，所以不用它的结果，使用fallback的结果
                .setFallbackThresholdAsMinimalFilteredNumberOfServers(20)
                .build();

        List<Server> servers = compositePredicate.getEligibleServers(serverList);
        System.out.println(servers);
    }


    @Test
    public void fun8() throws InterruptedException {
        String clientName = "YourBatman";
        // 负载均衡器状态信息   后面模拟请求来增加指标数据
        LoadBalancerStats lbs = new LoadBalancerStats(clientName);

        // 添加Server
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));
        lbs.updateServerList(serverList);

        Map<String, List<Server>> zoneServerMap = new HashMap<>();
        // 模拟向每个Server发送请求  记录ServerStatus数据
        serverList.forEach(server -> {
            ServerStats serverStat = lbs.getSingleServerStat(server);
            request(serverStat);

            // 顺便按照zone分组
            String zone = server.getZone();
            if (zoneServerMap.containsKey(zone)) {
                zoneServerMap.get(zone).add(server);
            } else {
                List<Server> servers = new ArrayList<>();
                servers.add(server);
                zoneServerMap.put(zone, servers);
            }
        });
        lbs.updateZoneServerMapping(zoneServerMap);

        // 指定当前的zone
        DeploymentContext deploymentContext = ConfigurationManager.getDeploymentContext();
        deploymentContext.setValue(DeploymentContext.ContextKey.zone, "华北");
        // 准备一个服务列表过滤器
        IClientConfig config = DefaultClientConfigImpl.getClientConfigWithDefaultValues(clientName);
        config.set(CommonClientConfigKey.EnableZoneAffinity, true);
        // 设置配置
        ConfigurationManager.getConfigInstance().setProperty(clientName + "." + config.getNameSpace() + ".zoneAffinity.maxLoadPerServer", 100);

        ZoneAffinityServerListFilter serverListFilter = new ZoneAffinityServerListFilter();
        serverListFilter.setLoadBalancerStats(lbs);
        serverListFilter.initWithNiwsConfig(config);

        // 从lbs里拿到一些监控数据
        monitor(lbs, serverListFilter);

        TimeUnit.SECONDS.sleep(500);
    }


    @Test
    public void fun10() throws InterruptedException {
        ServerListUpdater serverListUpdater = new PollingServerListUpdater();

        serverListUpdater.start(() -> {
            int coreThreads = serverListUpdater.getCoreThreads();
            String lastUpdate = serverListUpdater.getLastUpdate();
            int numberMissedCycles = serverListUpdater.getNumberMissedCycles();
            long durationSinceLastUpdateMs = serverListUpdater.getDurationSinceLastUpdateMs();
            System.out.println("===========上次的执行时间是：" + lastUpdate);
            System.out.println("自上次更新以来已经过的ms数：" + durationSinceLastUpdateMs);
            System.out.println("线程核心数：" + coreThreads);
            System.out.println("错过更新周期的数量：" + numberMissedCycles);

            // .... 执行你对Server列表的更新动作，本处略
        });

        TimeUnit.SECONDS.sleep(500);
    }
}
