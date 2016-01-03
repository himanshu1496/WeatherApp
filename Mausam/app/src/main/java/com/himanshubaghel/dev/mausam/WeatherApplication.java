package com.himanshubaghel.dev.mausam;


import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class WeatherApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
