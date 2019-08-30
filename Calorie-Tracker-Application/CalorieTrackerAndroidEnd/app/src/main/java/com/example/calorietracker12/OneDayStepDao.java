package com.example.calorietracker12;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OneDayStepDao {
    @Query("SELECT * FROM OneDayStep")
    List<OneDayStep> getAll();
    @Query("SELECT * FROM OneDayStep WHERE stepId = :stepId LIMIT 1")
    OneDayStep findByID(int stepId);
    @Query("SELECT * FROM OneDayStep WHERE step = :step")
    OneDayStep findByStep(int step);
    @Query("SELECT * FROM OneDayStep WHERE time = :time LIMIT 1")
    OneDayStep findByTime(String time);
    @Insert
    void insertAll(OneDayStep... oneDaySteps);
    @Insert
    long insert(OneDayStep oneDaySteps);
    @Delete
    void delete(OneDayStep oneDaySteps);
    @Update(onConflict = REPLACE)
    public void updateOneDayStep(OneDayStep... oneDaySteps);
    @Query("DELETE FROM OneDayStep")
    void deleteAll();

}
