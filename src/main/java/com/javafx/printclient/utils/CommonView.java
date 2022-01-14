package com.javafx.printclient.utils;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description：右键菜单核心类
 */
public class CommonView {

    private ContextMenu contextMenu = new ContextMenu();
    //可以添加的收藏夹list
    List<String> albumList = new ArrayList<>();

    //静态资源
    private static List<CheckMenuItem> selectCheckBsList = new ArrayList<>();
    private static CommonView instance = null;


    private CommonView() {
    }

    public static CommonView getInstance() {
        if (instance == null) {
            synchronized (CommonView.class) {
                if (instance == null) instance = new CommonView();
            }
        }
        return instance;
    }

    static {

        //依次给复选框菜单设置点击事件
        new CommonView().cbListChange();

    }

    private void cbListChange() {
        //给复选框设置选择事件
        selectCheckBsList.forEach(item -> item.setOnAction(me -> {
            CheckMenuItem checkMenuItem = (CheckMenuItem) me.getSource();
            String itemText = item.getText();   //获取复选框的文本
            System.out.println("点击了复选框" + itemText);    //测试是否被点击
        }));
    }
}
