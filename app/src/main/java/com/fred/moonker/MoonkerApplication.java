package com.fred.moonker;

import android.app.Application;

import com.fred.moonker.Model.User;

public class MoonkerApplication extends Application {

    public static final String URL = "http://192.168.0.109";
    public static final String USER_PREFIX = ":8001/user";
    public static final String ARTICLE_PREFIX = ":9001/article";


    public static User me;

    public static void setUser(User user){
        me = user;
    }

    public static User getUser(){
        return me;
    }

}
