package com.javafx.printclient.controller;

import com.javafx.printclient.MainApp;
import com.javafx.printclient.entity.ReturnData;
import com.javafx.printclient.service.LoginService;
import com.javafx.printclient.stage.MainStage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName LoginController
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 10:24
 * @Version 1.0
 **/
@FXMLController
public class LoginController implements Initializable {

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

    @Resource
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
        initEvent();
    }

    /**
     * 登录操作
     */
    @FXML
    public void login() {
        //登录验证
        Alert alertError = new Alert(Alert.AlertType.ERROR);
        Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
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
        }

        //成功 跳转到主界面
        alertInformation.setContentText("登录成功,即将跳转到主页面!");
        MainApp.hideWindow();
        MainApp.stage = new MainStage("fxml/main.fxml");
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
}
