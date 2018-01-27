package com.hack36.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hack36.Models.MyDatabase;
import needle.Needle;

import static com.hack36.Utils.Constants.TAG;

/**
 * Handles Recursive API calling
 * No need of Timer Observer
 */
public class RoomLocationService extends Service {

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60*60*1000; // in millisecs. I'll keep 1 hr
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(final Location l) {
            mLastLocation.set(l);
            // Morph original android.Location into Models.Location

            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"Location added at "+l.getTime());
                    MyDatabase
                            .getInstance(getApplicationContext())
                            .locationDao()
                            .insert(new com.hack36.Models.Location(l.getTime()/1000,l.getLatitude(),l.getLongitude(),l.getAccuracy()));
                }
            });
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG,"Location Provider Enabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            ex.printStackTrace();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
