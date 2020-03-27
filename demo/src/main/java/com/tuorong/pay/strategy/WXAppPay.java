package com.tuorong.pay.strategy;

import com.tuorong.pay.pay_interface.PayStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Z先生 on 2019/11/25.
 */
@Component(value = "WXAppPay")
public class WXAppPay implements PayStrategy {


    @Override
    public Map<String, String> pay(Integer payType) {
        System.out.println("微信app支付");
        return null;
    }
}
