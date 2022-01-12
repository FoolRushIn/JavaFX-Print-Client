package com.javafx.printclient;

import com.javafx.printclient.stage.MainStage;
import com.javafx.printclient.utils.ResizeHelper;
import com.javafx.printclient.utils.StringUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @ClassName MainApp
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 10:19
 * @Version 1.0
 **/

@SpringBootApplication
public class MainApp extends Application {

    //主窗口
    public static Stage stage = null;
    private static double xOffset = 0;
    private static double yOffset = 0;
    public static ConfigurableApplicationContext applicationContext;


    public static void main(String[] args) {
        applicationContext = SpringApplication.run(MainApp.class, args);
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = new MainStage("fxml/items/login_view.fxml");
        ResizeHelper.addResizeListener(stage);
        if (StringUtils.readProperties() != null) {
            stage.setAlwaysOnTop(Boolean.parseBoolean(StringUtils.readProperties()[1]));
        }
    }

    /**
     * 隐藏当前窗口
     */
    public static void hideWindow(){
        stage.hide();
    }

    public static void setXYOffset(double x, double y) {
        xOffset = x;
        yOffset = y;
    }

    public static void StageDraged(double screenX, double screenY) {
        stage.setX(screenX - xOffset);
        stage.setY(screenY - yOffset);
    }
}
