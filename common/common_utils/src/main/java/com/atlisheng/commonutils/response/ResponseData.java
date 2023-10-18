package com.atlisheng.commonutils.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseData {
    @ApiModelProperty(value = "响应是否成功")//这个注解的信息会展示在swagger中
    private Boolean success;

    @ApiModelProperty(value = "服务器响应状态码")
    private Integer code;

    @ApiModelProperty(value = "响应信息")
    private String message;

    @ApiModelProperty(value = "服务器响应数据")
    private Map<String,Object> data=new HashMap<>();

    private ResponseData(){

    }

    /**
     * @return {@link ResponseData }
     * @描述 成功响应返回响应数据的静态方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    public static ResponseData responseCall(){
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(true);
        responseData.setCode(ResponseCode.SUCCESS);
        responseData.setMessage("响应成功");
        return responseData;
    }

    /**
     * @return {@link ResponseData }
     * @描述 异常响应调用方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    public static ResponseData responseErrorCall(){
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setCode(ResponseCode.ERROR);
        responseData.setMessage("响应异常");
        return responseData;
    }

    /**
     * @param success 成功
     * @return {@link ResponseData }
     * @描述 以下方法是为了方便链式编程，即对一个对象一顿.，就可以设置其中的属性，如responseData.success(true).message("响应成功").code("20000").data()
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    public ResponseData success(Boolean success){
        this.setSuccess(success);
        return this;
    }
    public ResponseData message(String message){
        this.setMessage(message);
        return this;
    }
    public ResponseData code(Integer code){
        this.setCode(code);
        return this;
    }
    public ResponseData data(String key, Object value){
        this.data.put(key, value);
        return this;
    }
    public ResponseData data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
