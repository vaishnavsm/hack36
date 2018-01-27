package com.hack36.UI;

import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hack36.R;

import java.util.ArrayList;
import java.util.List;

public class CallLogFragment extends Fragment {
    @BindView(R.id.generic_list_view) ListView listView;

    public CallLogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_details, container, false);
        ButterKnife.bind(this,rootView);

        initUI();
        return rootView;
    }

    void initUI() {
        List<String> callLog = new ArrayList<>();

        try {
            String order = android.provider.CallLog.Calls.DATE + " DESC";
            Cursor cursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, order);
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dailerName = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);

            while (cursor.moveToNext()) {
                String phNum = cursor.getString(number), name = cursor.getString(dailerName);
                int callType = Integer.parseInt(cursor.getString(type));
                String dir = null;
                switch (callType) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }

                callLog.add(name+" " +phNum + " "+dir);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, callLog);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){e.printStackTrace();}
    }
}
