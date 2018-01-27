package com.hack36.Utils;

import android.util.Log;

import static com.hack36.Utils.Constants.TAG;

public class Utils {
    public static void myLog(Object... params){
        for(Object o:params)
            Log.d(TAG,o.toString());
    }


}
