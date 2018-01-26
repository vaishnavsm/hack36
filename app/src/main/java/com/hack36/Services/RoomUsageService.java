package com.hack36.Services;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hack36.Models.MyDatabase;
import com.hack36.Interfaces.MyTimerObserver;
import com.hack36.Models.UsageInstance;
import needle.Needle;

import static com.hack36.Utils.Constants.TAG;

public class RoomUsageService extends Service implements MyTimerObserver {

    UsageStatsManager usageStatsManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyTimer.getInstance().registerObserver(this);

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

//        timerUpdated(); // calling the first time

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void timerUpdated() {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Usage Stats added at: " + System.currentTimeMillis() / 1000);

                Calendar calendar;
                calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, -24);

                List<UsageStats> usageStats = usageStatsManager.
                        queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), System.currentTimeMillis());
                List<UsageInstance> usageInstances = new ArrayList<>();

                for (UsageStats u : usageStats)
                    usageInstances.add(new UsageInstance(
                            u.getLastTimeStamp() / 1000, u.getLastTimeUsed() / 1000, u.getPackageName()));

                MyDatabase.getInstance(getApplicationContext())
                        .usageInstanceDao()
                        .insertAll(usageInstances);
            }
        });

    }
}
