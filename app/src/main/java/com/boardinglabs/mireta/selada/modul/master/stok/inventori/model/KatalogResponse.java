package com.boardinglabs.mireta.selada.modul.master.stok.inventori.model;

import java.util.List;

public class KatalogResponse {

    private String id;
    private List<String> item_images;
    private String name;
    private String description;
    private String price;
    private String is_daily_stock;
    private Total_Qty total_qty;
    private TotalTodaQty total_today_qty;

    public class Total_Qty {
        private String item_id;
        private String total_qty;

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

    public class TotalTodaQty {
        private String item_id;
        private String total_daily_qty;

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getTotal_daily_qty() {
            return total_daily_qty;
        }

        public void setTotal_daily_qty(String total_daily_qty) {
            this.total_daily_qty = total_daily_qty;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getItem_images() {
        return item_images;
    }

    public void setItem_images(List<String> item_images) {
        this.item_images = item_images;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Total_Qty getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(Total_Qty total_qty) {
        this.total_qty = total_qty;
    }

    public String getIs_daily_stock() {
        return is_daily_stock;
    }

    public void setIs_daily_stock(String is_daily_stock) {
        this.is_daily_stock = is_daily_stock;
    }

    public TotalTodaQty getTotal_today_qty() {
        return total_today_qty;
    }

    public void setTotal_today_qty(TotalTodaQty total_today_qty) {
        this.total_today_qty = total_today_qty;
    }
}


