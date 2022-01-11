package com.javafx.printclient;

import com.javafx.printclient.stage.MainStage;
import com.javafx.printclient.utils.ResizeHelper;
import com.javafx.printclient.utils.StringUtils;
import com.javafx.printclient.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName MainApp
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 10:19
 * @Version 1.0
 **/

@SpringBootApplication
public class MainApp extends AbstractJavaFxApplicationSupport {

    //主窗口
    public static Stage stage = null;
    private static double xOffset = 0;
    private static double yOffset = 0;


    public static void main(String[] args) {
        launch(MainApp.class, LoginView.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = new MainStage("fxml/items/login.fxml");
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
