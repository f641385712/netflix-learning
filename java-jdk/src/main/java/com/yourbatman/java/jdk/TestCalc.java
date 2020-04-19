package com.yourbatman.java.jdk;

import org.junit.Test;

import java.math.BigDecimal;

public class TestCalc {

    @Test
    public void fun1() {
        BigDecimal count = new BigDecimal(0);
        for (int i = 1; i <= 240; i++) {
            count = count.add(cacl(i));
        }
        System.out.println("20年共还款额：" + count.doubleValue());
    }


    private static BigDecimal cacl(int n) {
        // return 6 + 6 * (n - 1) * 0.05;
        if (n == 1) {
            return new BigDecimal(6);
        }
        return cacl(n - 1).multiply(new BigDecimal(1 + 0.05));
    }
}
