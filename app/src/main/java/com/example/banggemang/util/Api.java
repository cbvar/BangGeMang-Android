package com.example.banggemang.util;

import com.example.banggemang.model.Goods;
import com.example.banggemang.model.GoodsCategory;
import com.example.banggemang.model.GoodsUnit;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class Api {

    public static final int INT_NONE = -1;

    public static class GoodsItem {
        public int id;
        public String name;
        public String unit;
        public float costPrice;
        public float retailPrice;
    }

    public static class CheckoutItem {
        public int id;
        public String name;
        public float unitPrice;
        public String unit;
        public float quantity;
        public float money;
    }

    public static List<GoodsUnit> getGoodsUnitList() {
        return LitePal.findAll(GoodsUnit.class);
    }

    public static GoodsUnit getGoodsUnit(int id) {
        return LitePal.find(GoodsUnit.class, id);
    }

    public static boolean addGoodsUnit(String name) {
        GoodsUnit item = new GoodsUnit();
        item.setName(name);
        return item.save();
    }

    public static boolean updateGoodsUnit(int id, String name) {
        GoodsUnit item = LitePal.find(GoodsUnit.class, id);
        item.setName(name);
        return item.save();
    }

    public static boolean deleteGoodsUnit(int id) {
        int count = LitePal.where("unitId = ?", Integer.toString(id)).count(Goods.class);
        if (count > 0) {
            return false;
        }
        return LitePal.delete(GoodsUnit.class, id) > 0;
    }

    public static List<GoodsCategory> getGoodsCategoryList() {
        return LitePal.where("pid = 0").find(GoodsCategory.class);
    }

    public static List<GoodsCategory> getGoodsCategoryList(int pid) {
        return LitePal.where("pid = ?", Integer.toString(pid)).find(GoodsCategory.class);
    }

    public static GoodsCategory getGoodsCategory(int id) {
        return LitePal.find(GoodsCategory.class, id);
    }

    public static boolean addGoodsCategory(String name) {
        return addGoodsCategory(name, 0);
    }

    public static boolean addGoodsCategory(String name, int pid) {
        GoodsCategory item = new GoodsCategory();
        item.setName(name);
        item.setPid(pid);
        return item.save();
    }

    public static boolean updateGoodsCategory(int id, String name) {
        GoodsCategory item = LitePal.find(GoodsCategory.class, id);
        item.setName(name);
        return item.save();
    }

    public static boolean deleteGoodsCategory(int id) {
        GoodsCategory item = LitePal.find(GoodsCategory.class, id);
        int count;
        if (item.getPid() == 0) {
            count = LitePal.where("pid = ?", Integer.toString(id)).count(GoodsCategory.class);
        } else {
            count = LitePal.where("categoryId = ?", Integer.toString(id)).count(Goods.class);
        }
        if (count > 0) {
            return false;
        }
        return item.delete() > 0;
    }

    public static List<GoodsItem> getGoodsList(int categoryId, int unitId) {
        List<Goods> items;
        List<GoodsItem> data = new ArrayList<>();
        if (categoryId == INT_NONE && unitId == INT_NONE) {
            items = LitePal.findAll(Goods.class);
        } else {
            String conditions = "";
            if (categoryId != INT_NONE) {
                GoodsCategory category = LitePal.find(GoodsCategory.class, categoryId);
                if (category.getPid() != 0) {
                    conditions += "categoryId = " + Integer.toString(categoryId);
                } else {
                    List<GoodsCategory> categories = LitePal.where("pid = ?", Integer.toString(category.getId())).find(GoodsCategory.class);
                    int length = categories.size();
                    if (length == 0) {
                        return data;
                    }
                    conditions += "categoryId IN (";
                    for (int i = 0; i < length; ++i) {
                        conditions += Integer.toString(categories.get(i).getId());
                        if (i != length - 1) {
                            conditions += ",";
                        }
                    }
                    conditions += ")";
                }
            }
            if (categoryId != INT_NONE && unitId != INT_NONE) {
                conditions += " AND ";
            }
            if (unitId != INT_NONE) {
                conditions += "unitId = " + Integer.toString(unitId);
            }
            items = LitePal.where(conditions).find(Goods.class);
        }
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

    public static Goods getGoods(int id) {
        return LitePal.find(Goods.class, id);
    }

    public static Goods getGoodsWithCode(String barCode) {
        return LitePal.where("barCode = ?", barCode).findFirst(Goods.class);
    }

    public static List<Goods> getGoodsListWithName(String name) {
        return LitePal.where("name LIKE ?", "%" + name + "%").limit(10).find(Goods.class);
    }

    public static boolean addGoods(String name, int categoryId, int unitId, float costPrice, float retailPrice, String barCode, String description) {
        Goods item = new Goods();
        item.setName(name);
        item.setCategoryId(categoryId);
        item.setUnitId(unitId);
        item.setCostPrice(costPrice);
        item.setRetailPrice(retailPrice);
        item.setBarCode(barCode);
        item.setDescription(description);
        return item.save();
    }

    public static boolean updateGoods(int id, String name, int categoryId, int unitId, float costPrice, float retailPrice, String barCode, String description) {
        Goods item = LitePal.find(Goods.class, id);
        item.setName(name);
        item.setCategoryId(categoryId);
        item.setUnitId(unitId);
        item.setCostPrice(costPrice);
        item.setRetailPrice(retailPrice);
        item.setBarCode(barCode);
        item.setDescription(description);
        return item.save();
    }

    public static boolean deleteGoods(int id) {
        return LitePal.delete(Goods.class, id) > 0;
    }

}
