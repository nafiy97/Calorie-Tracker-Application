package com.example.calorietracker12;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Signup extends AppCompatActivity {
    Button regBtn;
    EditText fname;
    EditText sname;
    EditText email;
    EditText height;
    EditText weight;
    EditText address;
    EditText postcode;
    EditText stepPerMile;
    EditText username;
    EditText psword;
    RadioGroup radioGroup;
    Spinner actSpinner;
    DatePicker dob;
    boolean userNameCheck=false;
    boolean emailCheck=false;
    int userId= 0;
    int creId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        regBtn = (Button) findViewById(R.id.regBtn);
        fname = (EditText) findViewById(R.id.fname_txt);
        sname = (EditText) findViewById(R.id.sname_txt);
        email = (EditText) findViewById(R.id.email_txt);
        height = (EditText) findViewById(R.id.height_txt);
        weight = (EditText) findViewById(R.id.weight_txt);
        address = (EditText) findViewById(R.id.address_txt);
        postcode = (EditText) findViewById(R.id.postcode_txt);
        stepPerMile = (EditText) findViewById(R.id.stepPerMile_txt);
        username = (EditText) findViewById(R.id.username_txt);
        psword = (EditText) findViewById(R.id.psword_txt);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupGender);
        actSpinner = (Spinner) findViewById(R.id.actSpinner);
        dob = (DatePicker) findViewById(R.id.dob);

        //       给register按钮设置监听,进入注册账号界面
        regBtn.setOnClickListener(new View.OnClickListener() {
            //including onClick() method
            public void onClick(View v) {
                //        取出输入的account和password的值
                final PostUsersAsyncTask postUserAsyncTask = new PostUsersAsyncTask();
                final PostCredentialAsyncTask postCredentialAsyncTask = new PostCredentialAsyncTask();
                //      将datepicker的日期转换为Calendar对象
                int day = dob.getDayOfMonth();
                int month = dob.getMonth();
                int year = dob.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                //                再利用simpledateformat去生成指定格式的日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //                再转换为date格式
                final String formatedDate = sdf.format(calendar.getTime());
                //                从radio里取值
                int selectId = radioGroup.getCheckedRadioButtonId();
                final RadioButton selectBtn = (RadioButton) findViewById(selectId);
                //                从spinner里取值
                final String actlevel = actSpinner.getSelectedItem().toString();
//               从后台取出UserId和creId最大值+1
                FindMaxUserIdAsyncTask findMaxUserIdAsyncTask = new FindMaxUserIdAsyncTask();
                FindMaxCreIdAsyncTask findMaxCreIdAsyncTask = new FindMaxCreIdAsyncTask();
                findMaxUserIdAsyncTask.execute();
                findMaxCreIdAsyncTask.execute();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!(fname.getText().toString().isEmpty()) && !(sname.getText().toString().isEmpty()) &&
                                !(email.getText().toString().isEmpty()) && !(formatedDate.isEmpty()) &&
                                !(height.getText().toString().isEmpty()) && !(weight.getText().toString().isEmpty()) &&
                                !(selectBtn.getText().toString().isEmpty()) && !(address.getText().toString().isEmpty()) &&
                                !(postcode.getText().toString().isEmpty()) && !(actlevel.isEmpty()) &&
                                !(stepPerMile.getText().toString().isEmpty()) && !(username.getText().toString().isEmpty()) &&
                                !(psword.getText().toString().isEmpty()) && (height.getText().toString().matches("[0-9]+"))
                                && (weight.getText().toString().matches("[0-9]+"))&& (stepPerMile.getText().toString().matches("[0-9]+"))) {
                            CheckRepeatEmailAsyncTask checkRepeatEmailAsyncTask = new CheckRepeatEmailAsyncTask();
                            checkRepeatEmailAsyncTask.execute();
                            CheckRepeatUsernameAsyncTask checkRepeatUsernameAsyncTask = new CheckRepeatUsernameAsyncTask();
                            checkRepeatUsernameAsyncTask.execute();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(emailCheck==false && userNameCheck==false)
                                    {
                                        postUserAsyncTask.execute(String.valueOf(userId), fname.getText().toString(),
                                                sname.getText().toString(), email.getText().toString(),
                                                address.getText().toString(), postcode.getText().toString(),
                                                selectBtn.getText().toString(), actlevel,
                                                stepPerMile.getText().toString(), weight.getText().toString(),
                                                height.getText().toString(), formatedDate);
                                        postCredentialAsyncTask.execute(String.valueOf(creId),String.valueOf(userId), fname.getText().toString(),
                                                sname.getText().toString(), email.getText().toString(),
                                                address.getText().toString(), postcode.getText().toString(),
                                                selectBtn.getText().toString(), actlevel,
                                                stepPerMile.getText().toString(), weight.getText().toString(),
                                                height.getText().toString(), formatedDate,
                                                username.getText().toString(),getMD5String(psword.getText().toString()));
                                        Toast toastCenter = Toast.makeText(getApplicationContext(),"Sign up successfully",Toast.LENGTH_LONG);
                                        toastCenter.setGravity(Gravity.BOTTOM,0,0);
                                        toastCenter.show();
                                        Intent intent = new Intent(Signup.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast toastCenter = Toast.makeText(getApplicationContext(),"Username or email is existed",Toast.LENGTH_LONG);
                                        toastCenter.setGravity(Gravity.BOTTOM,0,0);
                                        toastCenter.show();
                                    }
                                }
                             },2000);
                        } else {
                            Toast toastCenter = Toast.makeText(getApplicationContext(),"Please fill in all informations in correct way",Toast.LENGTH_LONG);
                            toastCenter.setGravity(Gravity.BOTTOM,0,0);
                            toastCenter.show();
                        }
                    }
                },2000);
            }
        });
    }
    private class PostUsersAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = null;
            try {
                dob = sdf.parse(params[11]);
            } catch (Exception e) {
            }
            //            将Users Post到服务器
            Users user = new Users(Short.valueOf(params[0]), params[1], params[2], params[3], params[4], params[5],
                    params[6], Short.valueOf(params[7]), Short.valueOf(params[8]), Double.valueOf(params[9]), Double.valueOf(params[10]), dob);
            RestClient.createUser(user);
            return "User was added";
        }

        @Override
        protected void onPostExecute(String response) {
            Toast toastCenter = Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG);
            toastCenter.setGravity(Gravity.BOTTOM,0,0);
            toastCenter.show();
        }
    }

    private class PostCredentialAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date now = null;
            Date dob = null;
            try {
                dob = sdf.parse(params[12]);
            } catch (Exception e) {
            }
            //            将Credential Post到服务器，因为里面包含了User的对象，所以要创建一个User的匿名对象
            Credential credential = new Credential(Short.valueOf(params[0]),new Users(Short.valueOf(params[1]), params[2], params[3], params[4], params[5], params[6],
                    params[7], Short.valueOf(params[8]), Short.valueOf(params[9]), Double.valueOf(params[10]), Double.valueOf(params[11]),dob),Calendar.getInstance().getTime(),params[13],params[14]);
            RestClient.createCredential(credential);
            return "Credential was added";
        }

        @Override
        protected void onPostExecute(String response) {

        }
    }

    public static String getMD5String(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private class FindMaxUserIdAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findMaxUserId();
        }

        @Override
        protected void onPostExecute(String response) {
            userId = Integer.valueOf(response)+1;
        }
    }

    private class FindMaxCreIdAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findMaxCreId();
        }

        @Override
        protected void onPostExecute(String response) {
            creId = Integer.valueOf(response)+1;
        }
    }

    private class CheckRepeatEmailAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findByUserEmail(email.getText().toString());
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.length()>10)
            {
                emailCheck = true;
            }
        }
    }

    private class CheckRepeatUsernameAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findByCreUsername(username.getText().toString());
        }

        @Override
        protected void onPostExecute(String response) {
            if(response.length()>10)
            {
                userNameCheck=true;
            }
        }
    }
}
