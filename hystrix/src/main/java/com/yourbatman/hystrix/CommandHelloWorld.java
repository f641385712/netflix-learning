package com.yourbatman.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class CommandHelloWorld extends HystrixCommand<String> {
    private final String name;

    // 指定一个HystrixCommandGroupKey，这样熔断策略会按照此组执行
    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("MyAppGroup"));
        this.name = name;
    }

    @Override
    protected String run() {
        if(name == null){
            // throw new NullPointerException();
            throw new HystrixBadRequestException("错误的请求");
        }
        return "Hello " + name + "!";
    }

    @Override
    protected String getFallback() {
        // super.getFallback()：No fallback available.
        return "this is fallback msg";
    }
}