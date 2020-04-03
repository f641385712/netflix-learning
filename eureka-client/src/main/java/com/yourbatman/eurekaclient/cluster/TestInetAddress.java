package com.yourbatman.eurekaclient.cluster;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddress {

    @Test
    public void fun0() throws UnknownHostException {
        // 同时指定域名 和 ip地址，那就是自己建立了对应关系喽
        InetAddress inet = InetAddress.getByAddress("www.baidu.com", new byte[]{61, (byte) 135, (byte) 169, 125});
        System.out.println("域名：" + inet.getHostName()); // 域名：www.baidu.com
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：61.135.169.125
    }

    @Test
    public void fun1() throws UnknownHostException {
        InetAddress inet = InetAddress.getByAddress(new byte[]{61, (byte) 135, (byte) 169, 125});
        // 若你要获取主机名，就尝试通过网络帮你找，所以一般比较耗时，不建议使用。  找不到就原样输出
        // System.out.println("域名：" + inet.getHostName()); // 域名：61.135.169.125
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：61.135.169.125
    }

    @Test
    public void fun2() throws UnknownHostException {
        // 网络域名
        InetAddress inet = InetAddress.getByName("www.baidu.com");
        System.out.println("域名：" + inet.getHostName()); // 域名：www.baidu.com
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：61.135.169.125

        // 本地域名（本机）
        inet = InetAddress.getByName("localhost");
        System.out.println("域名：" + inet.getHostName()); // 域名：localhost
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：127.0.0.1

        // 不存在的域名 抛出异常：java.net.UnknownHostException: aaaaaa.com
        // tips：abc.com这种域名是存在的哟
        inet = InetAddress.getByName("aaaaaa.com");
        System.out.println("域名：" + inet.getHostName());
        System.out.println("IP地址：" + inet.getHostAddress());
    }

    @Test
    public void fun3() throws UnknownHostException {
        InetAddress[] inets = InetAddress.getAllByName("www.baidu.com");
        for (InetAddress inet : inets) {
            // www.baidu.com/61.135.169.125
            // www.baidu.com/61.135.169.121
            System.out.println(inet);
        }
    }

    @Test
    public void fun4() {
        InetAddress inet = InetAddress.getLoopbackAddress();
        System.out.println("域名：" + inet.getHostName()); // 域名：localhost
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：127.0.0.1
    }

    @Test
    public void fun5() throws UnknownHostException {
        InetAddress inet = InetAddress.getLocalHost();
        System.out.println("域名：" + inet.getHostName()); // LP-BJ4556
        System.out.println("IP地址：" + inet.getHostAddress()); // IP地址：2.0.0.137
    }


    @Test
    public void fun7() throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName("114.249.196.58");
        for (int i = 0; i < addresses.length; i++) {
            String hostname = addresses[i].getHostName();
            System.out.println(hostname);
        }
    }


    @Test
    public void fun100() throws UnknownHostException {
        System.out.println("-----第一种方式-------");
        // 第一种方法：通过域名来获取IP对象（包括域名+IP地址）
        InetAddress inet1 = InetAddress.getByName("www.baidu.com");
        System.out.println("IP对象：" + inet1);
        // 获取对应的IP
        System.out.println("域名：" + inet1.getHostName());
        System.out.println("IP地址：" + inet1.getHostAddress());

        System.out.println("-----第二种方式-------");
        // 第二种方法：请注意后面byte数组的写法(参看：https://blog.csdn.net/ling376962380/article/details/72824880)
        InetAddress inet2 = InetAddress.getByAddress("www.baidu.com", new byte[]{(byte) 180, 97, 33, 107});
        System.out.println("IP对象：" + inet2);
        System.out.println("域名：" + inet2.getHostName());
        System.out.println("IP地址：" + inet2.getHostAddress());

        System.out.println("-----第三种方式-------");
        // 第三种方法：通过IP地址字符串
        InetAddress inet3 = InetAddress.getByName("180.97.33.107");
        System.out.println("IP对象：" + inet3);
        System.out.println("域名：" + inet3.getHostName());
        System.out.println("IP地址：" + inet3.getHostAddress());

        System.out.println("-----第四种方式-------");
        // 第四种方法：通过IP地址字符串
        InetAddress inet4 = InetAddress.getByAddress(new byte[]{(byte) 180, 97, 33, 107});
        System.out.println("IP对象：" + inet4);
        System.out.println("域名：" + inet4.getHostName());
        System.out.println("IP地址：" + inet4.getHostAddress());

        System.out.println("------获取本机的----");
        InetAddress inet5 = InetAddress.getLocalHost();
        System.out.println("IP对象：" + inet5);
        System.out.println("域名：" + inet5.getHostName());
        System.out.println("IP地址：" + inet5.getHostAddress());

        System.out.println("----获取回环地址----");
        InetAddress inet6 = InetAddress.getLoopbackAddress();
        System.out.println("IP对象：" + inet6);
        System.out.println("域名：" + inet6.getHostName());
        System.out.println("IP地址：" + inet6.getHostAddress());
    }
}
