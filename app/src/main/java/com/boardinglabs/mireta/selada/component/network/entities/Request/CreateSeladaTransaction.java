package com.boardinglabs.mireta.selada.component.network.entities.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateSeladaTransaction implements Serializable {
    @SerializedName("service_id")
    @Expose
    private String service_id;
    @SerializedName("merchant_id")
    @Expose
    private String merchant_id;
    @SerializedName("merchant_no")
    @Expose
    private String merchant_no;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("vendor_price")
    @Expose
    private String vendor_price;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("stan")
    @Expose
    private String stan;

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_no() {
        return merchant_no;
    }

    public void setMerchant_no(String merchant_no) {
        this.merchant_no = merchant_no;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVendor_price() {
        return vendor_price;
    }

    public void setVendor_price(String vendor_price) {
        this.vendor_price = vendor_price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }
}
