package com.tuorong.interceptor;

/**
 * Created by Z先生 on 2019/11/19.
 */
public class SessionValidationException extends Exception{
    public SessionValidationException() {
    }

    public SessionValidationException(String message) {
        super(message);
    }
}
