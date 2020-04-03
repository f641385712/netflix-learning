package com.yourbatman.ribbon.lb;

import com.netflix.client.ClientRequest;
import com.netflix.client.IClient;
import com.netflix.client.config.IClientConfig;

/**
 * 不具有负载均衡功能的一个Client
 *
 * @author yourbatman
 * @date 2020/3/14 22:09
 */
public class MyClient implements IClient<ClientRequest, MyResponse> {

    @Override
    public MyResponse execute(ClientRequest request, IClientConfig requestConfig) throws Exception {
        MyResponse response = new MyResponse();
        response.setRequestUri(request.getUri());
        return response;
    }
}
