package com.himanshubaghel.dev.mausam;


import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public class WeatherApplication extends Application{

    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        context = this;
    }
}
