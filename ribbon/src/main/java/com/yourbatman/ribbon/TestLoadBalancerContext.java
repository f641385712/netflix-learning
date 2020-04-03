package com.yourbatman.ribbon;

import com.netflix.client.ClientException;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerContext;
import com.netflix.loadbalancer.Server;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TestLoadBalancerContext {

    @Test
    public void fun1() throws ClientException {
        URI original = URI.create("http://account:3333");

        List<Server> serverList = new ArrayList<>();
        serverList.add(createServer("华南", 1));
        serverList.add(createServer("华东", 1));
        serverList.add(createServer("华东", 2));

        serverList.add(createServer("华北", 1));
        serverList.add(createServer("华北", 2));
        serverList.add(createServer("华北", 3));
        serverList.add(createServer("华北", 4));

        BaseLoadBalancer lb = new BaseLoadBalancer();
        lb.addServers(serverList);
        IClientConfig config = DefaultClientConfigImpl.getClientConfigWithDefaultValues("YourBatman");
        LoadBalancerContext loadBalancerContext = new LoadBalancerContext(lb, config);

        for (int i = 0; i < 5; i++) {
            System.out.println(loadBalancerContext.getServerFromLoadBalancer(original, null));
        }
    }

    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setZone(zone);
        return server;
    }

    public static void main(String[] args) {
        URI original = URI.create("www.baidu.com:8080");
        System.out.println(original.getScheme()); // www.baidu.com
        System.out.println(original.getHost()); // null
        System.out.println(original.getPort()); // -1
        System.out.println(original.getAuthority()); // null

        original = URI.create("tcp://www.baidu.com:8080");
        System.out.println(original.getScheme()); // tcp
        System.out.println(original.getHost()); // www.baidu.com
        System.out.println(original.getPort()); // 8080
        System.out.println(original.getAuthority()); // www.baidu.com:8080
    }
}
