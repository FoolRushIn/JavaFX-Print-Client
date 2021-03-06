package com.javafx.printclient.controller;

import com.javafx.printclient.common.LabelService;
import com.javafx.printclient.entity.MyButton;
import com.javafx.printclient.entity.PrinterMachine;
import com.javafx.printclient.service.Printer;
import com.javafx.printclient.utils.IDCell;
import com.javafx.printclient.utils.LoadUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public TableColumn tColumn_printer_operate;
    @FXML
    public MenuItem enabel;
    @FXML
    public MenuItem disable;
    @FXML
    public MenuItem delete;
    @FXML
    public JFXButton insertButton;
    @FXML
    public JFXButton cancelButton;

    public static JFXDialog jfxDialog;

    private static List<MenuItem> selectCheckBsList = Arrays.asList(
            new MenuItem("开启"),
            new MenuItem("停止"),
            new MenuItem("删除"));


    private static ContextMenu cm = new ContextMenu();

    private static PrinterMachine currSelected;

    LabelService labelService = LabelService.getInstance();
    List<PrinterMachine> printerMachineList = new ArrayList<>();
    PrinterMachine printerMachine = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        //初始化event
        initEvent();
//        new PrinterManagementController().cbListChange();
    }

    static {
        new PrinterManagementController().cbListChange();
    }

    @Override
    public void initView() {

        pagination.setVisible(false);

        labelService.loadPrinter();

        for (Map.Entry<String, Printer> entry : LabelService.allPrinter.entrySet()) {
            printerMachine = new PrinterMachine();
            printerMachine.setPrinterRegistered(entry.getKey());
            printerMachine.setPrinterInSystem(entry.getValue().getOsPrinterName());
            printerMachine.setActiveMode(entry.getValue().getPrinterActive() ? "就绪" : "停用");
            printerMachineList.add(printerMachine);
        }

        ObservableList<PrinterMachine> collect = FXCollections.observableArrayList(printerMachineList);

        //确定数据导入的列   属性值要和实体类的属性对的上
        tColumn_serial.setCellValueFactory(new PropertyValueFactory<>("serialkey"));
        tColumn_printer_registered.setCellValueFactory(new PropertyValueFactory<>("printerRegistered"));
        tColumn_printer_system.setCellValueFactory(new PropertyValueFactory<>("printerInSystem"));
        tColumn_printer_inuse.setCellValueFactory(new PropertyValueFactory<>("activeMode"));
        //序号列
        tColumn_serial.setCellFactory(new IDCell<>());
        //操作框按钮
        tColumn_printer_operate.setCellFactory(param -> new TableCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                MyButton myButton = new MyButton("≡");
                myButton.setHoverStyle();
                if (getIndex() < collect.size()) {
                    setGraphic(myButton);
                    operBtnEventBatchInit(getGraphic(), getIndex());
                }
            }
        });

        tableView.setItems(collect);

    }


    private void initEvent() {
        //新增  按钮点击事件
        final Parent[] insertPrinterPane = {null};
        insertButton.setOnAction(ae -> {
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            if (insertPrinterPane[0] == null) {
                insertPrinterPane[0] = LoadUtil.loadFXML("fxml/items/insert_printer_view.fxml");
            }
            dialogLayout.setBody(insertPrinterPane[0]);
            dialogLayout.setPrefSize(600, 340);
            //获取到主界面的stackpane
            StackPane stackPane = (StackPane) printer_management_pane.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent();
            jfxDialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            jfxDialog.show();
        });
    }

    //关闭弹窗
    public static void closePopWindow() {
        jfxDialog.close();
    }

    private void operBtnEventBatchInit(Node btn, int currIndex) {
        //点击添加按钮后，显示一个ContextMenu
        btn.setOnMouseClicked(me -> {
            tableView.getSelectionModel().select(currIndex);
            currSelected = (PrinterMachine) tableView.getSelectionModel().getSelectedItem();
            cm.getItems().addAll(selectCheckBsList);
            cm.show(btn.getScene().getWindow(), me.getScreenX() - 50, me.getScreenY() + 20);

            cm.setHideOnEscape(false);
        });
    }

    public List<PrinterMachine> loadPrinter() {
        Properties prop = new Properties();
        FileInputStream in = null;
        List<PrinterMachine> printerMachineList = new ArrayList<>();
        PrinterMachine printerMachine = null;

        try {
            File f = new File("printer.properties");
            if (f.exists()) {
                in = new FileInputStream("printer.properties");
                prop.load(in);
                Iterator it = prop.stringPropertyNames().iterator();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = prop.getProperty(key);
                    labelService.addPrinter(key, value);
                }
                for (Map.Entry<String, Printer> entry : LabelService.allPrinter.entrySet()) {
                    printerMachine = new PrinterMachine();
                    printerMachine.setPrinterRegistered(entry.getKey());
                    printerMachine.setPrinterInSystem(entry.getValue().getOsPrinterName());
                    printerMachine.setActiveMode(entry.getValue().getPrinterActive() ? "就绪" : "停用");
                    printerMachineList.add(printerMachine);
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
        return printerMachineList;
    }

    //    @Override
    public void showResult() {
        List<PrinterMachine> beanList = loadPrinter();
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> tableView.getItems().clear());
        }).whenComplete((v, t) -> {
            pagination.setDisable(true);
            if (t != null) {
                t.printStackTrace();
            } else {
                List<PrinterMachine> collect = beanList.stream().filter(bean -> !"".equals(bean.getPrinterRegistered().trim())).collect(Collectors.toList());
                Platform.runLater(() -> tableView.getItems().addAll(collect));
            }
        });
    }

    private void cbListChange() {
        //给复选框设置选择事件
        selectCheckBsList.forEach(item -> item.setOnAction(me -> {
            MenuItem menuItem = (MenuItem) me.getSource();
            String itemText = item.getText();   //获取复选框的文本
            switch (itemText) {
                case "开启":
                    labelService.setActive(currSelected.getPrinterRegistered(), true);
//                    for (Map.Entry<String, Printer> entry : LabelService.allPrinter.entrySet()) {
//                        if (entry.getKey().equalsIgnoreCase(currSelected.getPrinterRegistered()) && !entry.getValue().getPrinterActive()) {
//                            entry.getValue().setPrinterActive(true);
//                        }
//                    }
                    System.out.println("开启");
                    break;
                case "停止":
                    labelService.setActive(currSelected.getPrinterRegistered(), false);
                    System.out.println("停止");
                    break;
                case "删除":
                    labelService.removePrinterAction(currSelected.getPrinterRegistered());
                    break;
                default:
                    System.out.println("没获取到按钮");
                    break;
            }

            //刷新表格框架
            Platform.runLater(() -> BaseController.BC_CONTEXT.get(PrinterManagementController.class.getName()).initView());

            //刷新内容
            BaseController.BC_CONTEXT.get(PrinterManagementController.class.getName()).showResult();
            BaseController.BC_CONTEXT.get(PrintInProcessingController.class.getName()).showResult();

        }));
    }
}
