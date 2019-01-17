package com.example.banggemang.model;

public class HomeItem {

    private String name;
    private int iconRes;

    public HomeItem(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return iconRes;
    }
}
