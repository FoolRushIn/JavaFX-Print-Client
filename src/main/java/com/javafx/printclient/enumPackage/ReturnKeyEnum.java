package com.javafx.printclient.enumPackage;

/**
 * 异步请求返回码枚举定义
 *
 * @author shi_zujun
 * @date 2017年8月18日 下午5:00:22
 */
public enum ReturnKeyEnum {

    成功("ok"), 异常("error");

    private String value;

    ReturnKeyEnum(String value) {
        this.value = value;
    }

    /**
     * 重写toString()方法
     */
    @Override
    public String toString() {
        return value;
    }

}
