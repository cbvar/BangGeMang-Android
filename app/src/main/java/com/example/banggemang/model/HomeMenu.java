package com.example.banggemang.model;

import com.example.banggemang.base.BaseFragment;

public class HomeMenu {

    private String mName;
    private int mIconRes;

    private Class<? extends BaseFragment> mFragment;

    public HomeMenu(String name, int iconRes) {
        this(name, iconRes, null);
    }

    public HomeMenu(String name, int iconRes, Class<? extends BaseFragment> fragment) {
        mName = name;
        mIconRes = iconRes;
        mFragment = fragment;
    }

    public String getName() {
        return mName;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public Class<? extends BaseFragment> getFragment() {
        return mFragment;
    }
}
