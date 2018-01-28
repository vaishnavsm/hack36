package com.hack36.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.hack36.Helpers.AppAuth;
import com.hack36.Helpers.NetworkHelper;
import com.hack36.Helpers.UsageStatsHelper;
import static com.hack36.Helpers.UsageStatsHelper.storeYearOldData;
import static com.hack36.Utils.Utils.myLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hack36.R;
import com.hack36.Utils.Constants;

public class LoginFragment extends Fragment{

    @BindView(R.id.userNameTextBox)    EditText userName;
    @BindView(R.id.passwordTextBox)    EditText password;
    @BindView(R.id.submitButton)    Button submitButton;

    ProgressDialog dialog;

    public LoginFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,rootView);

        initUI();
        return rootView;
    }

    private void initUI(){
        // 1. Get his name
        // 2. Get year old data. Don't store, just send
        // 3. Recreate

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText() == null || userName.getText().toString().isEmpty()
                        || password.getText() == null || password.getText().toString().isEmpty())
                    Toast.makeText(getContext(), "No name specified", Toast.LENGTH_LONG).show();
                else {
                    if (NetworkHelper.getInstance().isNetworkAvailable()) {
                         dialog = new ProgressDialog(getActivity());
                         dialog.setMessage("Logging In...");
                         dialog.setCancelable(false);
                         dialog.show();

                        // login the user
                        Backendless.UserService.login(userName.getText().toString(),
                                password.getText().toString(),
                                new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {

                                        // Local App Login
                                        AppAuth.getInstance().logIn(backendlessUser.getObjectId());
                                        myLog("Login success: "+backendlessUser.getObjectId());

                                        // Cloud Push Register
                                        // Call back in MyPushReceiver
                                        Backendless.Messaging.registerDevice(Constants.BACKENDLESS_GCM_PUSH_KEY);

                                        // This fn will recreate the app too
//                                        storeYearOldData(getActivity().getApplication(), UsageStatsHelper.getInstance().getManager());

                                        dialog.dismiss();
                                        getActivity().recreate();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        Toast.makeText(getContext(), "Login Fault", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                },true);
                    }
                    else
                        Toast.makeText(getContext(), "Internet not available", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        if (dialog != null) // To avoid leaked decor view exception
            dialog.dismiss();
    }

}
