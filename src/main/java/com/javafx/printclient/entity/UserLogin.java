package com.javafx.printclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserLogin
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 15:57
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserLogin {
    private String username;
    private String password;
}
