package com.shs.contact.ui.app;

import android.app.Application;

/**
 * Created by wenhai on 2016/2/19.
 */
public class SHSApplication extends Application {
    private static SHSApplication application;
    private String username;
    private String token;
    private int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static boolean isDebug;

    @Override
    public void onCreate() {
        super.onCreate();
        application = (SHSApplication) getApplicationContext();

    }

    public static SHSApplication getInstance() {
        return application;
    }
}
