package com.example.banggemang.util;

import com.example.banggemang.model.Goods;
import com.example.banggemang.model.GoodsUnit;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class Api {
    public static class GoodsItem {
        public int id;
        public String name;
        public String unit;
        public float costPrice;
        public float retailPrice;
    }

    public static List<GoodsItem> getGoodsList() {
        List<Goods> items = LitePal.findAll(Goods.class);
        List<GoodsItem> data = new ArrayList<>();
        for (Goods item : items) {
            GoodsUnit unit = LitePal.find(GoodsUnit.class, item.getUnitId());

            GoodsItem nItem = new GoodsItem();
            nItem.id = item.getId();
            nItem.name = item.getName();
            nItem.unit = unit.getName();
            nItem.costPrice = item.getCostPrice();
            nItem.retailPrice = item.getRetailPrice();

            data.add(nItem);
        }
        return data;
    }

    public static boolean deleteGoods(int id) {
        return LitePal.delete(Goods.class, id) > 0;
    }
}
