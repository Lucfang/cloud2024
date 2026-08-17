package com.lcf.cloud.controller;

import com.lcf.cloud.entities.Pay;
import com.lcf.cloud.entities.PayDTO;
import com.lcf.cloud.resp.ResultData;
import com.lcf.cloud.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Tag(name = "支付微服务模块",description="支付CRUD")
public class PayController {

    @Autowired
    @Resource
    private PayService payService;

    @Value("${server.port}")
    private String port;

    @PostMapping(value = "/pay/add")
    @Operation(summary = "新增",description = "新增支付流水方法，json做参数")
    public ResultData<String> add(@RequestBody Pay pay){
        log.info(pay.toString());
        int i =  payService.add(pay);
        return ResultData.success("成功插入记录，返回值：" + i);
    }

    @DeleteMapping(value = "/pay/del/{id}")
    public ResultData<Integer> delete(@PathVariable("id") Integer id){
        return ResultData.success(payService.delete(id));
    }

    @PutMapping("/pay/update")
    public ResultData<String> update(@RequestBody PayDTO payDTO){
        Pay pay = new Pay();
        BeanUtils.copyProperties(payDTO,pay);
        int i = payService.update(pay);

        return ResultData.success("成功修改记录,返回值：" + i);
    }

    @GetMapping(value = "/pay/get/{id}")
    public ResultData<Pay> getById(@PathVariable("id") Integer id){
        try{
            TimeUnit.SECONDS.sleep(62);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResultData.success(payService.getById(id));
    }

    @GetMapping(value = "/pay/getAll")
    public ResultData<List<Pay>> getAll(){
        return ResultData.success(payService.getAll());
    }

    @GetMapping("/pay/get/info")
    public String getInfoByConsul(@Value("${lcf.info}") String info){
        return "info:" + info + "& port:"+port;
    }
}
