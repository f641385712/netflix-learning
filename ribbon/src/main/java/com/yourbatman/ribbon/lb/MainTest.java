package com.yourbatman.ribbon.lb;

import com.netflix.client.ClientRequest;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import org.junit.Test;

import java.net.URI;

/**
 * 在无任何协议的情况下  测试Ribbon
 *
 * @author yourbatman
 * @date 2020/3/14 21:27
 */
public class MainTest {

    @Test
    public void fun1() throws Exception {
        // client配置
        IClientConfig clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues("YourBatman");

        // Client客户端，用于发送请求（使用ClientConfig配置）
        // 因为木有LB功能，所以要不要IClientConfig没什么关系
        MyClient client = new MyClient();

        // 执行请求，获得响应
        MyResponse response = client.execute(createClientRequest(), null);
        System.out.println(response.isSuccess());
    }

    // @Test
    // public void fun2() throws Exception {
    //     // 1、负载均衡器lb
    //     NoOpLoadBalancer lb = new NoOpLoadBalancer();
    //     // 2、client配置
    //     IClientConfig clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues("YourBatman");
    //
    //     // 3、IClient客户端，用于发送请求（使用ClientConfig配置）
    //     MyClient client = new MyClient(lb, clientConfig);
    //
    //     // 4、发送请求 -> 带有负载均衡能力的发送请求
    //     // client.execute(createClientRequest(), null); // 一般不会直接调用此方法，否则没有负责均衡的能力就木有意义了
    //     IResponse response = client.executeWithLoadBalancer(createClientRequest());
    //     System.out.println(response.isSuccess());
    //     System.out.println(response.getRequestedURI());
    // }

    private ClientRequest createClientRequest() {
        ClientRequest clientRequest = new ClientRequest(URI.create("http://..."));
        return clientRequest;
    }

}
