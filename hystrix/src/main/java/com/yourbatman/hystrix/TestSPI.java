package com.yourbatman.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixDynamicProperties;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestSPI {

    @Test
    public void fun1() {
        HystrixPlugins instance = HystrixPlugins.getInstance();
        HystrixDynamicProperties dynamicProperties = instance.getDynamicProperties();
        System.out.println(dynamicProperties.getString("name", null).get());

        System.out.println("===========================================");
        // 类型
        System.out.println(dynamicProperties.getClass());
        System.out.println(instance.getMetricsPublisher().getClass());
        System.out.println(instance.getEventNotifier().getClass());
        System.out.println(instance.getConcurrencyStrategy().getClass());
    }

    // [1,2,3,4,5] [6,7,8,9,10] [0,2,9,9,0]
    public static void main(String[] args) {
        List<List<Integer>> lists = new ArrayList<>();
        lists.add(Arrays.asList(1, 2, 3, 4, 5));
        lists.add(Arrays.asList(6, 7, 8, 9, 10));
        lists.add(Arrays.asList(0, 2, 9, 9, 0));

        List<Integer> result = IntStream.range(0, lists.get(0).size())
                .map(index -> {
                    int sum = 0;
                    for (List<Integer> list : lists)
                        sum += list.get(index);
                    return sum;
                }).boxed().collect(Collectors.toList());
        System.out.println(result);
    }

}
