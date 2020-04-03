package com.yourbatman.ribbon;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestRule {

    @Test
    public void fun1() throws InterruptedException {
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));

        // 轮询策略：因为Servers均来自于lb，所以必须关联上一个lb实例哦
        ILoadBalancer lb = new BaseLoadBalancer();
        lb.addServers(serverList);
        RoundRobinRule rule = new RoundRobinRule(lb);
        while (true) {
            System.out.println(rule.choose(null));
            TimeUnit.SECONDS.sleep(2);
        }
    }


    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setAlive(true);
        server.setReadyToServe(true);
        server.setZone(zone);
        return server;
    }

    @Test
    public void fun2() throws InterruptedException {
        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));

        // 轮询策略：因为Servers均来自于lb，所以必须关联上一个lb实例哦
        BaseLoadBalancer lb = new BaseLoadBalancer();
        // 自己实现ping的逻辑：只留下指定的Server
        lb.setPing(server -> {
            return server.getPort() % 10 > 2;
        });
        lb.addServers(serverList);

        System.out.println("server总数："+lb.getAllServers().size());
        System.out.println("up的总数："+lb.getReachableServers().size());


        RandomRule rule = new RandomRule();
        rule.setLoadBalancer(lb);

        while (true) {
            System.out.println(rule.choose(null));
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
