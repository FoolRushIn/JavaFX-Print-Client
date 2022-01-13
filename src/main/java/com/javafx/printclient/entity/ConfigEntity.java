package com.javafx.printclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConfigEntity
 * @Description TODO
 * @Author sue
 * @Date 2022/1/13 13:56
 * @Version 1.0
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@PropertySource(value = "classpath:static/setting.properties")
public class ConfigEntity {

    @Value("${printerpath}")
    private String printerpath;

}
