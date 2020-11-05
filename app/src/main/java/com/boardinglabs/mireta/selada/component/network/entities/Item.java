package com.boardinglabs.mireta.selada.component.network.entities;

import com.boardinglabs.mireta.selada.component.network.entities.Stocks.Location;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.KatalogResponse;

import java.util.List;

public class Item extends BaseEntity {
    public String sku;
    public String qty;
    public Location location;
    public com.boardinglabs.mireta.selada.component.network.entities.Stocks.Item item;
    public String name;
    public String description;
    public String brand_id;
    public String category_id;
    public String business_id;
    public String price;
    public boolean is_daily_stock;
    public long order_qty;
    public List<String> item_images;
    public Brand brand;
    public Business business;
    public TotalQty total_qty;
    public List<ItemStock> stocks;
    public KatalogResponse.TotalTodaQty total_today_qty;

    public class TotalQty{
        public String item_id;
        public String total_qty;

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getTotal_qty() {
            return total_qty;
        }

        public void setTotal_qty(String total_qty) {
            this.total_qty = total_qty;
        }
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

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isIs_daily_stock() {
        return is_daily_stock;
    }

    public void setIs_daily_stock(boolean is_daily_stock) {
        this.is_daily_stock = is_daily_stock;
    }

    public long getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(long order_qty) {
        this.order_qty = order_qty;
    }

    public List<String> getItem_images() {
        return item_images;
    }

    public void setItem_images(List<String> item_images) {
        this.item_images = item_images;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public TotalQty getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(TotalQty total_qty) {
        this.total_qty = total_qty;
    }

    public List<ItemStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<ItemStock> stocks) {
        this.stocks = stocks;
    }

    public KatalogResponse.TotalTodaQty getTotal_today_qty() {
        return total_today_qty;
    }

    public void setTotal_today_qty(KatalogResponse.TotalTodaQty total_today_qty) {
        this.total_today_qty = total_today_qty;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public com.boardinglabs.mireta.selada.component.network.entities.Stocks.Item getItem() {
        return item;
    }

    public void setItem(com.boardinglabs.mireta.selada.component.network.entities.Stocks.Item item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
