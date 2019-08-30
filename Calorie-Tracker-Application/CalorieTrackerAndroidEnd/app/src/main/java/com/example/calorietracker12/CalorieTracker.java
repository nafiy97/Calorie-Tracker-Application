package com.example.calorietracker12;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CalorieTracker extends Fragment {
    View vDisplayUnit;
    TextView title;
    Button track_btn;
    OneDayStepDatabase db;
    int total_step;
    String concal;
    String burncal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.tracker, container, false);
        title = (TextView) vDisplayUnit.findViewById(R.id.track_title);
        db = Room.databaseBuilder(getActivity(), OneDayStepDatabase.class, "step_database").fallbackToDestructiveMigration().build();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String now = sdf.format(calendar.getTime());
        GetTotalConsumedCalorie getTotalConsumedCalorie = new GetTotalConsumedCalorie();
        getTotalConsumedCalorie.execute(new String[] {Mainfragment.USERID,now});

//       再得到数据库里步数的值
        GetTotalStep getTotalStep  = new GetTotalStep();
        getTotalStep.execute();

//       得到total burned的值
        GetTotalBurnedCalorie getTotalBurnedCalorie = new GetTotalBurnedCalorie();
        getTotalBurnedCalorie.execute(Mainfragment.USERID);

//       最后再等待0.5秒确保都由值了显示再调用setText
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                title.setText(" Goal is "+Mainfragment.SETTINGGOAL+" \n\n Total step is "+String.valueOf(total_step)+
                        "\n\n Total consumed calorie is "+concal+"\n\n Total burned calorie is "+burncal);
            }
        },500);



        return vDisplayUnit;
    }
    private class GetTotalStep extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            List<OneDayStep> oneDaySteps = db.oneDayStepDao().getAll();
            if (!(oneDaySteps.isEmpty() || oneDaySteps == null)) {
                int allOneDayStepNum = 0;
                for (OneDayStep temp : oneDaySteps) {
                    allOneDayStepNum = allOneDayStepNum + temp.getStep();
                }
                total_step = allOneDayStepNum;
            }
            else
            {
                total_step = 0;
            }
            return null;
        }
    }

    private class GetTotalConsumedCalorie extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            DecimalFormat df = new DecimalFormat( "0.0");
            String str= "0";
            if(RestClient.findTotalCalorieConsumed(params[0], params[1])!= null){
                str = RestClient.findTotalCalorieConsumed(params[0], params[1]);
            }
            concal = df.format(Double.valueOf(str));
            return null;
        }
    }

    private class GetTotalBurnedCalorie extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            DecimalFormat df = new DecimalFormat( "0.0");
            double restCal = 0;
            double stepCal = 0;
            String str = "0";
            if(RestClient.findTotalRestCalorieBurned(params[0])!=null)
            {
                str = RestClient.findTotalRestCalorieBurned(params[0]);
                restCal = Double.valueOf(str);
                stepCal = Double.valueOf(str) * total_step;
            }
            burncal = df.format(restCal + stepCal);
            return  null;
        }
    }
}