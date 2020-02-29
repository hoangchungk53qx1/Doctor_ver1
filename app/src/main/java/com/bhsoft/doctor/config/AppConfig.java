package com.bhsoft.doctor.config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.provider.Settings;

public class AppConfig extends Application {

    @SuppressLint("StaticFieldLeak")
    private  Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }



    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
