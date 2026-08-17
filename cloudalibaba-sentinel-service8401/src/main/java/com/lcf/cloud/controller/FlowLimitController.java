package com.lcf.cloud.controller;

import com.lcf.cloud.service.FlowLimitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class FlowLimitController {

    @Resource
    private FlowLimitService flowLimitService;

    @GetMapping("/testA")
    public String testA(){
        return "-----------testA";
    }

    @GetMapping("/testB")
    public String testB(){
        return "-----------testB";
    }

    @GetMapping("/testC")
    public String testC(){
        flowLimitService.common();
        return "-----------testC";
    }

    @GetMapping("/testD")
    public String testD(){
        flowLimitService.common();
        return "-----------testD";
    }

    @GetMapping("/testE")
    public String testE(){
        System.out.println(System.currentTimeMillis() + "  testE,流控效果----排队等待");
        return "-----------testE";
    }

    @GetMapping("/testF")
    public String testF(){

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("----测试：新增熔断规则-慢调用比例");
        return "-----------testF 新增熔断规则-慢调用比例";
    }

    @GetMapping("/testG")
    public String testG(){
        System.out.println("----测试：新增熔断规则-异常比例");
        int age = 10/0;
        return "-----------testG 新增熔断规则-异常比例";
    }

    @GetMapping("/testH")
    public String testH(){
        System.out.println("----测试：新增熔断规则-异常数");
        int age = 10/0;
        return "-----------testG 新增熔断规则-异常数";
    }
}
