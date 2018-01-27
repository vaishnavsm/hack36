package com.hack36.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.hack36.Helpers.AppAuth;
import com.hack36.Helpers.Panic;
import com.hack36.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.buttonElizaFragment) LinearLayout elizaFragment;
    @BindView(R.id.rippleLayout) RippleBackground rippleBackground;
    @BindView(R.id.heartImageButton) ImageButton imageButton;
    @BindView(R.id.buttonPersonality)    LinearLayout callPersonality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this,rootView);

        elizaFragment.setOnClickListener(this);
        callPersonality.setOnClickListener(this);

        rippleBackground.startRippleAnimation();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Panic.getInstance().panicAsync(AppAuth.getInstance().getLoginToken(), new AsyncCallback<Object>() {
                    @Override
                    public void handleResponse(Object o) {
                        Toast.makeText(getContext(),"Panic Button works: "+o.toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(),"Panic button at fault",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return rootView;
    }

    public void onClick(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (view.getId()){
            case R.id.buttonElizaFragment:
//                SharedPrefHelper.getInstance().put(Constants.PUSH_MSG_LOCATION,"{'latitude':10.1,'longitude':10.1}");
//                startActivity(new Intent(getContext(), MapActivity.class));
                ChatFragment fragment2 = new ChatFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment2, fragment2.toString());
                fragmentTransaction.addToBackStack(fragment2.toString());
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
