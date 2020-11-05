package com.boardinglabs.mireta.selada.component.network.entities.Report;

public class ReportModels {
    private int item_id;
    private String item_name;
    private String item_price;
    private int item_qty;
    private int category_id;
    private String category_name;

    public ReportModels(int item_id, String item_name, String item_price, int item_qty, int category_id, String category_name) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_qty = item_qty;
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public int getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(int item_qty) {
        this.item_qty = item_qty;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
