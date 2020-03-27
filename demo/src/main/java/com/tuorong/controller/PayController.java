package com.tuorong.controller;

import com.tuorong.pay.PayContext;
import com.tuorong.pay.pay_interface.PayStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Z先生 on 2019/11/29.
 */
@RestController
public class PayController {
    @Autowired
    private PayContext payContext;
    @RequestMapping("/pay")
    public String pay(){
        PayStrategy payStrategy = payContext.getPayStrategyByName("WXAppPay");
        payStrategy.pay(0);
        return "success";

    }
}
