package com.tuorong.pay.strategy;

import com.tuorong.pay.pay_interface.PayStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Z先生 on 2019/11/25.
 */
@Component(value = "WXPublicPay")
public class WXPublicAppPay implements PayStrategy {


    @Override
    public Map<String, String> pay(Integer payType) {
        System.out.println("公众号支付");
        return null;
    }
}
