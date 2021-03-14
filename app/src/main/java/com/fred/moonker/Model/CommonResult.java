package com.fred.moonker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @auther fred
 * @create 2021-02-19 13:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private RetCode code;
    private String  message;
    private T       data;

    public CommonResult(RetCode code,String message){
        this(code,message,null);
    }

    //链式调用
    public CommonResult<T> addCode(RetCode code){
        this.setCode(code);
        return this;
    }

    public CommonResult<T> addMessage(String message){
        this.setMessage(message);
        return this;
    }

    public CommonResult<T> addData(T data){
        this.setData(data);
        return this;
    }
}
