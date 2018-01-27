package com.hack36.Helpers;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.hack36.Utils.Constants;

public class Panic {
    private static final String SERVICE_NAME = "Panic";

    private static Panic ourInstance = new Panic();

    private Panic() {
    }

    public static Panic getInstance() {
        return ourInstance;
    }

    public static void initApplication() {
        Backendless.setUrl("https://api.backendless.com");

        // if you invoke this sample inside of android application, you should use overloaded "initApp" with "context" argument
//        Backendless.initApp(Constants.BACKENDLESS_APP_ID, Constants.BACKENDLESS_APP_KEY);
    }

    public void panic(java.lang.String userId) {
        Object[] args = new Object[]{userId};
        Backendless.CustomService.invoke( SERVICE_NAME, "panic", args );
    }

    public void panicAsync(java.lang.String userId, AsyncCallback<Object> callback){
        Object[] args = new Object[]{userId};
        Backendless.CustomService.invoke( SERVICE_NAME, "panic", args, Object.class, callback);
    }

}