package com.javafx.printclient.mapper;

import com.javafx.printclient.entity.UserLogin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserLoginMapper {
    //查询用户账号密码是否正确
    public int loginCheck(UserLogin userLogin);
}

