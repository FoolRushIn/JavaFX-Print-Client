package com.javafx.printclient.controller;


import com.javafx.printclient.MainApp;
import com.javafx.printclient.utils.StringUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class InsertPrinterViewController extends BaseController implements Initializable {

    public JFXTextField printerRegisterName;
    public ChoiceBox choiceBox;
    public JFXButton confirm;
    public JFXButton cancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        choiceBox.setItems(FXCollections.observableArrayList(
                "English", "Open ",
                new Separator(), "Save", "Save as")
        );
    }


    private void initEvent() {
        choiceBox.setOnAction(ae -> {

        });
    }
}