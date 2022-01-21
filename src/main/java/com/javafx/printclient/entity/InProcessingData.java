package com.javafx.printclient.entity;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @ClassName InProcessingData
 * @Description
 * @Author sue
 * @Date 2022/1/12 15:32
 * @Version 1.0
 **/


public class InProcessingData {
    private final SimpleStringProperty serialkey = new SimpleStringProperty();
    private final SimpleStringProperty  printer = new SimpleStringProperty();
    private final SimpleStringProperty  status = new SimpleStringProperty();
    private final SimpleStringProperty note = new SimpleStringProperty();

    public String getSerialkey() {
        return serialkey.get();
    }

    public SimpleStringProperty serialkeyProperty() {
        return serialkey;
    }

    public void setSerialkey(String serialkey) {
        this.serialkey.set(serialkey);
    }

    public String getPrinter() {
        return printer.get();
    }

    public SimpleStringProperty printerProperty() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer.set(printer);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }
}
