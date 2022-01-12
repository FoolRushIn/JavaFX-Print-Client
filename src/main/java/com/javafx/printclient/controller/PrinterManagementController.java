package com.javafx.printclient.controller;

import com.javafx.printclient.entity.PrinterMachine;
import com.javafx.printclient.utils.LoadUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PrinterManagementController extends BaseController implements Initializable {

    @FXML
    public BorderPane printer_management_pane;
    @FXML
    public Pagination pagination;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn tColumn_serial;
    @FXML
    public TableColumn tColumn_printer_registered;
    @FXML
    public TableColumn tColumn_printer_system;
    @FXML
    public TableColumn tColumn_printer_inuse;
    @FXML
    public JFXButton insertButton;
    @FXML
    public JFXButton cancelButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        //初始化event
        initEvent();
    }

    @Override
    public void initData(Object data) {
        initPagination((int) data);
    }

    private void initView() {
        //确定数据导入的列   属性值要和实体类的属性对的上
        tColumn_serial.setCellValueFactory(new PropertyValueFactory<>("serialkey"));
        tColumn_printer_registered.setCellValueFactory(new PropertyValueFactory<>("printerRegistered"));
        tColumn_printer_system.setCellValueFactory(new PropertyValueFactory<>("printerInSystem"));
        tColumn_printer_inuse.setCellValueFactory(new PropertyValueFactory<>("activeMode"));

        //测试数据
        ObservableList<PrinterMachine> collect = FXCollections.observableArrayList(
                new PrinterMachine(5, "one", "one", true),
                new PrinterMachine(6, "two", "two", true),
                new PrinterMachine(7, "three", "three", true),
                new PrinterMachine(8, "four", "four", true)
        );


        tableView.setItems(collect);
//        tableView.getColumns().addAll(tColumn_serial, tColumn_printer, tColumn_status, tColumn_note);
    }


    private void initEvent() {
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

        //新增  按钮点击事件
        final Parent[] insertPrinterPane = {null};
        insertButton.setOnAction(ae -> {
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            if (insertPrinterPane[0] == null) {
                insertPrinterPane[0] = LoadUtil.loadFXML("fxml/items/setting_view.fxml");
            }
            dialogLayout.setBody(insertPrinterPane[0]);
            dialogLayout.setPrefSize(600, 340);
            //获取到主界面的stackpane
            StackPane stackPane = (StackPane) printer_management_pane.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent();
            new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP).show();
        });
    }

//    private MusicResources.CurrentSelectIndexCallback currentSelectIndexCallback = index -> {
//        tableView.getSelectionModel().select(index);
//        tableView.scrollTo(index <= 5 ? 0 : index - 5);
//    };


    //初始化分页控件，在每次有新数据到达TableView之后
    private void initPagination(int totalCount) {
        pagination.setCurrentPageIndex(0);
        //设置pagination的页数
        pagination.setPageCount((totalCount % 30) > 0 ? (totalCount / 30 + 1) : totalCount / 30);
    }
}
