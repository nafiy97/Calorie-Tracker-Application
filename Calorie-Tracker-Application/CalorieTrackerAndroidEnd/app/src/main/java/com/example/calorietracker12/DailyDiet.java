package com.example.calorietracker12;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DailyDiet extends Fragment {
    View vDisplayUnit;
    Button searchBtn;
    EditText searchText;
    TextView result;
    ImageView foodImg;
    Spinner cateSpinner;
    Spinner foodSpinner;

    //  category的变量
    List<HashMap<String, String>> cgListArray;
    SimpleAdapter cgListAdapter;
    ListView cgList;
    String[] cgColHead = new String[] {"Category"};
    int[] cgDataCell = new int[] {R.id.category};

    //   food的变量
    List<HashMap<String, String>> foodListArray;
    SimpleAdapter foodListAdapter;
    ListView foodList;
    String[] foodColHead = new String[] {"Food"};
    int[] foodDataCell = new int[] {R.id.food};
    int foodMaxId;
    int conMaxId;
    int foodId;
    boolean isFoodExist = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.dailydiet, container, false);
        searchBtn = (Button) vDisplayUnit.findViewById(R.id.search_btn);
        CategorySpinnerAsyncTask categorySpinnerAsyncTask = new CategorySpinnerAsyncTask();
        categorySpinnerAsyncTask.execute();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = (EditText) vDisplayUnit.findViewById(R.id.search_txt);
                String keyword = searchText.getText().toString();
                SearchAsyncTask searchAsyncTask=new SearchAsyncTask();
                searchAsyncTask.execute(keyword);
            }
        });
        return vDisplayUnit;
    }

    // 用线程来添加cgSpinner
    private class CategorySpinnerAsyncTask extends AsyncTask<Void, Void,List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> list = new ArrayList<>();
            list.add("Sauce");
            list.add("Vegetable");
            list.add("Meat");
            list.add("Drink");
            list.add("Snack");
            list.add("Fruit");
            list.add("Others");
            return list;
        }
        protected void onPostExecute(List<String> cates) {
            if(cates != null)
            {
                cateSpinner = (Spinner) vDisplayUnit.findViewById(R.id.category_spinner);
                final ArrayAdapter<String> cgSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, cates);
                cgSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cateSpinner.setAdapter(cgSpinnerAdapter);
                cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String cgSelected = parent.getItemAtPosition(position).toString();
                        GetFoodFromCateAsyncTask getFoodFromCateAsyncTask = new GetFoodFromCateAsyncTask();
                        getFoodFromCateAsyncTask.execute(cgSelected);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    }

    // 用线程来添加cgSpinner
    private class FoodSpinnerAsyncTask extends AsyncTask<ArrayList<String>, Void,List<String>> {
        @Override
        protected List<String> doInBackground(ArrayList<String>... params) {
            List<String> list = new ArrayList<>();
            for(String str:params[0])
            {
                list.add(str);
            }
            return list;
        }
        @Override
        protected void onPostExecute(List<String> foods) {
            if (foods != null)
            {
                foodSpinner = (Spinner) vDisplayUnit.findViewById(R.id.food_spinner);
                ArrayAdapter<String> foodSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, foods);
                foodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                foodSpinner.setAdapter(foodSpinnerAdapter);
                foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String foodSelected = parent.getItemAtPosition(position).toString();
                        GetFoodIdAsyncTask getFoodIdAsyncTask = new GetFoodIdAsyncTask();
                        getFoodIdAsyncTask.execute(foodSelected);
                        FindMaxConIdlAsyncTask findMaxConIdlAsyncTask = new FindMaxConIdlAsyncTask();
                        findMaxConIdlAsyncTask.execute();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        final String now = sdf.format(Calendar.getInstance().getTime());
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PostConsumptionAsyncTask postConsumptionAsyncTask = new PostConsumptionAsyncTask();
                                postConsumptionAsyncTask.execute(String.valueOf(conMaxId),now,"1",String.valueOf(foodId),Mainfragment.USERID);
                            }
                        },1000);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    }

    //   调用REST方法得到包含的food
    private class GetFoodFromCateAsyncTask extends AsyncTask<String, Void,ArrayList<String>> {
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> foods =getFoodDataFromJson(RestClient.findByFoodCategory(params[0]));
            return foods;
        }

        @Override
        protected void onPostExecute(ArrayList<String> foods) {
            if(foods != null)
            {
                FoodSpinnerAsyncTask foodSpinnerAsyncTask = new FoodSpinnerAsyncTask();
                foodSpinnerAsyncTask.execute(foods);
            }
            else {
                Toast toastCenter = Toast.makeText(getActivity(), "We don't have this type foods in database", Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM, 0, 0);
                toastCenter.show();
            }
        }

        public ArrayList<String> getFoodDataFromJson(String jsonLine){
            ArrayList<String> foods = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(jsonLine);
                for(int i = 0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    foods.add(jsonObject.getString("foodName"));
                }
            }
            catch (Exception e) {
            }
            return foods;
        }
    }


    //  搜索输入食物
    private class SearchAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String response = SearchFoodAPI.search(params[0]);
            String[] data = getSnippet(response);
            return data;
        }
        @Override
        protected void onPostExecute(final String[] response) {
            if(response!=null)
            {
                result= (TextView) vDisplayUnit.findViewById(R.id.search_result);
                foodImg = (ImageView) vDisplayUnit.findViewById(R.id.food_img);
                result.setText(response[4]);
                new DownloadImageTask(foodImg).execute(response[5]);
                FindMaxFoodIdlAsyncTask findMaxFoodIdlAsyncTask = new FindMaxFoodIdlAsyncTask();
                findMaxFoodIdlAsyncTask.execute();
                IsExistedFood isExistedFood = new IsExistedFood();
                isExistedFood.execute(response[0]);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isFoodExist == false)
                        {
                            PostFoodAsyncTask postFoodAsyncTask = new PostFoodAsyncTask();
                            postFoodAsyncTask.execute(new String[]{String.valueOf(foodMaxId), response[0], response[1], response[2],"100gr", "1", response[3]});
                        }
                     }
                },2000);
            }
            else{
                Toast toastCenter = Toast.makeText(getActivity(),"we don't find this food",Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }

        }

        public String[] getSnippet(String response){
            if(response!=null && !(response.equals("[]"))&& response.length()!=0)
            {
                String[] result = new String[6];
                try{
                    JSONObject jsonObject = new JSONObject(response);
//          得到food名字
                    result[0]=jsonObject.getString("text");
                    JSONArray parsed = jsonObject.getJSONArray("parsed");
                    JSONObject temp = parsed.getJSONObject(0);
                    JSONObject food = temp.getJSONObject("food");
//          food种类
                    result[1]= food.getString("category");
//          得到food 描述
                    result[4]=food.getString("label");
//          food图片
                    result[5]=food.getString("image");
                    JSONObject nutrients = food.getJSONObject("nutrients");
//           得到卡路里和fat
                    result[2]=String.valueOf(nutrients.getDouble("ENERC_KCAL"));
                    result[3]=String.valueOf(nutrients.getDouble("FAT"));

                }catch (Exception e){
                    result[4]="No food found";
                }
                return result;
            }
            return null;
        }
    }

    private class GetFoodIdAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            foodId = getFoodIdFromJson(RestClient.findByFoodName(params[0]));
            return null;
        }
        public int getFoodIdFromJson(String jsonLine)
        {
            int foodid = 0;
            try {
                JSONArray jsonArray = new JSONArray(jsonLine);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                foodid = jsonObject.getInt("foodId");
            }
            catch (Exception e) {

            }
            return foodid;
        }

    }

    private class PostFoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //     将food Post到服务器
            Food food = new Food(Short.parseShort(params[0]),params[1],params[2],Double.parseDouble(params[3]),params[4],Double.parseDouble(params[5]),Double.parseDouble(params[6]));
            RestClient.createFood(food);
            return "Food was added";
        }

        @Override
        protected void onPostExecute(String response) {
            if(response!=null)
            {
                Toast toastCenter = Toast.makeText(getActivity(),response,Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
            else
            {
                Toast toastCenter = Toast.makeText(getActivity(),"Food add fail",Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
        }
    }

    private class PostConsumptionAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //     将food Post到服务器
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
            Date now = null;
            try
            {
                now =sdf.parse(params[1]);
            }
            catch (Exception e) {
            }
            Consumption consumption = new Consumption(Short.valueOf(params[0]),now,Double.valueOf(params[2]),new Food(Short.valueOf(params[3])),new Users(Short.valueOf(params[4])));
            RestClient.createConsumption(consumption);
            return "Consumption was added";
        }

        @Override
        protected void onPostExecute(String response) {
            if(response != null)
            {
                Toast toastCenter = Toast.makeText(getActivity(),response,Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
            else
            {
                Toast toastCenter = Toast.makeText(getActivity(),"Consumption add fail",Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
        }
    }

    //   网上找的下载更新图片的线程
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class FindMaxFoodIdlAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findMaxFoodId();
        }

        @Override
        protected void onPostExecute(String response) {
            foodMaxId = Integer.valueOf(response)+1;
        }
    }

    private class FindMaxConIdlAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.findMaxConId();
        }

        @Override
        protected void onPostExecute(String response) {
            conMaxId = Integer.valueOf(response)+1;
        }
    }

    private class IsExistedFood extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //     将food Post到服务器
            String response = RestClient.findByFoodName(params[0]);
            if(response.length()>10)
            {
                isFoodExist = true;
                return "Food exists in database";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if(response!=null)
            {
                Toast toastCenter = Toast.makeText(getActivity(),response,Toast.LENGTH_LONG);
                toastCenter.setGravity(Gravity.BOTTOM,0,0);
                toastCenter.show();
            }
        }
    }
}