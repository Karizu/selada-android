package com.boardinglabs.mireta.selada.component.network.entities;

import java.util.List;

public class TransactionToCashier {
    private String business_id;
    private String tenant_id;
    private String order_no;
    private String order_date;
    private String payment_type;
    private String payment_method;
    private long amount;
    private String discount;
    private String status;
    private List<Items> items;

    public TransactionToCashier(String business_id, String tenant_id, String order_no, String order_date, String payment_type, String payment_method, long amount, String discount, String status, List<Items> items) {
        this.business_id = business_id;
        this.tenant_id = tenant_id;
        this.order_no = order_no;
        this.order_date = order_date;
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.amount = amount;
        this.discount = discount;
        this.status = status;
        this.items = items;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public static class Items {
        private String item_stock_id;
        private String item_name;
        private long quantity;
        private String price;
        private String discount;

        public Items(String item_stock_id, String item_name, long quantity, String price, String discount) {
            this.item_stock_id = item_stock_id;
            this.item_name = item_name;
            this.quantity = quantity;
            this.price = price;
            this.discount = discount;
        }

        public String getItem_stock_id() {
            return item_stock_id;
        }

        public void setItem_stock_id(String item_stock_id) {
            this.item_stock_id = item_stock_id;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }
    }

}
