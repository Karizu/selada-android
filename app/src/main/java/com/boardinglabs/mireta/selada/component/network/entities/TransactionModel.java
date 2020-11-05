package com.boardinglabs.mireta.selada.component.network.entities;

public class TransactionModel {

    private String id;
    private String business_id;
    private String tenant_id;
    private String order_no;
    private String order_date;
    private String payment_type;
    private String payment_method;
    private String amount;
    private String discount;
    private String status;

    public TransactionModel(String id, String business_id, String tenant_id, String order_no, String order_date, String payment_type, String payment_method, String amount, String discount, String status) {
        this.id = id;
        this.business_id = business_id;
        this.tenant_id = tenant_id;
        this.order_no = order_no;
        this.order_date = order_date;
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.amount = amount;
        this.discount = discount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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
}
