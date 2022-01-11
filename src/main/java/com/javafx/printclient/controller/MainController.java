package com.javafx.printclient.controller;

import com.javafx.printclient.MainApp;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName MainController
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 10:29
 * @Version 1.0
 **/
@Component
public class MainController extends BaseController implements Initializable {

//    @FXML
//    StackPane stackPane;
//
//    @FXML
//    VBox vBoxToDrag;
//
//    HBox hBoxForDoubleClick;
//    JFXButton btnMin;
//    JFXButton btnClose;
//    JFXButton btnSetting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    //加载顶部控制模块
    private void initView() {
//        //实现窗口拖拽
//        initDragAble(vBoxToDrag);
//        //顶部组件加载
//        hBoxForDoubleClick = (HBox) vBoxToDrag.lookup("#hBoxForDoubleClick");
//        btnMin = (JFXButton) vBoxToDrag.lookup("#btnMin");
//        btnClose = (JFXButton) vBoxToDrag.lookup("#btnClose");
//        btnSetting = (JFXButton) vBoxToDrag.lookup("#btnSetting");
    }

    private void initEvent() {
//
//        //设置 按钮点事件
//        final Parent[] settingsPane = {null};
//        btnSetting.setOnAction(ae -> {
//            JFXDialogLayout dialogLayout = new JFXDialogLayout();
//            if (settingsPane[0] == null) {
//                settingsPane[0] = LoadUtil.loadFXML("fxml/items/setting_view.fxml");
//            }
//            dialogLayout.setBody(settingsPane[0]);
//            dialogLayout.setPrefSize(600, 340);
//            new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP).show();
//        });
//
//        //最小化 按钮点事件
//        btnMin.setOnAction(ae -> {
//            boolean iconified = MainApp.stage.isIconified();
//            if (!iconified) {
//                MainApp.stage.setIconified(true);
//            }
//        });
//
//        //关闭 按钮点事件
//        btnClose.setOnAction(ae -> {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "要退出了吗？", ButtonType.OK, ButtonType.CANCEL);
//            Optional<ButtonType> buttonType = alert.showAndWait();
//            if (buttonType.get().equals(ButtonType.OK)) {
//                Platform.exit();
//                System.exit(0);
//            } else {
//                alert.hide();
//                ae.consume();
//            }
//        });
//
//        //双击 按钮点事件
//        hBoxForDoubleClick.setOnMouseClicked(me -> {
//            if (me.getClickCount() == 2) {
//                //双击窗口最大化
//                boolean maximized = MainApp.stage.isMaximized();
//                if (maximized) {
//                    MainApp.stage.setMaximized(false);
//                } else {
//                    MainApp.stage.setMaximized(true);
//                }
//            }
//        });
    }


    /**
     * @param nodeToDrag
     * @return void
     * @Author heziyuan
     * @Description //拖动窗口
     **/
    private void initDragAble(Node nodeToDrag) {
        nodeToDrag.setOnMousePressed(me -> MainApp.setXYOffset(me.getSceneX(), me.getSceneY()));
        nodeToDrag.setOnMouseDragged(me -> MainApp.StageDraged(me.getScreenX(), me.getScreenY()));
    }
}
