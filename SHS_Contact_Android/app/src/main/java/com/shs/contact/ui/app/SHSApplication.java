package com.shs.contact.ui.app;

import android.app.Application;

/**
 * Created by wenhai on 2016/2/19.
 */
public class SHSApplication extends Application{
    public static SHSApplication application;
    public static boolean isDebug;

    public static SHSApplication hxSDKHelper = new SHSApplication();

    public static SHSApplication getInstance() {
        return application;
    }
}
