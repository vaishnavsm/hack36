package com.hack36.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    private static final String SETTINGS_NAME = "HealthAppPrefs";
    private static SharedPrefHelper sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public SharedPrefHelper(Context context){
        this.mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPref.edit();
    }

    public static SharedPrefHelper getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new SharedPrefHelper(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static SharedPrefHelper getInstance(){
        return sSharedPrefs;
    }

    public boolean contains(String key){
        return mPref.contains(key);
    }

    public void clear() {
        mEditor.clear();
    }

    public void put(String key, String val) {
        
        mEditor.putString(key, val);
        mEditor.commit();
    }

    public void put(String key, int val) {
        
        mEditor.putInt(key, val);
        mEditor.commit();
    }

    public void put(String key, boolean val) {
        
        mEditor.putBoolean(key, val);
        mEditor.commit();
    }

    public void put(String key, float val) {
        
        mEditor.putFloat(key, val);
        mEditor.commit();
    }

    /**
     * Convenience method for storing doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to store.
     * @param val The new value for the preference.
     */
    public void put(String key, double val) {
        
        mEditor.putString(key, String.valueOf(val));
        mEditor.commit();
    }

    public void put(String key, long val) {
        
        mEditor.putLong(key, val);
        mEditor.commit();
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return mPref.getLong(key, defaultValue);
    }

    public float getFloat(String key) {
        return mPref.getFloat(key, 0);
    }

    public float getFloat(String key, float defaultValue) {
        return mPref.getFloat(key, defaultValue);
    }

    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to fetch.
     */
    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    /**
     * Convenience method for retrieving doubles.
     * <p/>
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from String.
     *
     * @param key The name of the preference to fetch.
     */
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.valueOf(mPref.getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public void remove(String... keys) {
        for (String key : keys) {
            mEditor.remove(key);
        }
        mEditor.commit();
    }

}
