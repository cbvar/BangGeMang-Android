package com.example.banggemang.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class GoodsCategory extends LitePalSupport {

    private int id;

    @Column(unique = true)
    private String name;

    private int pid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
