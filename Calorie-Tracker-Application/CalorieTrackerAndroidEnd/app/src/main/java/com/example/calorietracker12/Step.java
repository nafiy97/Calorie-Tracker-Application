package com.example.calorietracker12;


import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.validation.TypeInfoProvider;

public class Step extends Fragment {
    EditText stepText;
    Button stepBtn;
    View vDisplayUnit;
    OneDayStepDatabase db;
    Button postBtn;
    int repMaxId;


    //   steplist的变量
    List<HashMap<String, String>> stepListArray;
    SimpleAdapter stepListAdapter;
    ListView stepList;
    String[] stepColHead = new String[] {"Step","Time"};
    int[] stepDataCell = new int[] {R.id.lits_step,R.id.list_time};

    //  调用post要用的变量
    String concal;
    String burncal;
    int total_step;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.step, container, false);
        stepBtn = (Button) vDisplayUnit.findViewById(R.id.step_button);
        stepText = (EditText) vDisplayUnit.findViewById(R.id.step_input);
        postBtn =(Button) vDisplayUnit.findViewById(R.id.post_step);

        db = Room.databaseBuilder(getActivity(), OneDayStepDatabase.class, "step_database").fallbackToDestructiveMigration().build();

        //   注意这里要先有数据库再去调用线程
        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();


        stepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDatabase insertDatabase  = new InsertDatabase();
                insertDatabase.execute();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //       单独写出来的零点发送的部分
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

                FindMaxRepIdlAsyncTask findMaxRepIdlAsyncTask =  new FindMaxRepIdlAsyncTask();
                findMaxRepIdlAsyncTask.execute();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
                        String now = sdf.format(Calendar.getInstance().getTime());
                        PostReportAsyncTask postReportAsyncTask = new PostReportAsyncTask();
                        postReportAsyncTask.execute(new String[] {String.valueOf(repMaxId),now,concal,burncal,String.valueOf(total_step),Mainfragment.SETTINGGOAL,Mainfragment.USERID,});
                    }},2000);

            }
        });
        return vDisplayUnit;
    }

    //  后台线程展示step list
    private class StepListAsyncTask extends AsyncTask<ArrayList<String[]>, Void,SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(ArrayList<String[]>... params) {
            stepList=(ListView) vDisplayUnit.findViewById(R.id.stepList);
            stepListArray =new ArrayList<>();
            for (String[] str:params[0]) {
                HashMap<String,String> newMap = new HashMap<>();
                newMap.put("Step","Step:" + str[0]);
                newMap.put("Time"," Time:" + str[1]);
                stepListArray.add(newMap);
            }
            stepListAdapter = new SimpleAdapter(getActivity(),stepListArray,R.layout.step_list,stepColHead,stepDataCell)
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    Button btn = (Button) v.findViewById(R.id.list_btn);
                    final TextView text = (TextView) v.findViewById(R.id.list_time);
                    final EditText editText = (EditText) v.findViewById(R.id.list_edit);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                          要把这两个放到监听器里面来，不然的话就会直接载入时读取空值
                            String input = editText.getText().toString();
                            String time = text.getText().toString().replaceAll(" Time:","");
                            if (!(input.isEmpty()) && (input.matches("[0-9]+")))
                            {
                                UpdateDatabase updateDatabase = new UpdateDatabase();
                                updateDatabase.execute(new String[] {time,input});
                            }
                            else
                            {
                                Toast toastCenter = Toast.makeText(getActivity(),"Please input available number",Toast.LENGTH_LONG);
                                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                                toastCenter.show();
                            }
                        }
                    });
                    return v;
                }
            };
            return stepListAdapter;
        }
        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            stepList.setAdapter(adapter);
        }
    }


    private class InsertDatabase extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String step = stepText.getText().toString();
            if(step != null && step.length()>0 && step.matches("[0-9]+"))
            {
                //      得到存入时的时间
//          这里只要改成其它格式就直接GG
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                Calendar calendar = Calendar.getInstance();
                String formatedDate = sdf.format(calendar.getTime());
//      判断是否为空
                if (!(step.isEmpty()) && (step.matches("[0-9]+"))) {
                    OneDayStep oneDayStep = new OneDayStep(Integer.parseInt(step), formatedDate);
                    long id = db.oneDayStepDao().insert(oneDayStep);
                    return (" Steps:" + step + " and Time:" + formatedDate);
                }

            }
                return null;
        }

        @Override
        protected void onPostExecute(String details) {
            if(details!=null)
            {
                ReadDatabase readDatabase = new ReadDatabase();
                readDatabase.execute();
                Toast toastCenter = Toast.makeText(getActivity(),"Added" + details,Toast.LENGTH_LONG);
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
    }
    private class ReadDatabase extends AsyncTask<Void, Void, ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(Void... params) {
            List<OneDayStep> oneDaySteps = db.oneDayStepDao().getAll();
            ArrayList<String[]> records = new ArrayList<>();
            if (!(oneDaySteps.isEmpty() || oneDaySteps == null) ){
                for(OneDayStep temp : oneDaySteps) {
                    String[] str = new String[2];
                    str[0] = String.valueOf(temp.getStep());
                    str[1] = String.valueOf(temp.getTime());
                    records.add(str);
                }
                return records;
            }
            else
                return null;
        }
        @Override
        protected void onPostExecute(ArrayList<String[]> records) {
//           注意这里判断不是null不能用equals，因为这样也会把null作为参数传给equals方法
            if(records!= null)
            {
                //           这里把records作为参数传给生成list的方法
                StepListAsyncTask stepListAsyncTask = new StepListAsyncTask();
                stepListAsyncTask.execute(records);
            }
            else
            {
                Toast toastCenter = Toast.makeText(getActivity(),"Please add step",Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
        }
    }
    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            db.oneDayStepDao().deleteAll();
            return null;
        }
        protected void onPostExecute(Void param) {
//          直接设为null可能很危险，但是我只想偷懒
            stepList.setAdapter(null);
            Toast toastCenter = Toast.makeText(getActivity(),"All data was deleted",Toast.LENGTH_LONG);
            toastCenter.setGravity(Gravity.BOTTOM,0,0);
            toastCenter.show();
        }
    }
    private class UpdateDatabase extends AsyncTask<String, Void, String> {
        @Override protected String doInBackground(String... params) {
            OneDayStep oneDayStep;
            String time = params[0];
            String input= params[1];
            oneDayStep = db.oneDayStepDao().findByTime(time);
            oneDayStep.setStep(Integer.parseInt(input));
            if (oneDayStep!=null) {
                db.oneDayStepDao().updateOneDayStep(oneDayStep);
                return ("Step:"+input);
            }
            return "";
        }
        @Override
        protected void onPostExecute(String details) {
            ReadDatabase readDatabase = new ReadDatabase();
            readDatabase.execute();
            Toast toastCenter = Toast.makeText(getActivity(),"Updeate details:"+details,Toast.LENGTH_LONG);
            toastCenter.setGravity(Gravity.BOTTOM,0,0);
            toastCenter.show();;
        }
    }

//  到24点就将Report发送到服务器，24点发送的方法还没写

    private class PostReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //     将report Post到服务器
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
            Date now = null;
            try {
                now =sdf.parse(params[1]);
            }
            catch (Exception e) {
            }
            Reports reports = new Reports(Short.valueOf(params[0]),now,Double.valueOf(params[2]),Double.valueOf(params[3]),Integer.valueOf(params[4]),Double.valueOf(params[5]),new Users(Short.valueOf(params[6])));
            RestClient.createReport(reports);
            return "Report was added";
        }

        @Override
        protected void onPostExecute(String response) {
            DeleteDatabase deleteDatabase = new DeleteDatabase();
            deleteDatabase.execute();
            Toast toastCenter = Toast.makeText(getActivity(),response,Toast.LENGTH_LONG);
            toastCenter.setGravity(Gravity.BOTTOM,0,0);
            toastCenter.show();
        }
    }

    //  找出最大值
    private class FindMaxRepIdlAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findMaxRepId();
        }

        @Override
        protected void onPostExecute(String response) {
            repMaxId = Integer.valueOf(response)+1;
        }
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
            return null;
        }
    }

    private class GetTotalConsumedCalorie extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            DecimalFormat df = new DecimalFormat( "0.000");
            concal = df.format(Double.valueOf(RestClient.findTotalCalorieConsumed(params[0], params[1])));
            return null;
        }
    }

    private class GetTotalBurnedCalorie extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            DecimalFormat df = new DecimalFormat( "0.000");
            double restCal = Double.valueOf(RestClient.findTotalRestCalorieBurned(params[0]));
            double stepCal = Double.valueOf(RestClient.findCalorieBurnedPerStep(params[0])) * total_step;
            burncal = df.format(restCal + stepCal);
            return  null;
        }
    }
}
