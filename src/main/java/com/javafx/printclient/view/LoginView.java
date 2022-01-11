package com.javafx.printclient.view;

import com.javafx.printclient.stage.MainStage;
import com.javafx.printclient.utils.ResizeHelper;
import com.javafx.printclient.utils.StringUtils;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

/**
 * @ClassName LoginView
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 17:21
 * @Version 1.0
 **/
@FXMLView("/fxml/item/login.fxml")
public class LoginView extends AbstractFxmlView {

    @PostConstruct
    protected void initUI() throws Exception {
        Stage stage = new MainStage("fxml/items/login.fxml");
        ResizeHelper.addResizeListener(stage);
        if (StringUtils.readProperties() != null) {
            stage.setAlwaysOnTop(Boolean.parseBoolean(StringUtils.readProperties()[1]));
        }
    }

}
