
package com.boardinglabs.mireta.selada.component.network.entities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GMerchant implements Serializable
{

    @SerializedName("merchant")
    @Expose
    private Merchant merchant;
    private final static long serialVersionUID = 6103649505557283939L;

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

}
