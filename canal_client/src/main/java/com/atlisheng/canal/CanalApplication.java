package com.atlisheng.canal;

import com.atlisheng.canal.client.CanalClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 当前库需要和远程库做同步，要保证程序一直监控远程库的变化，
 * 要保证当前服务一直处于启动状态，监控程序要一直在运行【CanalClient的run方法就是监控方法】，
 * 实现办法是让启动类实现CommandLineRunner接口，CommandLineRunner接口的run方法只要服务一直在执行，run方法就会一直执行
 * @创建日期 2023/10/15
 * @since 1.0.0
 */
@SpringBootApplication
public class CanalApplication implements CommandLineRunner {

    @Resource
    private CanalClient canalClient;

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //项目启动，执行canal客户端监听
        canalClient.run();
    }

}
