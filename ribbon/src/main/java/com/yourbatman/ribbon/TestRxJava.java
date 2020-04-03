package com.yourbatman.ribbon;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class TestRxJava {

    @Test
    public void fun1() throws InterruptedException {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap(i -> Observable.just(i).delay(10, TimeUnit.MILLISECONDS))
                .subscribe(d -> System.out.println(d));

        System.out.println("----------end-----------");
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void fun2() throws InterruptedException {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .concatMap(i -> Observable.just(i).delay(10, TimeUnit.MILLISECONDS))
                .subscribe(d -> System.out.println(d));

        System.out.println("----------end-----------");
        TimeUnit.SECONDS.sleep(2);
    }


}
