package com.hack36.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import com.hack36.Models.Location;

@Dao
public interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Location location);

    @Query("SELECT * FROM location WHERE time > :time")
    List<Location> getAllAfter(int time);

    @Query("SELECT * FROM location WHERE time > :time")
    List<Location> getAllAfter(long time);
}
