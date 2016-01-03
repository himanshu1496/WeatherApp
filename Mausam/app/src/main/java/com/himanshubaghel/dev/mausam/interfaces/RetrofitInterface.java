package com.himanshubaghel.dev.mausam.interfaces;


import com.himanshubaghel.dev.mausam.model.WeatherModel;

import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface RetrofitInterface {
    @GET("v1/public/yql")
    Call<WeatherModel> getWeather(@QueryMap HashMap<String, String > map);

}
