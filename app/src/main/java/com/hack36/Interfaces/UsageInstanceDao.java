package com.hack36.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import com.hack36.Models.UsageInstance;

@Dao
public interface UsageInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UsageInstance> instances);

    @Query("SELECT * FROM usage WHERE start_time > :time")
    List<UsageInstance> getAllAfter(long time);
}
