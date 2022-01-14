package com.javafx.printclient.controller;


import com.javafx.printclient.service.Printer;
import com.javafx.printclient.utils.PrinterUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class InsertPrinterViewController implements Initializable {

    public JFXTextField printerRegisterName;
    public ChoiceBox choiceBox;
    public JFXButton confirm;
    public JFXButton cancel;

    public static Map<String, Printer> allPrinter = new HashMap();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    @FXML
    private void initView() {
        //将本地的打印机加载到下拉框中
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.PNG;
        PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);

        ObservableList<String> observableList = FXCollections.observableArrayList();
        for (int i = 0; i < printService.length; ++i) {
            observableList.add(i, printService[i].getName());
        }
        choiceBox.setItems(observableList);
        choiceBox.setValue(observableList.get(0));
    }


    @FXML
    private void initEvent() {
        //确认按钮点击事件
        confirm.setOnAction(ae -> {
            //获取输入的数据
            String inputText = printerRegisterName.getText();
            String choosePrinter = choiceBox.getValue().toString();

            //将数据写入到配置文件中
            PrinterUtil.savePrinter(inputText, choosePrinter);

            //弹出提示并关闭该弹窗
            Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
            alertInformation.setContentText("添加成功");
            alertInformation.show();

            PrinterManagementController.closePopWindow();

            //刷新管理打印机的界面数据
            BaseController.BC_CONTEXT.get(PrinterManagementController.class.getName()).showResult();
        });
        //取消按钮点击事件
        cancel.setOnAction(ae -> {
            printerRegisterName.setText("");
            PrinterManagementController.closePopWindow();
        });
    }
}