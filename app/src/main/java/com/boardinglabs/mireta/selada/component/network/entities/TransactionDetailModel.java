package com.boardinglabs.mireta.selada.component.network.entities;

public class TransactionDetailModel {

    private String id;
    private String transaction_id;
    private String item_stock_id;
    private String item_name;
    private String quantity;
    private String price;
    private String discount;

    public TransactionDetailModel(String item_name, String quantity, String price, String discount) {
        this.item_name = item_name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
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
