package com.boardinglabs.mireta.selada.modul.master.stok.inventori.model;

import java.util.List;

public class Item {

    private String name;
    private String description;
    private String business_id;
    private String brand_id;
    private String price;
    private String category_id;
    private String _method;
    private String is_daily_stock;
    private List<NewStocks> new_stocks;

    public Item(String name, String description, String business_id, String brand_id, String price, String category_id, String is_daily_stock, List<NewStocks> new_stocks) {
        this.name = name;
        this.description = description;
        this.business_id = business_id;
        this.brand_id = brand_id;
        this.price = price;
        this.category_id = category_id;
        this.is_daily_stock = is_daily_stock;
        this.new_stocks = new_stocks;
    }

    public Item(String _method, List<NewStocks> new_stocks) {
        this._method = _method;
        this.new_stocks = new_stocks;
    }

    public String get_method() {
        return _method;
    }

    public void set_method(String _method) {
        this._method = _method;
    }

    public String getIs_daily_stock() {
        return is_daily_stock;
    }

    public void setIs_daily_stock(String is_daily_stock) {
        this.is_daily_stock = is_daily_stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public List<NewStocks> getNew_stocks() {
        return new_stocks;
    }

    public void setNew_stocks(List<NewStocks> new_stocks) {
        this.new_stocks = new_stocks;
    }

    public static class NewStocks {

        private String stock_location_id;
        private String sku;
        private String qty;

        public NewStocks(String stock_location_id, String sku, String qty) {
            this.stock_location_id = stock_location_id;
            this.sku = sku;
            this.qty = qty;
        }

        public String getStock_location_id() {
            return stock_location_id;
        }

        public void setStock_location_id(String stock_location_id) {
            this.stock_location_id = stock_location_id;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }
    }

    public String getMethod() {
        return _method;
    }

    public void setMethod(String method) {
        this._method = method;
    }
}
