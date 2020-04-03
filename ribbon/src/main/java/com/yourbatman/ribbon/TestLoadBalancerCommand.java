package com.yourbatman.ribbon;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerContext;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import org.junit.Test;
import rx.Observable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestLoadBalancerCommand {

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

        BaseLoadBalancer lb = new BaseLoadBalancer();
        lb.addServers(serverList);
        IClientConfig config = DefaultClientConfigImpl.getClientConfigWithDefaultValues("YourBatman");
        // 通过API方式设置重试次数
        config.set(CommonClientConfigKey.MaxAutoRetries, 2);
        config.set(CommonClientConfigKey.MaxAutoRetriesNextServer, 5);

        LoadBalancerContext loadBalancerContext = new LoadBalancerContext(lb, config);


        // 构建一个执行命令command
        // 说明：案例中请使用不含host的URI，因为有host的情况下将不会再使用ILoadBalancer去选择Server
        // （当然你若要它使用也行，就请配置vipAddress吧）
        URI original = URI.create("");
        // URI original = URI.create("http://account:3333");
        LoadBalancerCommand<String> command = LoadBalancerCommand.<String>builder()
                .withClientConfig(config)
                .withLoadBalancerContext(loadBalancerContext)
                .withLoadBalancerURI(original)
                // 自定义一个重拾器，让NPE也能触发异常  配置使用config的
                .withRetryHandler(new DefaultLoadBalancerRetryHandler(config) {
                    @Override
                    public boolean isRetriableException(Throwable e, boolean sameServer) {
                        boolean result = super.isRetriableException(e, sameServer);
                        return result || e instanceof NullPointerException;
                    }
                })
                // 注册一个监听器，监听执行的过程
                // .withListeners(Collections.singletonList(...))
                .build();

        // 执行目标方法/操作
        // 记录总重试次数
        AtomicInteger totalRetry = new AtomicInteger();
        Observable<String> submit = command.submit(server -> {
            System.out.println("第[" + totalRetry.incrementAndGet() + "]次发送请求，使用的Server是：" + server);

            // 模拟执行时出现异常（请注意：NPE等业务异常并不会触发重试~~~~~）
            // System.out.println(1 / 0);
            Integer i = null;
            System.out.println(i.toString());

            return Observable.just("hello success!!!");
        });

        // 监听且打印结果
        submit.doOnError(throwable -> System.out.println("执行失败，异常：" + throwable.getClass()))
                .subscribe(d -> System.out.println("执行成功，结果：" + d));

    }

    private Server createServer(String zone, int index) {
        Server server = new Server("www.baidu" + zone + ".com", index);
        server.setZone(zone);
        return server;
    }
}
