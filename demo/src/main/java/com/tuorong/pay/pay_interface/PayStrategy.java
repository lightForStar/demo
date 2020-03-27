package com.tuorong.pay.pay_interface;

import java.util.Map;

/**
 * Created by Z先生 on 2019/11/25.
 */
public interface PayStrategy {
    Map<String, String> pay(Integer payType);
}
