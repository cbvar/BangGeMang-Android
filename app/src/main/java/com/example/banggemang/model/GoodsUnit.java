package com.example.banggemang.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class GoodsUnit extends LitePalSupport {

    private int id;

    @Column(unique = true)
    private String name;

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
}
