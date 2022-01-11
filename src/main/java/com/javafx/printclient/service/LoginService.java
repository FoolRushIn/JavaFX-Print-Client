package com.javafx.printclient.service;

import com.javafx.printclient.entity.ReturnData;

public interface LoginService {
    public ReturnData loginAccountCheck(String username, String password);
}
