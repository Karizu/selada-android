package com.boardinglabs.mireta.selada.component.network.entities.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetSeladaService implements Serializable {
    @SerializedName("category_id")
    @Expose
    private String category_id;
    @SerializedName("provider_id")
    @Expose
    private String provider_id;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }
}
