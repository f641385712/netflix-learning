package com.yourbatman.ribbon;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestLoadBalancer {

    @Test
    public void fun1() {
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));

        ILoadBalancer lb = new BaseLoadBalancer();
        lb.addServers(serverList);

        // 把华北的机器都标记为down掉
        LoadBalancerStats loadBalancerStats = ((BaseLoadBalancer) lb).getLoadBalancerStats();
        loadBalancerStats.updateServerList(serverList); // 这一步不能省哦~~~
        loadBalancerStats.getServerStats().keySet().forEach(server -> {
            if (server.getHost().contains("华北")) {
                lb.markServerDown(server);
            }
        });

        for (int i = 0; i < 5; i++) {
            System.out.println(lb.chooseServer(null));
        }
    }

    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setZone(zone);
        return server;
    }
}
