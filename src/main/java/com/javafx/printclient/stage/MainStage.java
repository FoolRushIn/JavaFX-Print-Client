package com.javafx.printclient.stage;


import com.javafx.printclient.MainApp;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class MainStage extends Stage {
    public static Map<String, BaseStage> BS_CONTEXT = new WeakHashMap<>();

    public MainStage(String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        loader.setControllerFactory(MainApp.applicationContext::getBean);
        Parent root = null;
        Scene scene = null;
        try {
            root = loader.load();
            //注册界面
            if (fxml.contains("login.fxml")){
                scene = new Scene(root, 350.0, 250.0);
                setMinWidth(350);
                setMinHeight(250);
            }else {
                scene = new Scene(root, 1200.0, 700.0);
                setMinWidth(1200);
                setMinHeight(700);
            }
            setScene(scene);
            centerOnScreen();
            setTitle("及时报表打印");
            Image image = new Image("static/icon/音乐-1.png", 50, 50, true, true);
            getIcons().add(image);

            //定义Stage具有纯白色背景且没有装饰的样式。
            initStyle(StageStyle.UNDECORATED);

            show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "要退出了吗？", ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get().equals(ButtonType.OK)) {
                Platform.exit();
                System.exit(0);
            } else {
                alert.hide();
                event.consume();
            }
        });
    }
}
