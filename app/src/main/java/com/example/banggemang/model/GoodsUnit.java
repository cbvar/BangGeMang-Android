package com.example.banggemang.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class GoodsUnit extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
