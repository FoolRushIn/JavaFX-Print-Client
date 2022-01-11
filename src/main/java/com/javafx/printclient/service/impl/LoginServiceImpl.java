package com.javafx.printclient.service.impl;

import com.javafx.printclient.entity.ReturnData;
import com.javafx.printclient.entity.UserLogin;
import com.javafx.printclient.enumPackage.ReturnKeyEnum;
import com.javafx.printclient.mapper.UserLoginMapper;
import com.javafx.printclient.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author sue
 * @Date 2022/1/11 16:24
 * @Version 1.0
 **/

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserLoginMapper userLoginMapper;

    @Autowired
    UserLogin userLogin;

    @Autowired
    ReturnData returnData;

    @Override
    public ReturnData loginAccountCheck(String username, String password) {

        try {
            userLogin.setUsername(username);
            userLogin.setPassword(password);
//            userLoginMapper.loginCheck(userLogin);
            userLoginMapper.query(username);
            returnData.setKey(ReturnKeyEnum.成功.toString());
        } catch (Exception e) {
            e.printStackTrace();
            returnData.setKey(ReturnKeyEnum.异常.toString());
        }
        return returnData;
    }
}
