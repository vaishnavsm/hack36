package com.hack36.UI;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hack36.Helpers.AppAuth;
import com.hack36.Helpers.AzureDBHelper;
import com.hack36.Models.MyDatabase;
import com.hack36.Helpers.NetworkHelper;
import com.hack36.Helpers.SharedPrefHelper;
import com.hack36.Helpers.UsageStatsHelper;
import com.hack36.Models.UsageInstance;
import com.hack36.R;
import com.hack36.Services.AzureDBService;
import com.hack36.Services.RoomLocationService;
import com.hack36.Services.MyTimer;
import com.hack36.Services.RoomUsageService;
import needle.Needle;
import needle.UiRelatedTask;

import static com.hack36.Utils.Constants.PERMISSIONS;
import static com.hack36.Utils.Constants.PERMISSIONS_REQUEST;
import static com.hack36.Helpers.UsageStatsHelper.storeYearOldData;
import static com.hack36.Helpers.UsageStatsHelper.usagePermissionGranted;

// TODO Check how is coarse or fine is switched

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

    // Managers
    UsageStatsManager mUsageStatsManager;

    // Views
    @BindView(R.id.list_view_app_usage) ListView listView;
    @BindView(R.id.userNameTextBox) EditText userName;
    @BindView(R.id.submitButton) Button submitButton;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this); // For all Views

        initHelpers();

        // Min API is 22, so hv to ask runtime permissions
        // If either coarse of fine ain't granted, ask both
        if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);

        if (!usagePermissionGranted(this))
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        else {
            if (AppAuth.getInstance().isLoggedIn()) {
                // 1. Check App permissions
                // 2. Get last 1 day data. Display

                listView.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);


                initServices();
                initUI();
            } else {
                // 1. Get his name
                // 2. Get year old data. Don't store, just send
                // 3. Recreate

                listView.setVisibility(View.GONE);
                userName.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userName.getText() == null || userName.getText().toString().isEmpty())
                            Toast.makeText(getApplicationContext(), "No name specified", Toast.LENGTH_LONG).show();
                        else {
                            if (NetworkHelper.getInstance().isNetworkAvailable()) {

                                // login the user
                                AppAuth.getInstance().logIn(userName.getText().toString());

                                // This fn will recreate the app too
                                storeYearOldData(getApplication(), (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE));

                                dialog = new ProgressDialog(MainActivity.this);
                                dialog.setMessage("Configuring");
                                dialog.show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
        AzureDBHelper.getInstance(this);
        AppAuth.getInstance(this);
        NetworkHelper.getInstance(this);
    }

    // Set adapters and shit
    private void initUI(){

        mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);

        Needle.onBackgroundThread().execute(new UiRelatedTask<List<UsageStats>>() {
            @Override
            protected List<UsageStats> doWork() {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR,-24); // Last 24 hours

                List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,calendar.getTimeInMillis(), System.currentTimeMillis());

                // Remove background or old apps
                for(Iterator<UsageStats> iterator = usageStats.iterator(); iterator.hasNext();)
                    if (iterator.next().getTotalTimeInForeground() < 10*1000) // less than 10 sec
                        iterator.remove();

                return usageStats;
            }

            @Override
            protected void thenDoUiRelatedWork(List<UsageStats> usageStats) {
                List<UsageInstance> result = new ArrayList<>();

                // Morph them
                for(UsageStats u:usageStats)
                    result.add(new UsageInstance(u.getLastTimeUsed(),u.getTotalTimeInForeground(),u.getPackageName()));

                // Sort them
                Collections.sort(result,new UsageStatsHelper.UsageInstanceComparator());

                // Update UI
                updateAppsList(result);
            }
        });

    }

    /**
     * Updates the {@link #listView} with the list of {@link com.hack36.Models.UsageInstance} passed as an argument.
     */
    void updateAppsList(List<UsageInstance> instances) {
        List<String> appUsageDescription = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yy, hh:mm a", Locale.ENGLISH);

        for (UsageInstance u : instances)
            appUsageDescription.add(u.getPackageName().substring(u.getPackageName().lastIndexOf('.')+1) + "    " + dateFormat.format(new Date(u.getStartTime())));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, appUsageDescription);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (dialog != null) // To avoid leaked decor view exception
            dialog.dismiss();
    }
}
