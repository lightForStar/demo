package com.tuorong.pay;

import com.tuorong.pay.pay_interface.PayStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Z先生 on 2019/11/29.
 */
@Component
public class PayContext {
    private Map<String,PayStrategy> payStrategyMap = new ConcurrentHashMap<>();

    @Autowired
    public PayContext(Map<String, PayStrategy> payStrategyMap) {
        this.payStrategyMap.clear();
        this.payStrategyMap = payStrategyMap;
        System.out.println(this.payStrategyMap);
    }

    public PayStrategy getPayStrategyByName(String name){
        return payStrategyMap.get(name);
    }
}
