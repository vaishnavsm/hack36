package com.hack36.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    private static NetworkHelper instance;
    private static ConnectivityManager connectivityManager;

    private NetworkHelper(Context context){
        connectivityManager =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkHelper getInstance(Context context){
        if (instance == null)
            instance = new NetworkHelper(context);
        return instance;
    }

    public static NetworkHelper getInstance(){
        return instance;
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
