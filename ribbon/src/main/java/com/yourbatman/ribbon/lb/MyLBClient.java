// package com.yourbatman.ribbon.lb;
//
// import com.netflix.client.AbstractLoadBalancerAwareClient;
// import com.netflix.client.ClientRequest;
// import com.netflix.client.IResponse;
// import com.netflix.client.RequestSpecificRetryHandler;
// import com.netflix.client.RetryHandler;
// import com.netflix.client.config.IClientConfig;
// import com.netflix.loadbalancer.ILoadBalancer;
//
// /**
//  * 自己实现的Client：用于发送请求（非Http，用本地直接模拟远程调用）
//  *
//  * @author yourbatman
//  * @date 2020/3/14 21:41
//  */
// public class MyLBClient extends AbstractLoadBalancerAwareClient {
//
//     public MyLBClient(ILoadBalancer lb, IClientConfig clientConfig) {
//         super(lb, clientConfig);
//     }
//
//     // 准备一个重试处理器RetryHandler
//     @Override
//     public RequestSpecificRetryHandler getRequestSpecificRetryHandler(ClientRequest request, IClientConfig requestConfig) {
//         // okToRetryOnConnectErrors：false
//         // okToRetryOnAllErrors：true  -> 遇上任何抛错都进行重试，非常干脆
//         // baseRetryHandler：RetryHandler.DEFAULT
//         // requestConfig：requestConfig
//         return new RequestSpecificRetryHandler(false, true, RetryHandler.DEFAULT, requestConfig);
//     }
//
//     @Override
//     public IResponse execute(ClientRequest request, IClientConfig requestConfig) throws Exception {
//         MyResponse response = new MyResponse();
//         response.setRequestUri(request.getUri());
//         return response;
//     }
// }
