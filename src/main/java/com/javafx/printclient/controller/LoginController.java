package com.javafx.printclient.controller;

import com.javafx.printclient.MainApp;
import com.javafx.printclient.entity.ReturnData;
import com.javafx.printclient.service.LoginService;
import com.javafx.printclient.stage.MainStage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName LoginController
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 10:24
 * @Version 1.0
 **/
@Component
public class LoginController implements Initializable {

    @FXML
    AnchorPane vPaneToDrag;

    @FXML
    JFXTextField username;

    @FXML
    JFXPasswordField password;

    @FXML
    JFXButton loginButton;

    @FXML
    JFXButton btnMin;

    @FXML
    JFXButton btnClose;

    @Autowired
    private LoginService loginService;

    private MainApp mainApp;

    public void setApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * 初始化方法
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }



    /**
     * 登录操作
     */
    @FXML
    public void login() {
        //登录验证
        Alert alertError = new Alert(Alert.AlertType.ERROR);
//        Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
        if (StringUtils.isEmpty(username.getText())) {
            alertError.setContentText("用户名不能为空！");
            alertError.show();
            return;
        }
        if (StringUtils.isEmpty(password.getText())) {
            alertError.setContentText("密码不能为空！");
            alertError.show();
            return;
        }

        ReturnData returnData = loginService.loginAccountCheck(username.getText(), password.getText());
        if (!returnData.getKey().equalsIgnoreCase("ok")) {
            alertError.setContentText("用户名或密码错误,请重试!");
            alertError.show();
            return;
        }

        //成功 跳转到主界面
//        alertInformation.setContentText("登录成功,即将跳转到主页面!");
//        alertInformation.show();

        //这个方法无法实现拖拽窗口
//        openMainWindow();

        MainApp.hideWindow();
        MainApp.stage = new MainStage("fxml/main.fxml");
    }

    private void initView() {
        //实现窗口拖拽
        initDragAble(vPaneToDrag);
    }

    private void initEvent() {
        //最小化 按钮点事件
        btnMin.setOnAction(ae -> {
            boolean iconified = MainApp.stage.isIconified();
            if (!iconified) {
                MainApp.stage.setIconified(true);
            }
        });

        //关闭 按钮点事件
        btnClose.setOnAction(ae -> {
            Platform.exit();
            System.exit(0);
        });

    }

    private void openMainWindow() {
        Stage mainStageWindow = new MainStage("fxml/main.fxml");
        Window window = username.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).close();
        }
        mainStageWindow.show();
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
