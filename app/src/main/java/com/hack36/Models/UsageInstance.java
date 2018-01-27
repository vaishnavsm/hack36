package com.hack36.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.hack36.Helpers.AppAuth;

@Entity(tableName = "usage",indices = {@Index(value = {"start_time"},unique = true)})
public class UsageInstance {

    @Ignore
    private String id; // For Azure SDK, not for client

    @com.google.gson.annotations.SerializedName("login_id")
    private String loginID;

    @com.google.gson.annotations.SerializedName("start_time")
    @PrimaryKey
    @ColumnInfo(name = "start_time")
    private long startTime;

    @com.google.gson.annotations.SerializedName("duration")
    private long duration;

    @com.google.gson.annotations.SerializedName("package_name")
    private String packageName;

    public UsageInstance(long startTime, long duration, String packageName) {
        this.startTime = startTime;
        this.duration = duration;
        this.packageName = packageName;
        this.loginID = AppAuth.getInstance().getLoginToken();
        this.id = packageName + startTime;
    }

    public String getId() {
        return id;
    }
    public String getLoginID() {return loginID;}

    public void setLoginID(String loginID) {this.loginID = loginID;}

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
