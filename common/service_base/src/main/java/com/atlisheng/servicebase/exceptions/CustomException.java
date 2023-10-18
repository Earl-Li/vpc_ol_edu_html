package com.atlisheng.servicebase.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException{
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 一张信息
     */
    private String message;
}
