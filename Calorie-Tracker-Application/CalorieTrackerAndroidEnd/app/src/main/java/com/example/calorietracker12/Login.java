package com.example.calorietracker12;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private Boolean check = false;
    //   将用户名设置为静态变量全局可以调用
    public static String ACTPASS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button logBtn = (Button) findViewById(R.id.logBtn);
        final EditText account = (EditText) findViewById(R.id.account_txt);
        final EditText password = (EditText) findViewById(R.id.password_txt);


        //       给login按钮设置监听,判断账户密码是否都正确
        logBtn.setOnClickListener(new View.OnClickListener() {
            //including onClick() method
            public void onClick(View v) {
                //        取出输入的account和password的值
                String act = account.getText().toString();
//              存入输入的用户账号以便后面使用
                ACTPASS = act;
                String psw = Signup.getMD5String(password.getText().toString());
                UsernameAndPasswordAsyncTask task = new UsernameAndPasswordAsyncTask();
                task.execute(new String[]{act,psw});
            }
        });
    }


    private class UsernameAndPasswordAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground (String...params){
            return RestClient.findByCreUsernameAndCrePasshash(params[0],params[1]);
        }
        @Override
        protected void onPostExecute (String str){
//          如果能返回东西，也就是返回的JSON长度大于10，那就将BOOLEAN设置为true
            if( str.length() >= 20) {
                check = true;
            }
            if(check == true)
            {
                Intent intent = new Intent(Login.this, HomeActivity.class);
                startActivity(intent);
            }
            else{
                check = false;
                Toast toastCenter = Toast.makeText(getApplicationContext(),"Account or password is wrong",Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
        }
    }
}
