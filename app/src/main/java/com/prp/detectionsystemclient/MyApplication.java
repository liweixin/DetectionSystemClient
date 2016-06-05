package com.prp.detectionsystemclient;

import android.app.Application;
import android.content.Context;

/**
 * Created by lwx on 2016/6/2.
 */
public class MyApplication extends Application {
    private Context context;
    public static MyApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }
    public Context getContext(){
        if(context==null){
            context = getApplicationContext();
        }
        return context;
    }
}
