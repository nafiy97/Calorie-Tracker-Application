package com.example.calorietracker12;

import android.arch.persistence.room.*;
import android.content.Context;

@Database(entities = {OneDayStep.class}, version = 2, exportSchema = false)

public abstract class  OneDayStepDatabase extends RoomDatabase {
    public abstract OneDayStepDao oneDayStepDao();

    private static volatile OneDayStepDatabase INSTANCE;

    static OneDayStepDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OneDayStepDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), OneDayStepDatabase.class, "step_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
