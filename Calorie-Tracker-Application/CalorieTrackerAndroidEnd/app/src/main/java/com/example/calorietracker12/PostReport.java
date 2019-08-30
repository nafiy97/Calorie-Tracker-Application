package com.example.calorietracker12;

import android.app.IntentService;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostReport extends IntentService {

    public PostReport() {
        super("PostReport");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String data = intent.getDataString();
        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
        Date now = Calendar.getInstance().getTime();
        String postTime="23-59-00";
        if (postTime.equals(sdf.format(now)))
        {

        }
        intent = new Intent(getApplicationContext(), PostReport.class);
        getApplicationContext().startService(intent);
    }
}
