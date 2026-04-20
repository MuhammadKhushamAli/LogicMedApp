package com.example.logicmed;

import android.app.Application;

import java.util.List;

public class MyApplication extends Application {
    public List<String> cities;
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
