package com.example.banggemang.base;

import android.content.Context;
import android.content.res.Configuration;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {

    @Override
    protected void attachBaseContext(Context base) {
        Configuration configuration = base.getResources().getConfiguration();
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f;
            base = base.createConfigurationContext(configuration);
        }
        super.attachBaseContext(base);
    }
}
