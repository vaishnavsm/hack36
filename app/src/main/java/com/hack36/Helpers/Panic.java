package com.hack36.Helpers;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;

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