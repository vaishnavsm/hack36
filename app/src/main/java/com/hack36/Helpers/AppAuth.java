package com.hack36.Helpers;

import android.content.Context;

import static com.hack36.Utils.Constants.LOGIN_ID;

public class AppAuth {
    private static AppAuth instance;
    private static SharedPrefHelper helper;

    private AppAuth(Context context){
        helper = SharedPrefHelper.getInstance(context);
    }

    public boolean isLoggedIn(){
        return helper.contains(LOGIN_ID);
    }

    public void logIn(String loginID){
        helper.put(LOGIN_ID,loginID);
    }

    public String getLoginToken(){
        return helper.getString(LOGIN_ID,null);
    }
    public static AppAuth getInstance(Context context){
        if (instance == null)
            instance = new AppAuth(context);
        return instance;
    }

    public static AppAuth getInstance(){
        if (instance == null)
            return null;
        return instance;
    }

}
