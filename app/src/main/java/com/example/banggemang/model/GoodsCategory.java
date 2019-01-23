package com.example.banggemang.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class GoodsCategory extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String name;

    private int pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
