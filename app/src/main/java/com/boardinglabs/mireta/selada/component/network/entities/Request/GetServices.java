package com.boardinglabs.mireta.selada.component.network.entities.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetServices implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("customer_no")
    @Expose
    private String no;
    @SerializedName("category")
    @Expose
    private String cat;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
}
