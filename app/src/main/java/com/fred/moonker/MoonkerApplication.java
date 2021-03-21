package com.fred.moonker;

import android.app.Application;

import com.fred.moonker.Model.User;

import java.util.ArrayList;
import java.util.List;

public class MoonkerApplication extends Application {

    public static List<Long> likeArticles = new ArrayList<>();
    public static List<Long> markArticles = new ArrayList<>();

    public static User me;
    public static boolean isLogin;

    public static final String URL = "http://192.168.0.114";
    public static final String USER_PREFIX = ":8001/user";
    public static final String User_Article_Relation_PREFIX = ":8001/uar";
    public static final String ARTICLE_PREFIX = ":9001/article";

    public static final String PROJECT_URI = "moonker";
    public static final String HTML_URI = "/article/";
    public static final String PORTRAIT_URI = "/portrait/";
    public static final String ARTICLE_PIC = "/pic";

    public static final String HTML_PATH = URL+":9001/"+PROJECT_URI+HTML_URI;
    public static final String UAR_PATH = URL+User_Article_Relation_PREFIX;
    public static final String PORTRAIT_PATH = URL+":8001/"+PROJECT_URI+PORTRAIT_URI;

    /**
     * 退出登录时调用
     */
    public static void exit(){
        me = new User();
        isLogin = false;
        likeArticles.clear();
        markArticles.clear();
    }

    public static void setUser(User user){
        me = user;
    }

    public static User getUser(){
        return me;
    }

    public static List<Long> getLikeArticles() {
        return likeArticles;
    }

    public static List<Long> getMarkArticles() {
        return markArticles;
    }

}
