package com.hack36.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.backendless.Backendless;
import com.hack36.Helpers.AppAuth;
import com.hack36.Helpers.AzureDBHelper;
import com.hack36.Helpers.UsageStatsHelper;
import com.hack36.Models.MyDatabase;
import com.hack36.Helpers.NetworkHelper;
import com.hack36.Helpers.SharedPrefHelper;
import com.hack36.Services.AzureDBService;
import com.hack36.Services.RoomLocationService;
import com.hack36.Services.MyTimer;
import com.hack36.Services.RoomUsageService;
import com.hack36.UI.LoginFragment;
import com.hack36.R;
import com.hack36.UI.MenuFragment;
import com.hack36.Utils.Constants;

import java.util.Collections;

import static com.hack36.Utils.Constants.BACKENDLESS_APP_ID;
import static com.hack36.Utils.Constants.BACKENDLESS_APP_KEY;
import static com.hack36.Utils.Constants.PERMISSIONS;
import static com.hack36.Utils.Constants.PERMISSIONS_REQUEST;
import static com.hack36.Helpers.UsageStatsHelper.usagePermissionGranted;

// TODO Check how is coarse or fine is switched
// TODO Locations and latest usage still not syncing

/**
 * Here's the algo. Assuming beginning at t=0
 *
 * 1. Every day, Fetch last 24 hrs usage stats and store in local db
 * 2. After some time T2
 *      1.a Fetch GPS Coordinates
 *      1.b store in local db
 * 3. When net switches on
 *      2.a Send local DB contents to the cloud DB
 *      2.b Flush local db
 *      2.c Read FCM reminders, if any, for later display
 *
 *
 * Problems:
 *
 * 2. What should be the local database structure?
 *      Two tables
 *      a. Package Name, Start Time, End time
 *      b. GPS Coordinates, Time Stamps
 *      Can't get App Name, API doesn't provide that. Hv to configure it online
 *
 * 3. Auth method? TODO
 *      Currently guy's name
 *
 * 4. How frequently should I access GPS coordinates? OR Whats T2
 *      Currently every hour. Fine if net, else coarse
 *
 * 5. What DB tech will u use?
 *      Using Room
 *
 * 6. We can get only 6 month old app usage. Interval_MONTH produces most entries.
 *      Can't get even day precision on 4 month old data
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHelpers();

        // Min API is 22, so hv to ask runtime permissions
        // If either coarse of fine ain't granted, ask both
        if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);

        if (!usagePermissionGranted(this))
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        else {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (AppAuth.getInstance().isLoggedIn()) {
                initServices();

                // Check whether we had a notification or not
                if (SharedPrefHelper.getInstance().getBoolean(Constants.PUSH_NOTIFICATION,false)){
                    startActivity(new Intent(MainActivity.this, MapActivity.class));

                    // Also set the S.Helper to false for now
                    SharedPrefHelper.getInstance().put(Constants.PUSH_NOTIFICATION,false);
                }else {
                    ft.add(R.id.fragment_container, new MenuFragment());
                    ft.commit();
                }
            } else {
                ft.add(R.id.fragment_container, new LoginFragment());
                ft.commit();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0){
            boolean allGranted = true;

            for (int i:grantResults)
                if (i != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    Toast.makeText(this, "Not all Permissions granted", Toast.LENGTH_LONG).show();
                    break;
                }

            if (allGranted)
                recreate();
            else
                finish();
        }
    }

    private void initServices(){
        startService(new Intent(this, RoomLocationService.class));
        startService(new Intent(this, RoomUsageService.class));
        startService(new Intent(this, AzureDBService.class));
    }

    // Instantiates helpers and db
    private void initHelpers(){
        MyTimer.getInstance();
        MyDatabase.getInstance(this);
        SharedPrefHelper.getInstance(this);
        UsageStatsHelper.getInstance(this);
        AzureDBHelper.getInstance(this);
        AppAuth.getInstance(this);
        NetworkHelper.getInstance(this);

        // Necessary for login
        Backendless.initApp( this, BACKENDLESS_APP_ID, BACKENDLESS_APP_KEY);
    }

    @Override
    public void onResume(){
        super.onResume();

    }
}
