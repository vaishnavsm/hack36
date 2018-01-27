package com.hack36.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.hack36.Helpers.AppAuth;

@Entity(indices = {@Index(value = {"time"},unique = true)})
public class Location { // By def, table name will be class name in lower case

    @com.google.gson.annotations.SerializedName("login_id")
    private String loginID;

    @Ignore
    private String id; // For Azure SDK, not for client

    @com.google.gson.annotations.SerializedName("c_time")
    @PrimaryKey
    private long time;

    @com.google.gson.annotations.SerializedName("latitude")
    private double latitude;

    @com.google.gson.annotations.SerializedName("longitude")
    private double longitude;

    @com.google.gson.annotations.SerializedName("accuracy")
    private float accuracy;

    public Location(long time, double latitude, double longitude, float accuracy){
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.loginID = AppAuth.getInstance().getLoginToken();
        this.id = loginID + time;
    }

    public String getId() {
        return id;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {this.loginID = loginID;}

    public long getTime() {
        return time;
    }

    public void setTime(long time) {this.time =  time;    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
