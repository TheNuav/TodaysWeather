package com.example.todayssweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.*;
public class MainActivity extends AppCompatActivity {

    RelativeLayout homeRL;
    ProgressBar loadingpb;
    TextView citynametv,temperaturetv,conditiontv;
    RecyclerView weatherrv;
    TextInputEditText cityedt;
    ImageView backiv,iconiv,searchiv;
    ArrayList<WeatherActivity> weatherActivityArrayList;
    WeatherAdapter weatherrvadapter;
    LocationManager locationManager;
    String cityName;
    int PERMISSION_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLhome);
        loadingpb = findViewById(R.id.idPBloading);
        citynametv = findViewById(R.id.idTVcityname);
        temperaturetv = findViewById(R.id.idTVtemperature);
        conditiontv = findViewById(R.id.idtvcondition);
        weatherrv = findViewById(R.id.idRVweather);
        cityedt = findViewById(R.id.idedtcity);
        backiv = findViewById(R.id.idIVback);
        searchiv = findViewById(R.id.idIVback);
        iconiv = findViewById(R.id.idTVvicon);
        weatherActivityArrayList = new ArrayList<>();
        weatherrvadapter = new WeatherAdapter(this, weatherActivityArrayList);
        weatherrv.setAdapter(weatherrvadapter);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "Dehradun";
            getWeatherInfo(cityName);
        }
        searchiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=cityedt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter the city name", Toast.LENGTH_SHORT).show();
                }else{
                    citynametv.setText((cityName));
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "PLEASE gRANT PERMISSIONN", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    String getCityName(double longitude, double latitude){
        String name="not found";
        Geocoder gcd=new Geocoder(getBaseContext(),Locale.getDefault());
        try {
            List <Address> addresses=gcd.getFromLocation(latitude,longitude,10);
            for(Address adr:addresses){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"USER city not found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
            String url="http://api.weatherapi.com/v1/forecast.json?key=340672de6bc4455a83250311232807&q="+ cityName +"&days=1&aqi=yes&alerts=yes";
        citynametv.setText(cityName);
        RequestQueue requestQueue=Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingpb.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherActivityArrayList.clear();
                try {
                    String temperature=response.getJSONObject("current").getString("temp_c");
                    temperaturetv.setText(temperature+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condtion=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String condtionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(condtionIcon)).into(iconiv);
                    conditiontv.setText(condtion);
                    if(isDay==1){
                        Picasso.get().load("https://images.unsplash.com/photo-1595064085577-7c2ef98ec311?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1964&q=80").into(backiv);
                    }else{
                        Picasso.get().load("https://images.unsplash.com/photo-1505322022379-7c3353ee6291?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1500&q=80").into(backiv);
                    }
                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forecasto=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArr=forecasto.getJSONArray("hour");
                    for(int i=0;i<hourArr.length();i++){
                        JSONObject hourObj=hourArr.getJSONObject(i);
                        String time=hourObj.getString("time");
                        String temper=hourObj.getString("temp_c");
                        String img=hourObj.getJSONObject("time").getString("icon");
                        String wind=hourObj.getString("wind_kph");
                        weatherActivityArrayList.add(new WeatherActivity(time,temper,img,wind));

                        
                    }
                    weatherrvadapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}