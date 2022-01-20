package com.javafx.printclient.entity;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InProcessingData {
    private int serialkey;
    private String printer;
    private String status;
    private String note;
}
