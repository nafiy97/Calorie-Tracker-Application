package com.example.calorietracker12;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Mainfragment extends Fragment {
    TextView welcome;
    TextView currentTime;
    EditText goalText;
    Button saveBtn;
    View vMain;
    Timer timer = new Timer();
    AlarmManager alarmMgr;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    static String USERID= "0";
    String name;

    //这里也创建一个静态变量用来存储设定的goal
    public static String SETTINGGOAL ="0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vMain = inflater.inflate(R.layout.mainfragment, container, false);
        welcome = (TextView) vMain.findViewById(R.id.welcome);
        currentTime = (TextView) vMain.findViewById(R.id.currentTime);
        goalText = (EditText) vMain.findViewById(R.id.goal_txt);
        saveBtn = (Button) vMain.findViewById(R.id.saveGoal);


//        得到当前的userId
        GetUserIdAsyncTask getUserIdAsyncTask = new GetUserIdAsyncTask();
        getUserIdAsyncTask.execute(Login.ACTPASS);

//        得到当前用户的first name
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GetNameAsyncTask getNameAsyncTask = new GetNameAsyncTask();
                getNameAsyncTask.execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        welcome.setText("Welcome "+name);
                    }},500);
            }},1000);


        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        String now = sdf.format(Calendar.getInstance().getTime());
        currentTime.setText("Current time is "+now);

        //      存储设定的goal
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goal = goalText.getText().toString();
                if (!(goal.isEmpty()) && (goal.matches("[0-9]+"))) {
                    SETTINGGOAL = goal;
                    Toast toastCenter = Toast.makeText(getActivity(),"Set goal successfully",Toast.LENGTH_LONG);
                    toastCenter.setGravity(Gravity.BOTTOM,0,0);
                    toastCenter.show();
                }
                else
                {
                    Toast toastCenter = Toast.makeText(getActivity(),"Please input a valid number",Toast.LENGTH_LONG);
                    toastCenter.setGravity(Gravity.BOTTOM,0,0);
                    toastCenter.show();
                }
            }
        });



        return vMain;
    }

    private class GetUserIdAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
//          得到usreid赋值给静态变量
            USERID = getUserIdFromJson(RestClient.findByCreUsername(params[0]));
            return null;
        }
        //   写一个方法返回寻找到的userID
        public String getUserIdFromJson(String jsonLine) {
//        JsonElement jElement =  new JsonParser().parse(jsonLine);
//        JsonObject jObject = jElement.getAsJsonObject();
            String userId = "";
            try {
                JSONArray jsonArray = new JSONArray(jsonLine);
//           取出包含了CreUserId的这个JSON object，这里只返回一个用户的数据，所以其实就是0
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                JSONObject userObject = jsonObject.getJSONObject("creUserid");
                userId = String.valueOf(userObject.getInt("userId"));
            } catch (JSONException e) {
            }
            return userId;
        }
    }

    private class GetNameAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
//          得到usreid赋值给静态变量
            name = getNameFromJson(RestClient.findFromUserId(Short.valueOf(USERID)));
            return null;
        }
        //   写一个方法返回寻找到的userID
        public String getNameFromJson(String jsonLine) {
            String name = "";
            try {
                JSONObject jsonObject = new JSONObject(jsonLine);
                name = jsonObject.getString("userName");
            } catch (JSONException e) {
            }
            return name;
        }
    }
}
