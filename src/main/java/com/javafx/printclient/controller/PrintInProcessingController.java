package com.javafx.printclient.controller;

import com.javafx.printclient.common.LabelService;
import com.javafx.printclient.entity.InProcessingData;
import com.javafx.printclient.entity.PrinterMachine;
import com.javafx.printclient.service.Printer;
import com.javafx.printclient.utils.IDCell;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    LabelService labelService = LabelService.getInstance();
    List<InProcessingData> inProcessingDataList = new ArrayList<>();
    InProcessingData inProcessingData = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        //初始化event
//        initEvent();
    }


    public void initData(Object data) {
        initPagination((int)data);
    }

    //    @Override
    public void showResult() {
        inProcessingDataList.clear();
        for (Map.Entry<String, Printer> entry :LabelService.allPrinter.entrySet()) {
            inProcessingData = new InProcessingData();
            inProcessingData.setSerialkey(entry.getKey());
            inProcessingData.setStatus(Printer.statusMap.get("0"));
            inProcessingData.setNote("");
            inProcessingDataList.add(inProcessingData);
        }
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> tableView.getItems().clear());
        }).whenComplete((v, t) -> {
            pagination.setDisable(true);
            if (t != null) {
                t.printStackTrace();
            } else {
                Platform.runLater(() -> tableView.getItems().addAll(inProcessingDataList));
            }
        });
    }

    public void initView() {

        pagination.setVisible(false);

        //确定数据导入的列 属性值要和实体类的属性对的上
        tColumn_serial.setCellValueFactory(new PropertyValueFactory<>("serialkey"));
        tColumn_printer.setCellValueFactory(new PropertyValueFactory<>("printer"));
        tColumn_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        tColumn_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        tColumn_serial.setCellFactory(new IDCell<>());

        labelService.loadPrinter();

        for (Map.Entry<String, Printer> entry :LabelService.allPrinter.entrySet()) {
            inProcessingData = new InProcessingData();
            inProcessingData.setPrinter(entry.getKey());
            inProcessingData.setStatus(Printer.statusMap.get("0"));
            inProcessingData.setNote("");
            inProcessingDataList.add(inProcessingData);
        }

        ObservableList<InProcessingData> collect = FXCollections.observableArrayList(inProcessingDataList);


        tableView.setItems(collect);
    }


    //初始化分页控件，在每次有新数据到达TableView之后
    private void initPagination(int totalCount) {
        pagination.setCurrentPageIndex(0);
        //设置pagination的页数
        pagination.setPageCount((totalCount%30) > 0 ? (totalCount/30 + 1) : totalCount/30);
    }
}
