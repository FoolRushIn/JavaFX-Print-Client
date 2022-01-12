package com.javafx.printclient.controller;

import com.javafx.printclient.entity.InProcessingData;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class PrintInProcessingController extends BaseController implements Initializable {

    @FXML public BorderPane printer_management_pane;
    @FXML public Pagination pagination;
    @FXML public TableView tableView;
    @FXML public TableColumn tColumn_serial;
    @FXML public TableColumn tColumn_printer;
    @FXML public TableColumn tColumn_status;
    @FXML public TableColumn tColumn_note;
    @FXML public Label labTitle;
    @FXML public JFXTextArea jfxTextAreaIntro;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        //初始化event
//        initEvent();
    }

    @Override
    public void initData(Object data) {
        initPagination((int)data);
    }

    private void initView() {
        //确定数据导入的列 属性值要和实体类的属性对的上
        tColumn_serial.setCellValueFactory(new PropertyValueFactory<>("serialkey"));
        tColumn_printer.setCellValueFactory(new PropertyValueFactory<>("printer"));
        tColumn_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        tColumn_note.setCellValueFactory(new PropertyValueFactory<>("note"));

        //测试数据
        ObservableList<InProcessingData> collect = FXCollections.observableArrayList(
            new InProcessingData(1, "one", "one", "one"),
            new InProcessingData(2, "two", "two", "two"),
            new InProcessingData(3, "three", "three", "three"),
            new InProcessingData(4, "four", "four", "four")
        );


        tableView.setItems(collect);
//        tableView.getColumns().addAll(tColumn_serial, tColumn_printer, tColumn_status, tColumn_note);
    }



//    private void initEvent() {
//        //分页控件事件
//        pagination.setPageFactory(param -> {
//            if (!CommonResources.isPaginationInit) {
//                pagination.setDisable(true);
//                return null;
//            } else {
//                pagination.setDisable(false);
//                if (!CommonResources.isPaginationDone) {
//                    return CommonResources.getNode();
//                }
//                requestMusic(param);
//                return CommonResources.getNode();
//            }
//        });
//    }

//    private MusicResources.CurrentSelectIndexCallback currentSelectIndexCallback = index -> {
//        tableView.getSelectionModel().select(index);
//        tableView.scrollTo(index <= 5 ? 0 : index - 5);
//    };


    //初始化分页控件，在每次有新数据到达TableView之后
    private void initPagination(int totalCount) {
        pagination.setCurrentPageIndex(0);
        //设置pagination的页数
        pagination.setPageCount((totalCount%30) > 0 ? (totalCount/30 + 1) : totalCount/30);
    }
}
