package com.himanshubaghel.dev.mausam.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.himanshubaghel.dev.mausam.R;
import com.himanshubaghel.dev.mausam.WeatherApplication;
import com.himanshubaghel.dev.mausam.model.WeatherModel;

public class PreferenceUtils {

    public static final String WEATHER_DATA = "Weather_Data";

    public static final String LAST_UPDATED_TIME = "Last_Updated_Time";

    public static SharedPreferences getSharedPreference() {
        Context context = WeatherApplication.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_file), Context.MODE_PRIVATE);
        return sharedPref;
    }

    public static WeatherModel getWeatherModel() {
        String weatherData = getSharedPreference().getString(WEATHER_DATA, "");
        if (!weatherData.isEmpty()) {
            Gson gson = new Gson();

            WeatherModel weatherModel = gson.fromJson(weatherData, WeatherModel.class);
            return weatherModel;
        }
        return null;
    }

    public static void setWeatherModel(WeatherModel weatherModel) {
        String weatherData = (new Gson()).toJson(weatherModel, WeatherModel.class);
        getSharedPreference().edit().putString(WEATHER_DATA, weatherData).apply();
    }

    public static long getLastUpdatedTimeInMillis() {
        return getSharedPreference().getLong(LAST_UPDATED_TIME, 0);
    }

    public static void setLastUpdatedTime(long timeInMillis) {
        getSharedPreference().edit().putLong(LAST_UPDATED_TIME, timeInMillis).apply();
    }
}
