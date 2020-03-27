package com.tuorong.pay.strategy;

import com.tuorong.pay.pay_interface.PayStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Z先生 on 2019/11/25.
 */
@Component(value = "AliPay")
public class AliPay implements PayStrategy {


    @Override
    public Map<String, String> pay(Integer payType) {
        System.out.println("阿里支付");
        return null;
    }
}
