package com.example.springcloud.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lak
 * @date 2020/3/5 21:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResut<T> {
    private String code;
    private String msg;
    private T data;

    public CommonResut(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
