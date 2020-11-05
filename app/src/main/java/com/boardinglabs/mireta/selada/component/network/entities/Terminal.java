
package com.boardinglabs.mireta.selada.component.network.entities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Terminal implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("merchant_address")
    @Expose
    private String merchantAddress;
    @SerializedName("merchant_account_number")
    @Expose
    private String merchantAccountNumber;
    @SerializedName("serial_number")
    @Expose
    private String serialNumber;
    @SerializedName("sid")
    @Expose
    private String sid;
    @SerializedName("iccid")
    @Expose
    private String iccid;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("tid")
    @Expose
    private String tid;

    private final static long serialVersionUID = -1644480839746058200L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getMerchantAccountNumber() {
        return merchantAccountNumber;
    }

    public void setMerchantAccountNumber(String merchantAccountNumber) {
        this.merchantAccountNumber = merchantAccountNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
