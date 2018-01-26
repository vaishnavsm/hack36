package com.hack36.Helpers;


import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.hack36.Models.UsageInstance;
import needle.Needle;
import needle.UiRelatedTask;

public class UsageStatsHelper {
    /**
     * The {@link Comparator} to sort a collection of {@link com.hack36.Models.UsageInstance} sorted by the
     * last time the app was used in the descendant order.
     */
    public static class UsageInstanceComparator implements Comparator<UsageInstance> {
        @Override
        public int compare(UsageInstance left, UsageInstance right) {
            return Long.compare(right.getStartTime(), left.getStartTime());
        }
    }

    public static boolean usagePermissionGranted(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // Will recreate the app too
    public static void storeYearOldData(final Application application, final UsageStatsManager manager){

        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);

                Context context = application.getApplicationContext();

                List<UsageStats> usageStats = manager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, calendar.getTimeInMillis(), System.currentTimeMillis());

                // Remove background or old apps
                for (Iterator<UsageStats> iterator = usageStats.iterator(); iterator.hasNext(); )
                    if (iterator.next().getTotalTimeInForeground() < 10 * 1000) // less than 10 sec
                        iterator.remove();

                List<UsageInstance> result = new ArrayList<>();

                // Morph them
                for(UsageStats u:usageStats)
                    result.add(new UsageInstance(u.getLastTimeUsed()/1000,u.getTotalTimeInForeground()/1000,u.getPackageName()));

                // Sort them
                Collections.sort(result,new UsageStatsHelper.UsageInstanceComparator());

                // Dont add old data to DB, just upload it
                AzureDBHelper.getInstance(context).syncOldAppUsage(result,application);

            }
        });
    }

    /*
     *  queryAndAggregateUsageStats provides only one entry per app,even for an year interval. Not what I want!
     *
     *  queryUsageStats does not provide hour precision.
     *      You may get when did user first and last opened whatsapp
     *      But never how many time he did it(at least by using this API)
     *
     *  Bottom line:
     *      U hv duration of all apps, everyday since last year -> social media time
     *      Using LTU for diff apps, u can get first and last app usage time -> sleeping time
     *      LTS, FTS are worthless
     */
}
