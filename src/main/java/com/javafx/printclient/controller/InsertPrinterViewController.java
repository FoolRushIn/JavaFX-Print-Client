package com.javafx.printclient.controller;


import com.javafx.printclient.entity.ConfigEntity;
import com.javafx.printclient.service.Printer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Component
public class InsertPrinterViewController extends BaseController implements Initializable {

    public JFXTextField printerRegisterName;
    public ChoiceBox choiceBox;
    public JFXButton confirm;
    public JFXButton cancel;

    @Resource
    ConfigEntity configEntity;

    private static Map<String, Printer> allPrinter = new HashMap();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

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


    private void initEvent() {
        //确认按钮点击事件
        confirm.setOnAction(ae -> {
            //获取输入的数据
            String inputText = printerRegisterName.getText();
            String choosePrinter = choiceBox.getValue().toString();

            //将数据写入到配置文件中
            loadPrinter();

            //弹出提示并关闭该弹窗
            Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
            alertInformation.setContentText("登录成功,即将跳转到主页面!");
            alertInformation.show();

            PrinterManagementController.closePopWindow();
        });
        //取消按钮点击事件
        cancel.setOnAction(ae -> {
            PrinterManagementController.closePopWindow();
        });
    }

    public void loadPrinter() {
        Properties prop = new Properties();
        FileInputStream in = null;

        try {
            File f = new File(configEntity.getPrinterpath());
            if (f.exists()) {
                in = new FileInputStream(configEntity.getPrinterpath());
                prop.load(in);
                Iterator it = prop.stringPropertyNames().iterator();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = prop.getProperty(key);
                    this.addPrinter(key, value);
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }
        }
    }

    public void addPrinter(String printerName, String osprintername) {
        if (!allPrinter.containsKey(printerName.toUpperCase())) {
            Printer printer = new Printer(printerName);
            printer.setOsPrinterName(osprintername);
            allPrinter.put(printerName, printer);
//            printer.startThread();
        }
    }
}