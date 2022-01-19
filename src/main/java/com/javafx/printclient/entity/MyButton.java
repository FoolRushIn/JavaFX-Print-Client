package com.javafx.printclient.entity;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class MyButton extends JFXButton {

    private String attribute;
    private String[] attributes;

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public void setAttributesss(String...attributes) {
        this.attributes = attributes;
    }

    public MyButton(String text) {
        super(text);
    }

    public MyButton() {
        super();
    }

    public void setHoverStyle() {
        getStylesheets().add("static/css/my_btn_css.css");
    }

    public MyButton(String text, Node graphic) {
        super(text, graphic);
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
