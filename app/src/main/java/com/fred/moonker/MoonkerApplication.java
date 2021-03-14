package com.fred.moonker;

import android.app.Application;

import com.fred.moonker.Model.User;

public class MoonkerApplication extends Application {

    public static String URL = "http://192.168.95.1";
    public static User me;

    public static void setUser(User user){
        me = user;
    }

    public static User getUser(){
        return me;
    }

}
