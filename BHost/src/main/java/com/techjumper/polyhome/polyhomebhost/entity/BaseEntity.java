package com.techjumper.polyhome.polyhomebhost.entity;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/3/2
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 所有接口返回的结果
 */
public class BaseEntity<T> {

    /**
     * error_code : 101
     * error_msg : 手机号码不正确
     * data : {}
     * <p>
     * # 所有接口统一返回的 ERROR CODE
     * error_code: 100,	error_msg: '没有访问权限！' # 签名错误
     * error_code: 109,	error_msg: '此功能登录后可使用！'
     */

    private int error_code;
    private String error_msg;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public int getError_code() {
        return error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "error_code=" + error_code +
                ", error_msg='" + error_msg + '\'' +
                ", data=" + data +
                '}';
    }
}
