package com.tuorong.utils;

import com.tuorong.model.Result;

/**
 * Created by Z先生 on 2019/11/8.
 */
public class ResultUtil {

    private static Result<Object> result = new Result<>();

    public static Result<Object> success(){
        result.setStatus(200);
        result.setMsg("操作成功");
        result.setData(null);
        return result;
    }

    public static Result<Object> success(Object data){
        result.setStatus(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static Result<Object> fail(){
        result.setStatus(500);
        result.setMsg("操作失败");
        result.setData(null);
        return result;
    }

    public static Result<Object> fail(String message){
        result.setStatus(500);
        result.setMsg(message);
        result.setData(null);
        return result;
    }
}
