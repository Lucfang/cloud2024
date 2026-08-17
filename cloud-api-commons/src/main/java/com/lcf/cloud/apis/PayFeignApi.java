package com.lcf.cloud.apis;

import com.lcf.cloud.entities.PayDTO;
import com.lcf.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient("cloud-payment-service")
@FeignClient("cloud-gateway")
public interface PayFeignApi {

    @PostMapping("/pay/add")
    ResultData addPay(@RequestBody PayDTO payDTO);

    @GetMapping("/pay/get/{id}")
    ResultData getPayInfo(@PathVariable("id") Integer id);

    @GetMapping("/pay/get/info")
    String mylb();

    /**
     * Resilience4j CircuitBreaker的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/circuit/{id}")
    String myCircuit(@PathVariable("id") Integer id);

    /**
     * Resilience4j Bulkhead的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/bulkhead/{id}")
    String myBulkhead(@PathVariable("id") Integer id);

    /**
     * Resilience4j Ratelimit的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/ratelimit/{id}")
    String myRatelimit(@PathVariable("id") Integer id);

    /**
     * Micrometer(Sleuth)进行链路监控的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/micrometer/{id}")
    String myMicrometer(@PathVariable("id") Integer id);

    /**
     * Gateway 进行网关测试案例01
     * @param id
     * @return
     */
    @GetMapping("/pay/gateway/get/{id}")
    ResultData getById(@PathVariable("id") Integer id);

    /**
     * Gateway 进行网关测试案例02
     * @return
     */
    @GetMapping(value = "/pay/gateway/info")
    ResultData<String> getGatewayInfo();
}
