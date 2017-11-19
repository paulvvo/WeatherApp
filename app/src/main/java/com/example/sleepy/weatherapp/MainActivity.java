package com.example.sleepy.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    //Global Variables
    int previous=0;
    int current=0;
    Random rand;
    Handler handler;
    TextView inputTextView,showTextView;
    ArrayList<ImageView> imageList;
    DecimalFormat f;
    ImageView imageView,imageView2,imageView3, imageView4, imageView5;

    public class BackgroundTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {

            try {
                String result = "";
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                //Read data from URL
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = "";

                String weather = jsonObject.getString("weather");
                JSONArray weatherArray = new JSONArray(weather);
                for(int i=0; i<weatherArray.length(); i++){
                    JSONObject temp = weatherArray.getJSONObject(i);
                    result += temp.getString("main") + ": " +temp.getString("description")+"\n";
                    //Log.i("main", temp.getString("main"));
                    //Log.i("description", temp.getString("description"));
                }

                String name = jsonObject.getString("name");
                String main  = jsonObject.getString("main");
                JSONObject mainObject = new JSONObject(main);
                String tempKelvin = mainObject.getString("temp");
                Double tempCel = Double.parseDouble(tempKelvin) - 273.15;
                //Log.i("main", mainObject.getString("temp"));



                showTextView.setText("City: " + name + "\n" + result + "Temperature: " + f.format(tempCel) +"C");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch(Exception e){
                Toast.makeText(MainActivity.this, "Please enter a valid city.", Toast.LENGTH_SHORT ).show();
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand = new Random();
        f =  new DecimalFormat("#0.00");
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);

        inputTextView = (TextView) findViewById(R.id.inputTextView);
        showTextView = (TextView) findViewById(R.id.showTextView);

        imageList = new ArrayList<ImageView>(asList(imageView, imageView2, imageView3, imageView4, imageView5));

        handler = new Handler();
        Runnable run = new Runnable(){
            @Override
            public void run() {

                handler.postDelayed(this,10000);
                imageList.get(previous).animate().alpha(0f).setDuration(1000);

                current = rand.nextInt(5);
                while(current == previous){
                    current = rand.nextInt(5);
                }
                previous = current;

                imageList.get(current).animate().alpha(1f).setDuration(1000);
                //Log.i("Timer", "Tick 5 Every Second...");
            }
        };
        handler.post(run);

    }

    public void searchWeather(View view){
        BackgroundTask task = new BackgroundTask();

        //input method manager used to hide the keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputTextView.getWindowToken(),0);

        try{
            //uses open weather api to to retrieve weather
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(inputTextView.getText().toString(),"UTF-8") + "&APPID=2b074ed77f1a65e64391139aa1b323cf");
        }catch(Exception e){
            //toast to show that the city is not valid
            Toast.makeText(MainActivity.this, "Please enter a valid city.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }

    }
}
