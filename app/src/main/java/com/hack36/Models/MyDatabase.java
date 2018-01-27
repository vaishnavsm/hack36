package com.hack36.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hack36.Interfaces.LocationDao;
import com.hack36.Interfaces.UsageInstanceDao;

@Database(entities = {UsageInstance.class, Location.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract UsageInstanceDao usageInstanceDao();
    public abstract LocationDao locationDao();

    private static final String DB_NAME = "health.db";
    private static volatile MyDatabase Instance;

    public static MyDatabase getInstance(Context context){
        if (Instance == null){
            Instance = Room.databaseBuilder(context.getApplicationContext(),
                    MyDatabase.class, DB_NAME).build();
        }
        return Instance;
    }

}
