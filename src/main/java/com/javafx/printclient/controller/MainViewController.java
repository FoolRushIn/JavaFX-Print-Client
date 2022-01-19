package com.javafx.printclient.controller;


import com.javafx.printclient.service.MainService;
import com.javafx.printclient.utils.LoadUtil;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainViewController extends BaseController implements Initializable {
    @FXML
    public SplitPane splitPane;

    public ObservableList<Node> centerPaneList;

    @FXML
    public StackPane stackPane;

    @FXML
    BorderPane mainPane;

    @FXML
    JFXTabPane centerTabPane;

    @FXML
    VBox vBoxToDrag;

    @Autowired
    MainService mainService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
    }


    @Override
    public void showResult() {

    }

    //加载中间界面的多个模块
    public void initView() {
//
//        MainApp.hideWindow();
//        MainApp.stage = new MainStage("fxml/main.fxml");

        Tab tab0 = new Tab("打印报表进程", LoadUtil.loadFXML("fxml/items/print_porcess_view.fxml"));
        Tab tab1 = new Tab("打印机管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
//        Tab tab2 = new Tab("模板配置管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
//        Tab tab3 = new Tab("映射关系管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
        centerTabPane.getTabs().add(tab0);
        centerTabPane.getTabs().add(tab1);
//        centerTabPane.getTabs().add(tab2);
//        centerTabPane.getTabs().add(tab3);
//        centerPaneList = splitPane.getItems();


//        MainApp.hideWindow();
//        MainApp.stage = new MainStage("fxml/items/printer_management_view.fxml");
    }
}