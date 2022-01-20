package com.javafx.printclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PrinterMachine
 * @Description
 * @Author sue
 * @Date 2022/1/12 11:05
 * @Version 1.0
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrinterMachine {
    private int serialkey;
    private String printerRegistered;
    private String printerInSystem;
    private String activeMode;
}
