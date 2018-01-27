package com.hack36.Helpers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;

import com.hack36.Models.Location;
import com.hack36.Models.UsageInstance;

import static com.hack36.Utils.Constants.LAST_SYNC_TIME;
import static com.hack36.Utils.Utils.myLog;

public class AzureDBHelper {
    private MobileServiceClient mClient;
    private static AzureDBHelper instance = null;

    private AzureDBHelper(Context context){
        try {
            mClient = new MobileServiceClient(
                    "https://hassistant.azurewebsites.net",
                    context
            );
        }catch (MalformedURLException e){e.printStackTrace();}
    }

    public static AzureDBHelper getInstance(Context context) {
        if (instance == null)
            instance = new AzureDBHelper(context);

        return instance;
    }

    public void syncLocations(List<Location> locations){
        MobileServiceTable<Location> locationTable = mClient.getTable("locations",Location.class);

        TableOperationCallback<Location> mCallback = new TableOperationCallback<Location>() {
            public void onCompleted(Location entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null)
                    myLog("Insert Successful");
                else
                    myLog("Insert Failed in syncLocations");
            }
        };
        
        // Multiple inserts
        for(Location l:locations)
            locationTable.insert(l, mCallback);

    }

    public void syncAppUsage(List<UsageInstance> usageInstances){
        MobileServiceTable<UsageInstance> usageInstanceTable = mClient.getTable("usage",UsageInstance.class);

        TableOperationCallback<UsageInstance> mCallback = new TableOperationCallback<UsageInstance>() {
            public void onCompleted(UsageInstance entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null)
                    myLog("Insert Successful");
                else
                    myLog("Insert Failed in syncAppUsage");
            }
        };

        // Multiple inserts
        for(UsageInstance l:usageInstances)
            usageInstanceTable.insert(l, mCallback);
    }

    private final int MAX_SIZE = 80;
    // Add only MAX_SIZE insert queries in one go
    // Remove those many from usageInstance
    // And recursively call the same fn until it ends
    void syncOldAppUsage(final List<UsageInstance> usageInstances, final Application application){
        MobileServiceTable<UsageInstance> usageInstanceTable = mClient.getTable("usage",UsageInstance.class);


        TableOperationCallback<UsageInstance> mCallback = new TableOperationCallback<UsageInstance>() {
            public void onCompleted(UsageInstance entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null)
                    myLog("Insert Successful");
                else
                    myLog("Insert Failed in syncOldAppUsage");

                // FIXME When usageInstance.size is multiple of MAX_SIZE
                // Recursive call
                if (usageInstances.indexOf(entity)+1 == MAX_SIZE)
                    syncOldAppUsage(usageInstances.subList(MAX_SIZE,usageInstances.size()),application);

                // Restart the app if its last entry
                if (usageInstances.indexOf(entity) == usageInstances.size()-1){
                    Intent i = application.getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( application.getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    application.startActivity(i);

                    SharedPrefHelper.getInstance()
                            .put(LAST_SYNC_TIME, Calendar.getInstance().getTime().getTime()/1000);
                }
            }
        };

        // MAX_SIZE number of inserts
        for (int i = 0; i < usageInstances.size() && i<MAX_SIZE ;++i )
            usageInstanceTable.insert(usageInstances.get(i), mCallback);
    }
}
