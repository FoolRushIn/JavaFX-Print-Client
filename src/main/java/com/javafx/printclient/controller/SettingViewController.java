package com.javafx.printclient.controller;


import com.javafx.printclient.MainApp;
import com.javafx.printclient.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Component
public class SettingViewController extends BaseController implements Initializable {

    public JFXTextField tfScanPath;
    public JFXButton btnSelectPath;
    //    public JFXButton btnScan;
    public JFXSpinner jfxSpinner;
    public Label labShowMsg;
    public JFXButton btnFullScreen;
    public JFXCheckBox checkBoxAlwaysOnTop;
    private String[] properties;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initEvent();
        initView();
        tfScanPath.setEditable(false);
    }

    private void initView() {
        if (properties == null || properties.length < 1) {
            properties = StringUtils.readProperties();
        }
        tfScanPath.setText(properties[0]);
        checkBoxAlwaysOnTop.setSelected(Boolean.parseBoolean(properties[1]));
        if (labShowMsg.getText() != null && !labShowMsg.getText().isEmpty()) labShowMsg.setText(null);
    }

    private void initEvent() {
        btnSelectPath.setOnAction(ae -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择音乐扫描路径");
            String path = chooser.showDialog(btnSelectPath.getScene().getWindow()).getAbsolutePath();
            StringUtils.writeProperties("scanPath", path);
            this.properties[0] = path;
            initView();
        });

        checkBoxAlwaysOnTop.setOnMouseClicked(me -> {
            boolean b = checkBoxAlwaysOnTop.isSelected();
            StringUtils.writeProperties("alwaysOnTop", String.valueOf(!b));
            MainApp.stage.setAlwaysOnTop(b);
        });


        btnFullScreen.setOnAction(ae -> {
            Stage stage = MainApp.stage;
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
            } else {
                stage.setFullScreen(true);
            }
        });
    }
}