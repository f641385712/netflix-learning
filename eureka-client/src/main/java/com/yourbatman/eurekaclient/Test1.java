package com.yourbatman.eurekaclient;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.guice.EurekaModule;
import com.netflix.discovery.internal.util.Archaius1Utils;
import com.netflix.discovery.shared.resolver.DefaultEndpoint;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl;
import com.netflix.discovery.shared.transport.jersey.JerseyApplicationClient;
import com.netflix.discovery.shared.transport.jersey.JerseyEurekaHttpClientFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test1 {

    @Test
    public void fun1() throws InterruptedException {
        // 准备一个配置
        EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
        EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();


        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account")
                .build();
        ApplicationInfoManager manager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        DiscoveryClient client = new DiscoveryClient(manager, clientConfig);

        client.getApplications();


        TimeUnit.MINUTES.sleep(100);
    }

    @Test
    public void fun2() {
        // Client jerseyClient = Client.create();
        ApacheHttpClient4 jerseyClient = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder().build().getClient();


        JerseyApplicationClient client = new JerseyApplicationClient(jerseyClient, "http://localhost:8761/eureka", new HashMap<>());
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account")
                .build();
        EurekaHttpResponse<Void> response = client.register(instanceInfo);
        System.out.println(response.getStatusCode());

    }

    @Test
    public void fun3() {
        DynamicPropertyFactory factory = Archaius1Utils.initConfig(null);

        System.out.println(factory.getStringProperty("xxx.name", "").get());
        System.out.println(factory.getStringProperty("xxx.age", "").get());
    }

    @Test
    public void fun4() {
        // 使用它就得准备一个AmazonInfo配置AmazonInfoConfig 默认使用的Archaius1AmazonInfoConfig
        // 因此也是直接放在配置文件里即可
        CloudInstanceConfig instanceConfig = new CloudInstanceConfig();

        System.out.println(instanceConfig.getInstanceId());
        System.out.println(instanceConfig.isInstanceEnabledOnit());
        System.out.println(instanceConfig.getSecurePortEnabled());
    }

    public static void main(String[] args) {
        // Client jerseyClient = Client.create();
        ApacheHttpClient4 jerseyClient = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder()
                .withMaxConnectionsPerHost(2000)
                .withConnectionTimeout(2000)
                .withReadTimeout(2000)
                .withClientName("account")
                .build().getClient();

        JerseyApplicationClient client = new JerseyApplicationClient(jerseyClient, "http://127.0.0.1:8761/eureka/", new HashMap<>());
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001l")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account") // 大小写无所谓
                .build();
        EurekaHttpResponse<Void> response = client.register(instanceInfo);
        System.out.println(response.getStatusCode());
    }

    @Test
    public void fun5() {
        Injector injector = Guice.createInjector(new EurekaModule());

        InstanceInfo instance1 = injector.getInstance(InstanceInfo.class);
        InstanceInfo instance2 = injector.getInstance(InstanceInfo.class);
        System.out.println(instance1.getId());
        System.out.println(instance2.getId());

        System.out.println(System.identityHashCode(instance1));
        System.out.println(System.identityHashCode(instance2));
    }

    @Test
    public void fun6() {
        EurekaEndpoint endpoint = new DefaultEndpoint("http://192.168.1.100:8080/eureka/");
        System.out.println(endpoint.getServiceUrl()); // http://localhost:8080/eureka/
        System.out.println(endpoint.getNetworkAddress()); // 192.168.1.100
        System.out.println(endpoint.getHostName()); // 192.168.1.100
        System.out.println(endpoint.getPort()); // 8080
        System.out.println(endpoint.isSecure()); // false
        System.out.println(endpoint.getRelativeUri()); // /eureka/


        // 静态方法测试  isSecure=true那用的就是https
        List<EurekaEndpoint> endpoints = DefaultEndpoint.createForServerList(Arrays.asList("192.168.1.1", "192.168.1.2"), 8080, true, "/eureka/");
        // [DefaultEndpoint{ serviceUrl='https://192.168.1.1:8080/eureka/}, DefaultEndpoint{ serviceUrl='https://192.168.1.2:8080/eureka/}]
        System.out.println(endpoints);
    }

    @Test
    public void fun7() throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        CloseableHttpResponse response = httpClient.execute(new HttpGet("https://www.baidu.com"));
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void fun8() {
        EurekaJerseyClient jerseyClient = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder()
                .withClientName("YoutBatman-Client")
                .withConnectionTimeout(2000)
                .withReadTimeout(3000)
                .withConnectionIdleTimeout(30 * 60 * 1000)
                .withMaxConnectionsPerHost(50)
                //十分注意：此值必须设定，否则将获取不到连接（超时）
                .withMaxTotalConnections(200)
                .build();

        // 发送Http请求客户端
        ApacheHttpClient4 client = jerseyClient.getClient();
        // HttpClient httpClient = client.getClientHandler().getHttpClient();

        WebResource.Builder resourceBuilder = client.resource("http://www.baidu.com").path("").getRequestBuilder();
        ClientResponse response = resourceBuilder
                // .header("Accept-Encoding", "gzip") // 若开启了这个，对方就会以gzip的形式返回，你就得会解码才行，否则乱码哦~~~
                // .type(MediaType.APPLICATION_JSON_TYPE)
                // .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        System.out.println("响应码：" + response.getStatus());
        System.out.println("响应体：" + response.getEntity(String.class));

        jerseyClient.destroyResources();
    }

    @Test
    public void fun9() {
        EurekaJerseyClient jerseyClient = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder()
                .withClientName("YoutBatman-Client")
                .withConnectionTimeout(2000)
                .withReadTimeout(3000)
                .withConnectionIdleTimeout(30 * 60 * 1000)
                .withMaxConnectionsPerHost(50)
                //十分注意：此值必须设定，否则将获取不到连接（超时）
                .withMaxTotalConnections(200)
                .build();
        JerseyApplicationClient client = new JerseyApplicationClient(jerseyClient.getClient(), "http://localhost:8761/eureka/", null);

        // 服务注册  构建的实例是InstanceInfo
        EurekaHttpResponse<Void> response = client.register(InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account") // 大小写无所谓
                .build());
        System.out.println("注册成功，状态码：" + response.getStatusCode());
    }


    @Test
    public void fun10() {
        Client jerseyClient = Client.create();

        JerseyApplicationClient client = new JerseyApplicationClient(jerseyClient, "http://localhost:8761/eureka/", null);

        EurekaHttpResponse<Void> response = client.register(InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-001")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account") // 大小写无所谓
                .build());
        System.out.println("注册成功，状态码：" + response.getStatusCode());
    }


    @Test
    public void fun11() {
        // TransportClientFactory factory = JerseyEurekaHttpClientFactory.create();
        TransportClientFactory factory = JerseyEurekaHttpClientFactory.newBuilder()
                .withClientName("YoutBatman-Client") //必填
                .build();
        // 准备远端Server的地址
        EurekaEndpoint endpoint = new DefaultEndpoint("http://localhost:8761/eureka/");
        EurekaHttpClient client = factory.newClient(endpoint);


        // 注册服务
        EurekaHttpResponse<Void> response = client.register(InstanceInfo.Builder.newBuilder()
                .setInstanceId("account-002")
                .setHostName("localhost")
                .setIPAddr("127.0.0.1")
                .setDataCenterInfo(new MyDataCenterInfo(DataCenterInfo.Name.MyOwn))
                .setAppName("account") // 大小写无所谓
                .build());
        System.out.println("注册成功，状态码：" + response.getStatusCode());
    }

}
