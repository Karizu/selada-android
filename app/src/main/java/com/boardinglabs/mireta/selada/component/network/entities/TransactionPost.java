package com.boardinglabs.mireta.selada.component.network.entities;


import java.util.List;

public class TransactionPost {
    public String stock_location_id;
    public String manual_transaction_code;
    public String total_discount;
    public String aggregator_id;
    public List<Items> items;
    /////////////////////////
    public String location_id;
    public String transaction_code;
    public long total_qty;
    public long total_price;
    public int payment_type;
    public int payment_method;
    public int status;
    public String stan;
    public List<Stock> details;

    public TransactionPost(String stock_location_id, String manual_transaction_code, int payment_type, int payment_method, int status, String total_discount, String aggregator_id, List<Items> items) {
        this.stock_location_id = stock_location_id;
        this.manual_transaction_code = manual_transaction_code;
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.status = status;
        this.total_discount = total_discount;
        this.aggregator_id = aggregator_id;
        this.items = items;
    }

    public TransactionPost(String location_id, String transaction_code, long total_qty, String total_price, int payment_type, int payment_method, int status, List<Stock> details) {
        this.location_id = location_id;
        this.transaction_code = transaction_code;
        this.total_qty = total_qty;
        this.total_price = Long.parseLong(total_price);
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.status = status;
        this.details = details;
    }

    public TransactionPost(String location_id, String transaction_code, long total_qty, String total_price, int payment_type, int payment_method, int status, String stan, List<Stock> details) {
        this.location_id = location_id;
        this.transaction_code = transaction_code;
        this.total_qty = total_qty;
        this.total_price = Long.parseLong(total_price);
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.status = status;
        this.stan = stan;
        this.details = details;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getTransaction_code() {
        return transaction_code;
    }

    public void setTransaction_code(String transaction_code) {
        this.transaction_code = transaction_code;
    }

    public long getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(long total_qty) {
        this.total_qty = total_qty;
    }

    public long getTotal_price() {
        return total_price;
    }

    public void setTotal_price(long total_price) {
        this.total_price = total_price;
    }

    public List<Stock> getDetails() {
        return details;
    }

    public void setDetails(List<Stock> details) {
        this.details = details;
    }

    public String getStock_location_id() {
        return stock_location_id;
    }

    public void setStock_location_id(String stock_location_id) {
        this.stock_location_id = stock_location_id;
    }

    public String getManual_transaction_code() {
        return manual_transaction_code;
    }

    public void setManual_transaction_code(String manual_transaction_code) {
        this.manual_transaction_code = manual_transaction_code;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public int getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(int payment_method) {
        this.payment_method = payment_method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getAggregator_id() {
        return aggregator_id;
    }

    public void setAggregator_id(String aggregator_id) {
        this.aggregator_id = aggregator_id;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public static class Stock {
        private String stock_id;
        private long qty;
        private String sales_price;
        private long item_discount;

        public Stock(String stock_id, long qty, String sales_price, long item_discount) {
            this.stock_id = stock_id;
            this.qty = qty;
            this.sales_price = sales_price;
            this.item_discount = item_discount;
        }

        public String getStock_id() {
            return stock_id;
        }

        public void setStock_id(String stock_id) {
            this.stock_id = stock_id;
        }

        public long getQty() {
            return qty;
        }

        public void setQty(long qty) {
            this.qty = qty;
        }

        public String getSales_price() {
            return sales_price;
        }

        public void setSales_price(String sales_price) {
            this.sales_price = sales_price;
        }

        public long getItem_discount() {
            return item_discount;
        }

        public void setItem_discount(long item_discount) {
            this.item_discount = item_discount;
        }
    }

    public static class Items {
        private String item_id;
        private long qty;
        private long item_discount;

        public Items(String item_id, long qty, long item_discount) {
            this.item_id = item_id;
            this.qty = qty;
            this.item_discount = item_discount;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public long getQty() {
            return qty;
        }

        public void setQty(long qty) {
            this.qty = qty;
        }

        public long getItem_discount() {
            return item_discount;
        }

        public void setItem_discount(long item_discount) {
            this.item_discount = item_discount;
        }
    }
}
