package com.hack36.Services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import com.backendless.push.BackendlessPushService;
import com.hack36.Helpers.SharedPrefHelper;
import com.hack36.R;
import com.hack36.Utils.Constants;

public class MyPushService extends BackendlessPushService {

    @Override
    public void onRegistered(Context context, String registrationId) {
        Toast.makeText(context,
                "device registered" + registrationId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnregistered(Context context, Boolean unregistered) {
        Toast.makeText(context,
                "device unregistered",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMessage(Context context, Intent intent) {
        final String message = intent.getStringExtra("message");

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                // Store in SP for later use
                SharedPrefHelper.getInstance().put(Constants.PUSH_NOTIFICATION,true);
                SharedPrefHelper.getInstance().put(Constants.PUSH_MSG_LOCATION,message);

                // Play a pop sound
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.catchy_pop);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
        });


        // When returning 'true', default Backendless onMessage implementation
        // will be executed. The default implementation displays the notification
        // in the Android Notification Center. Returning false, cancels the
        // execution of the default implementation.
        return true;
    }

    @Override
    public void onError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}