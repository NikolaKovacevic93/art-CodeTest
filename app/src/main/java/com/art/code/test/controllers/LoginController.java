package com.art.code.test.controllers;

/**
 * Created by Nikola on 11/17/2017.
 */

public class LoginController {

    private String loginToken;

    private static class SingletonHelper {
        private static final LoginController INSTANCE = new LoginController();
    }

    public static LoginController getInstance() {
        LoginController loginController = LoginController.SingletonHelper.INSTANCE;
        return loginController;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
