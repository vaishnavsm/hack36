package com.hack36.Utils;

import android.Manifest;

public class Constants {
    public static final String TAG = "###";
    public static final int PERMISSIONS_REQUEST = 101;
    public static final String LOGIN_ID = "login_id";
    public static final String LAST_SYNC_TIME = "last_sync_time";


    public static final String PERMISSIONS[] = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CALL_LOG
    };
}
