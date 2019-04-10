package com.example.banggemang;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

import org.litepal.LitePal;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化LitePal
        LitePal.initialize(this);
        //初始化QMUI arch
        QMUISwipeBackActivityManager.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
