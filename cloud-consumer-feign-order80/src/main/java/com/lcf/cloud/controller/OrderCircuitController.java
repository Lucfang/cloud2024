package com.lcf.cloud.controller;

import com.lcf.cloud.apis.PayFeignApi;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
public class OrderCircuitController {

    @Resource
    private PayFeignApi payFeignApi;

    @GetMapping("/feign/pay/circuit/{id}")
    @CircuitBreaker(name = "cloud-payment-service",fallbackMethod = "myCircuitFallback")
    public String myCircuitBreaker(@PathVariable("id") Integer id){
        return payFeignApi.myCircuit(id);
    }

    public String myCircuitFallback(Integer id,Throwable t){
        return "myCircuitFallback,系统繁忙，请稍后再试-----/(ToT)/~~";
    }

    /**
     * (船的)舱壁，隔离 Semaphore
     * @param id
     * @return
     */
//    @GetMapping("/feign/pay/bulkhead/{id}")
//    @Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadFallback",type = Bulkhead.Type.SEMAPHORE)
//    public String myBulkhead(@PathVariable("id") Integer id){
//        return payFeignApi.myBulkhead(id);
//    }
//
//    public String myBulkheadFallback(Integer id,Throwable t){
//        return "myBulkheadFallback,隔板超出最大数量限制，系统繁忙，请稍后再试-----/(ToT)/~~";
//    }

    /**
     * (船的)舱壁，隔离 ThreadPool
     * @param id
     * @return
     */
    @GetMapping("/feign/pay/bulkhead/{id}")
    @Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadPoolFallback",type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> myBulkhead(@PathVariable("id") Integer id){
        System.out.println(Thread.currentThread().getName() + "\t" + "-----开始进入");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "\t" + "-----准备离开");
        return CompletableFuture.supplyAsync(() -> payFeignApi.myBulkhead(id) + "\t" + "Bulkhead.Type.THREADPOOL");
    }

    public CompletableFuture<String> myBulkheadPoolFallback(Integer id,Throwable t){
        return CompletableFuture.supplyAsync(()->"Bulkhead.Type.THREADPOOL,系统繁忙，请稍后再试-----/(ToT)/~~");
    }

    @GetMapping("/feign/pay/ratelimit/{id}")
    @RateLimiter(name="cloud-payment-service",fallbackMethod="myRatelimitFallback")
    public String myRatelimit(@PathVariable("id") Integer id){
        return payFeignApi.myRatelimit(id);
    }

    public String myRatelimitFallback(Integer id,Throwable t){
        return "你被限流了，禁止访问/(ToT)/~~";
    }
}
