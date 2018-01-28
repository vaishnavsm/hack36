//package com.hack36.UI;
//
//import android.support.v4.app.Fragment;
//import android.app.usage.UsageStats;
//import android.app.usage.UsageStatsManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.hack36.Helpers.UsageStatsHelper;
//import com.hack36.Models.UsageInstance;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import com.hack36.R;
//import needle.Needle;
//import needle.UiRelatedTask;
//
//public class UsageStatsFragment extends Fragment {
//    @BindView(R.id.generic_list_view) ListView listView;
//
//    // Managers
//    UsageStatsManager mUsageStatsManager;
//
//    public UsageStatsFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_list_details, container, false);
//        ButterKnife.bind(this,rootView);
//
//        initUI();
//        return rootView;
//    }
//
//    // Set adapters and shit
//    private void initUI(){
//
//        mUsageStatsManager = UsageStatsHelper.getInstance().getManager();
//
//        Needle.onBackgroundThread().execute(new UiRelatedTask<List<UsageStats>>() {
//            @Override
//            protected List<UsageStats> doWork() {
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.HOUR,-24); // Last 24 hours
//
//                List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,calendar.getTimeInMillis(), System.currentTimeMillis());
//
//                // Remove background or old apps
//                for(Iterator<UsageStats> iterator = usageStats.iterator(); iterator.hasNext();)
//                    if (iterator.next().getTotalTimeInForeground() < 10*1000) // less than 10 sec
//                        iterator.remove();
//
//                return usageStats;
//            }
//
//            @Override
//            protected void thenDoUiRelatedWork(List<UsageStats> usageStats) {
//                List<UsageInstance> result = new ArrayList<>();
//
//                // Morph them
//                for(UsageStats u:usageStats)
//                    result.add(new UsageInstance(u.getLastTimeUsed(),u.getTotalTimeInForeground(),u.getPackageName()));
//
//                // Sort them
//                Collections.sort(result,new UsageStatsHelper.UsageInstanceComparator());
//
//                // Update UI
//                updateAppsList(result);
//            }
//        });
//
//    }
//
//    /**
//     * Updates the {@link #listView} with the list of {@link com.hack36.Models.UsageInstance} passed as an argument.
//     */
//    void updateAppsList(List<UsageInstance> instances) {
//        List<String> appUsageDescription = new ArrayList<>();
//        DateFormat dateFormat = new SimpleDateFormat("dd MMM yy, hh:mm a", Locale.ENGLISH);
//
//        for (UsageInstance u : instances)
//            appUsageDescription.add(u.getPackageName().substring(u.getPackageName().lastIndexOf('.')+1) + "    " + dateFormat.format(new Date(u.getStartTime())));
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, appUsageDescription);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }
//}
