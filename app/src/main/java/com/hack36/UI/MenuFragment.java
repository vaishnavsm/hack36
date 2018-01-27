package com.hack36.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hack36.Models.Personality;
import com.hack36.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.buttonElizaFragment) Button elizaFragment;
    @BindView(R.id.buttonUsageFragment) Button usageFragment;
    @BindView(R.id.buttonCallFragment) Button callLogFragment;
    @BindView(R.id.buttonPersonality) Button callPersonality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this,rootView);

        elizaFragment.setOnClickListener(this);
        usageFragment.setOnClickListener(this);
        callLogFragment.setOnClickListener(this);
        callPersonality.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (view.getId()){
            case R.id.buttonUsageFragment:
                UsageStatsFragment fragment = new UsageStatsFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
                fragmentTransaction.addToBackStack(fragment.toString());
                break;

            case R.id.buttonElizaFragment:
                ChatFragment fragment2 = new ChatFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment2, fragment2.toString());
                fragmentTransaction.addToBackStack(fragment2.toString());
                break;

            case R.id.buttonCallFragment:
                CallLogFragment fragment3 = new CallLogFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment3, fragment3.toString());
                fragmentTransaction.addToBackStack(fragment3.toString());
                break;

            case R.id.buttonPersonality:
                PersonalityFragment fragment4 = new PersonalityFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment4, fragment4.toString());
                fragmentTransaction.addToBackStack(fragment4.toString());
                break;
        }

        fragmentTransaction.commit();
    }
}
