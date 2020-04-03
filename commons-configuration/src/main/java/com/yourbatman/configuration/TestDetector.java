package com.yourbatman.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.reloading.FileHandlerReloadingDetector;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.commons.configuration2.reloading.ReloadingEvent;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestDetector {

    @Test
    public void fun1() throws InterruptedException {
        // 关联上1.properties这个文件
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", "1.properties");
        // 因fileHandler此例不需要FileBased，所以先用null吧
        FileHandler fileHandler = new FileHandler(null, FileHandler.fromMap(map));

        // 构建一个detector实例
        ReloadingDetector detector = new FileHandlerReloadingDetector(fileHandler);

        while (true) {
            if (detector.isReloadingRequired()) {
                System.out.println("====文件被修改了====程序退出。。。");
                break;
            } else {
                TimeUnit.SECONDS.sleep(20);
                System.out.println("文件没有修改。。。");
            }
        }

    }

    @Test
    public void fun2() throws InterruptedException {
        // 关联上1.properties这个文件
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", "1.properties");
        // 因fileHandler此例不需要FileBased，所以先用null吧
        FileHandler fileHandler = new FileHandler(null, FileHandler.fromMap(map));

        // 使用控制器ReloadingController 代理掉ReloadingDetector来使用，更好用
        ReloadingController reloadingController = new ReloadingController(new FileHandlerReloadingDetector(fileHandler));
        reloadingController.addEventListener(ReloadingEvent.ANY, event -> {
            ReloadingController currController = event.getController();
            Object data = event.getData();
            currController.resetReloadingState(); // 需要手动充值一下，否则下次文件改变就不会发送此事件啦
            System.out.println((reloadingController == currController) + " data：" + data);
        });


        while (true) {
            if (reloadingController.checkForReloading("自定义数据")) {
                System.out.println("====文件被修改了====触发重载事件，然后程序退出。。。");
                break;
            } else {
                TimeUnit.SECONDS.sleep(20);
                System.out.println("文件没有修改。。。");
            }
        }
    }

    @Test
    public void fun22()  {
        // 关联上1.properties这个文件
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", "1.properties");
        // 因fileHandler此例不需要FileBased，所以先用null吧
        FileHandler fileHandler = new FileHandler(null, FileHandler.fromMap(map));

        // 使用控制器ReloadingController 代理掉ReloadingDetector来使用，更好用
        ReloadingController reloadingController = new ReloadingController(new FileHandlerReloadingDetector(fileHandler));
        reloadingController.addEventListener(ReloadingEvent.ANY, event -> {
            ReloadingController currController = event.getController();
            Object data = event.getData();
            currController.resetReloadingState(); // 需要手动充值一下，否则下次文件改变就不会发送此事件啦
            System.out.println((reloadingController == currController) + " data：" + data);
        });

        // 准备定时器：用于监控文件的的变化：3秒看一次  注意一定要start()才能生效哦
        new PeriodicReloadingTrigger(reloadingController, "自定义数据", 3, TimeUnit.SECONDS).start();
        // hold住主线程
        while (true) { }
    }


    /**
     * 文件热加载最佳实践
     */
    @Test
    public void fun3() throws ConfigurationException, InterruptedException {
        // 准备Builder，并且持有期引用，方便获取到重载后的内容
        // 已自动帮绑定好`ReloadingBuilderSupportListener`监听器：因此具有重复一直检测的能力
        ReloadingFileBasedConfigurationBuilder builder = new ReloadingFileBasedConfigurationBuilder(PropertiesConfiguration.class);
        builder.configure(new PropertiesBuilderParametersImpl().setFileName("reload.properties"));


        // 准备定时器：用于监控文件的的变化：3秒看一次  注意一定要start()才能生效哦
        new PeriodicReloadingTrigger(builder.getReloadingController(), "自定义数据", 3, TimeUnit.SECONDS).start();

        // 查看文件变化  10秒钟去获取一次
        while (true) {
            Configuration configuration = (Configuration) builder.getConfiguration();
            System.out.println("====config hashCode：" + configuration.hashCode());
            ConfigurationUtils.dump(configuration, System.out);
            System.out.println();
            TimeUnit.SECONDS.sleep(8);
        }
    }

    @Test
    public void fun4() throws ConfigurationException, InterruptedException {
        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setEncoding("UTF-8")
                        .setFileName("reload.properties")
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        .setReloadingRefreshDelay(2000L)
                        .setThrowExceptionOnMissing(true));
        new PeriodicReloadingTrigger(builder.getReloadingController(), "自定义数据", 3, TimeUnit.SECONDS).start();


        // 查看文件变化  10秒钟去获取一次
        while (true) {
            Configuration configuration = (Configuration) builder.getConfiguration();
            System.out.println("====config hashCode：" + configuration.hashCode());
            ConfigurationUtils.dump(configuration, System.out);
            System.out.println();
            TimeUnit.SECONDS.sleep(8);
        }
    }
}
