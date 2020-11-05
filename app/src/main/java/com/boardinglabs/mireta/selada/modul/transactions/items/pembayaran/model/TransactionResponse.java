
package com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionResponse implements Serializable
{

    @SerializedName("business_id")
    @Expose
    private String businessId;
    @SerializedName("tenant_id")
    @Expose
    private String tenantId;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    private final static long serialVersionUID = 1417484011624215503L;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
