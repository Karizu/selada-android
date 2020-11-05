package com.boardinglabs.mireta.selada.component.network.entities;

import java.util.List;

public class TransactionResponse {

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
    private TransactionHeader transaction_header;
    private List<TransactionDetail> transaction_detail;

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

    public TransactionHeader getTransaction_header() {
        return transaction_header;
    }

    public void setTransaction_header(TransactionHeader transaction_header) {
        this.transaction_header = transaction_header;
    }

    public List<TransactionDetail> getTransaction_detail() {
        return transaction_detail;
    }

    public void setTransaction_detail(List<TransactionDetail> transaction_detail) {
        this.transaction_detail = transaction_detail;
    }
}
