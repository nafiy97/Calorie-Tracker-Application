package com.example.calorietracker12;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Report extends  Fragment {
    View vDisplayUnit;
    DatePicker repDatePicker;
    Button pieBtn;
    Button barBtn;
    String repDate;
    EditText start;
    EditText end;
    String startDate;
    String endDate;

    //   做piechart的变量
    PieChart pieChart;
    ArrayList<PieEntry> pieEntries = new ArrayList<>();
    PieDataSet pieDataSet;
    PieData pieData;

    //   做barchart的变量
    BarChart barChart;
    ArrayList<BarEntry>  barEntries1 = new ArrayList<>();
    ArrayList<BarEntry>  barEntries2 = new ArrayList<>();

    BarDataSet barDataSet1;
    BarDataSet barDataSet2;
    BarData barData;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.report, container, false);
        repDatePicker = (DatePicker) vDisplayUnit.findViewById(R.id.report_date);
        pieBtn = (Button) vDisplayUnit.findViewById(R.id.pie_btn);
        barBtn = (Button) vDisplayUnit.findViewById(R.id.bar_btn);
        start = (EditText) vDisplayUnit.findViewById(R.id.start_date);
        end = (EditText) vDisplayUnit.findViewById(R.id.end_date);


        pieBtn.setOnClickListener(new View.OnClickListener() {
            //including onClick() method
            public void onClick(View v) {
                int day = repDatePicker.getDayOfMonth();;
                int month = repDatePicker.getMonth();
                int year = repDatePicker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                repDate = sdf.format(calendar.getTime());
                GetReportAsyncTask getReportAsyncTask = new GetReportAsyncTask();
                getReportAsyncTask.execute(new String[]{Mainfragment.USERID, repDate});
            }
        });

        barBtn.setOnClickListener(new View.OnClickListener() {
            //including onClick() method
            public void onClick(View v) {
                GetIntervalCalorieAsyncTask getIntervalCalorieAsyncTask = new GetIntervalCalorieAsyncTask();
                //这里要注意要在click里面取值，在onCreat后立马取值因为没有输入东西只会得到NULL
                startDate = start.getText().toString();
                endDate = end.getText().toString();
                getIntervalCalorieAsyncTask.execute(Mainfragment.USERID,startDate,endDate);
            }
        });

        return vDisplayUnit;
    }




    //这个线程从服务器得到返回的包含report的calories的JSON array
    private class GetReportAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String chartData = RestClient.findByRepUserIdAndRepDate(params[0], params[1]);
            if(! (chartData.equals("[]"))&& ! (chartData.equals(""))&& ! (chartData == null))
            {
                String[] data = getReportDataFromJson(chartData);
                return data;
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] response) {
            if (response != null) {
                pieEntries.clear();
//           这里调用PieChart的作图方法
                pieChart = (PieChart) vDisplayUnit.findViewById(R.id.piechart);
//            修改小数点后位数
                DecimalFormat df = new DecimalFormat("0.00");
//               存百分比用
                double total = 0.0;
                String[] percent = new String[3];
                for (int i = 0; i < 3; i++) {
                    total = total + Double.valueOf(response[i]);
                }
                for (int i = 0; i < 3; i++) {
                    double p;
                    p = 100 * Double.valueOf(response[i]) / total;
                    percent[i] = String.valueOf(df.format(p)) + "%";
                }
                for (int i = 0; i < 3; i++) {
                    {
                        pieEntries.add(new PieEntry(Float.valueOf(response[i]), percent[i]));
                    }

//              这里不知道为什么需要点击一下，才会出先表格
                    pieDataSet = new PieDataSet(pieEntries, "demo");
                    pieData = new PieData(pieDataSet);
                    pieDataSet.setValueTextSize(20);
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieChart.setData(pieData);
                }
                Toast toastCenter = Toast.makeText(getActivity(), "Plot Piechart successfully,please click the first area below", Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM, 0, 0);
                toastCenter.show();
            }
            else if( response == null)
            {
                Toast toastCenter = Toast.makeText(getActivity(), "Please select another day", Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM, 0, 0);
                toastCenter.show();
            }
        }

        //   写一个方法返回report的JSON里的三个数据值
        public String[] getReportDataFromJson(String jsonLine) {
//        JsonElement jElement =  new JsonParser().parse(jsonLine);
//        JsonObject jObject = jElement.getAsJsonObject();
            String[] data = new String[3];
            try {
                JSONArray jsonArray = new JSONArray(jsonLine);
//           取出包含所要data的这个JSON object，这里只返回一个用户的数据，所以其实就是0
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                data[0] = String.valueOf(jsonObject.getDouble("total consumed calorie"));
                data[1] = String.valueOf(jsonObject.getDouble("total burned calorie"));
                data[2] = String.valueOf(jsonObject.getDouble("remain calorie"));
            } catch (JSONException e) {
            }
            return data;
        }
    }

    private class GetIntervalCalorieAsyncTask extends AsyncTask<String, Void, ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(String... params) {
            String chartData = RestClient.findCaloriesByRepUserIdAndStartTimeAndEndTime(params[0], params[1],params[2]);
            if(!chartData.equals("[]") && !chartData.equals("") && ! (chartData == null))
            {
                ArrayList<String[]> data = getCalorieFromJson(chartData);
                return data;
            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<String[]> data) {
            if (data != null) {
                barEntries1.clear();
                barEntries2.clear();
                barChart = (BarChart) vDisplayUnit.findViewById(R.id.barchart);
                float i = 0f;
                for (String[] strArray : data) {
                    barEntries1.add(new BarEntry(i, Float.valueOf(strArray[0])));
                    barEntries2.add(new BarEntry(i, Float.valueOf(strArray[1])));
                    i = i + 1f;
                }
                barDataSet1 = new BarDataSet(barEntries1,"");
                barDataSet1.setValueTextSize(20);
                barDataSet1.setColor(Color.rgb(30,150,30));
                barDataSet2 = new BarDataSet(barEntries2,"");
                barDataSet2.setValueTextSize(20);
                barDataSet2.setColor(Color.rgb(30,30,150));
                barData = new BarData(barDataSet1,barDataSet2);

                barChart.setData(barData);

                Toast toastCenter = Toast.makeText(getActivity(), "Plot Barchart successfully,please click the second area below", Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM, 0, 0);
                toastCenter.show();
            }
            else {
                Toast toastCenter = Toast.makeText(getActivity(), "Date input is invalid", Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM, 0, 0);
                toastCenter.show();
            }
        }

        public ArrayList<String[]> getCalorieFromJson(String jsonLine)
        {
            ArrayList<String[]> data = new ArrayList<>();
            try {
                JSONArray jsonArray= new JSONArray(jsonLine);
                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String[] str = new String[3];
                    str[0]=String.valueOf(jsonObject.getDouble("consumed calorie"));
                    str[1]=String.valueOf(jsonObject.getDouble("burned calorie"));
                    str[2]=jsonObject.getString("date");
                    data.add(str);
                }
            }
            catch (JSONException e) {
            }
            return data;
        }
    }
}