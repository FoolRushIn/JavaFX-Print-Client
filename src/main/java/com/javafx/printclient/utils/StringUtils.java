package com.javafx.printclient.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class StringUtils {


    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static String[] readProperties() {
        Properties prop = new Properties();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(StringUtils.class.getClassLoader().getResourceAsStream("static/setting.properties"), StandardCharsets.UTF_8);
            prop.load(reader);
            String scanPath = prop.get("scanPath").toString();
            String alwaysOnTop = prop.get("alwaysOnTop").toString();
            return new String[]{scanPath, alwaysOnTop};
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeProperties(String key, String content) {
        Properties prop = new Properties();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(com.javafx.printclient.utils.StringUtils.class.getResourceAsStream("/src/com.javafx.printclient/setting.properties"), StandardCharsets.UTF_8);
            prop.load(reader);
            prop.setProperty(key, content);
            System.out.println("设置项更新完毕");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
