package com.boardinglabs.mireta.selada.modul.master.laporan.model;

public class LaporanModel {

    private String id;
    private String name;
    private String description;
    private String business_id;
    private String brand_id;
    private String price;
    private String category_id;
    private String total_today_qty;
    private long qtyOut;

    public LaporanModel(String name, String total_today_qty, long qtyOut) {
        this.name = name;
        this.total_today_qty = total_today_qty;
        this.qtyOut = qtyOut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getQtyOut() {
        return qtyOut;
    }

    public void setQtyOut(long qtyOut) {
        this.qtyOut = qtyOut;
    }

    public String getTotal_today_qty() {
        return total_today_qty;
    }

    public void setTotal_today_qty(String total_today_qty) {
        this.total_today_qty = total_today_qty;
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

    public class TotalQty {
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
}
