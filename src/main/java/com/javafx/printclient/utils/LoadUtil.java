package com.javafx.printclient.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;


public class LoadUtil {
    public static Parent loadFXML (String fxml) {
        try {
            return FXMLLoader.load(ClassLoader.getSystemResource(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
