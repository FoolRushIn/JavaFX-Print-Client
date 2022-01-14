package com.javafx.printclient.controller;


import com.javafx.printclient.MainApp;
import com.javafx.printclient.utils.LoadUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class TopViewController implements Initializable {

    StackPane stackPane;

    @FXML
    VBox vBoxToDrag;

    @FXML
    HBox hBoxForDoubleClick;

    @FXML
    JFXButton btnMin;

    @FXML
    JFXButton btnClose;

    @FXML
    JFXButton btnSetting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    //加载顶部控制模块
    private void initView() {
        //实现窗口拖拽
        initDragAble(vBoxToDrag);
    }

    private void initEvent() {

        //设置 按钮点事件
        final Parent[] settingsPane = {null};
        btnSetting.setOnAction(ae -> {
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            if (settingsPane[0] == null) {
                settingsPane[0] = LoadUtil.loadFXML("fxml/items/setting_view.fxml");
            }
            dialogLayout.setBody(settingsPane[0]);
            dialogLayout.setPrefSize(600, 340);
            //获取到主界面的stackpane
            stackPane = (StackPane) vBoxToDrag.getParent().getParent().getParent();
            new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP).show();
        });

        //最小化 按钮点事件
        btnMin.setOnAction(ae -> {
            boolean iconified = MainApp.stage.isIconified();
            if (!iconified) {
                MainApp.stage.setIconified(true);
            }
        });

        //关闭 按钮点事件
        btnClose.setOnAction(ae -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "是否退出？", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get().equals(ButtonType.OK)) {
                Platform.exit();
                System.exit(0);
            } else {
                alert.hide();
                ae.consume();
            }
        });

        //双击 按钮点事件
        hBoxForDoubleClick.setOnMouseClicked(me -> {
            if (me.getClickCount() == 2) {
                //双击窗口最大化
                boolean maximized = MainApp.stage.isMaximized();
                if (maximized) {
                    MainApp.stage.setMaximized(false);
                } else {
                    MainApp.stage.setMaximized(true);
                }
            }
        });
    }


    /**
     * @param nodeToDrag
     * @return void
     * @Author heziyuan
     * @Description //拖动窗口
     **/
    private void initDragAble(Node nodeToDrag) {
        nodeToDrag.setOnMousePressed(me -> MainApp.setXYOffset(me.getSceneX(), me.getSceneY()));
        nodeToDrag.setOnMouseDragged(me -> MainApp.StageDraged(me.getScreenX(), me.getScreenY()));
    }
}