package com.techjumper.polyhome_b.bhostdaemon.entity;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/3
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class BaseArgumentsEntity {

    public static final String FILED_SIGN = "sign";
    public static final String FILED_DATA = "data";

    /**
     * sign : 加密后的签名信息
     * data : 转成json后的参数
     */


    public BaseArgumentsEntity(String sign, String data) {
        this.sign = sign;
        this.data = data;
    }

    private String sign;

    private String data;

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BaseArgumentsEntity{" +
                "sign='" + sign + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
