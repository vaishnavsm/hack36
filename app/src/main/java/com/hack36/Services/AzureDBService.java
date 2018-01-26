package com.hack36.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

import com.hack36.Helpers.AzureDBHelper;
import com.hack36.Models.MyDatabase;
import com.hack36.Helpers.NetworkHelper;
import com.hack36.Helpers.SharedPrefHelper;
import com.hack36.Interfaces.MyTimerObserver;
import com.hack36.Models.Location;
import com.hack36.Models.UsageInstance;
import needle.Needle;

import static com.hack36.Utils.Constants.LAST_SYNC_TIME;
import static com.hack36.Utils.Utils.myLog;

public class AzureDBService extends Service implements MyTimerObserver {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyTimer.getInstance().registerObserver(this);

//        timerUpdated(); // calling the first time

        return Service.START_NOT_STICKY;
    }

    @Override
    public void timerUpdated() {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {

                /*
                 * If there's no internet, there won't be any update
                 * So,last sync time remains intact and we'll try it the next time
                 * And the next time we'll hv more app and location stats
                 * than usual
                 */
                if (NetworkHelper.getInstance().isNetworkAvailable()) {
                    myLog("Syncing to cloud DB at: " + System.currentTimeMillis() / 1000);

                    // S.Helper will contain LAST_SYNC_TIME
                    // after configure old app stats call
                    // Its in seconds
                    long time = SharedPrefHelper.getInstance().getInt(LAST_SYNC_TIME,-1);

                    List<UsageInstance> usageInstances =
                            MyDatabase.getInstance(getApplicationContext())
                                    .usageInstanceDao()
                                    .getAllAfter(time);

                    List<Location> locations =
                            MyDatabase.getInstance(getApplicationContext())
                                    .locationDao()
                                    .getAllAfter(time);

                    AzureDBHelper.getInstance(getApplicationContext()).syncLocations(locations);
                    AzureDBHelper.getInstance(getApplicationContext()).syncAppUsage(usageInstances);

                    myLog(locations.size() + " locations to sync");
                    myLog(usageInstances.size() + " usage instances to sync");

                    // Update last sync time too
                    SharedPrefHelper.getInstance()
                            .put(LAST_SYNC_TIME, Calendar.getInstance().getTime().getTime()/1000);
                }
            }
        });

    }
}
