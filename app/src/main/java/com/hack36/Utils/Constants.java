package com.hack36.Utils;

import android.Manifest;

public class Constants {
    public static final String TAG = "###";
    public static final int PERMISSIONS_REQUEST = 101;
    public static final String LOGIN_ID = "login_id";
    public static final String PUSH_NOTIFICATION = "push_notification";
    public static final String PUSH_MSG_LOCATION = "push_msg_location";
    public static final String LAST_SYNC_TIME = "last_sync_time";

    public static final String BACKENDLESS_APP_ID = "45958D6A-36CC-C65F-FF4C-97701CC22A00";
    public static final String BACKENDLESS_APP_KEY = "CEE28517-6459-8E52-FFD3-95F86AF77C00";
    public static final String BACKENDLESS_GCM_PUSH_KEY = "940390285597";

    public static final String PERMISSIONS[] = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CALL_LOG
    };
}
