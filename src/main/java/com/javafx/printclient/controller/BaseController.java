package com.javafx.printclient.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BaseController
 * @Description base控制器
 * @Author heziyuan
 * @Version 1.0
 **/
public abstract class BaseController {
    public static Map<String, BaseController> BC_CONTEXT = new HashMap<>();

    public BaseController() {
        BC_CONTEXT.put(this.getClass().getName(), this);
    }

    //更新ui界面,展示结果
    public void showResult(Object data, int flag){}

    //初始化数据
    public void initData() { }

    public abstract void showResult();
}
