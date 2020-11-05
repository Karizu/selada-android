package com.boardinglabs.mireta.selada.component.network.entities.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SpiCMDInquiryBPJS implements Serializable {
    @SerializedName("cmd")
    @Expose
    private String cmd;
    @SerializedName("nop")
    @Expose
    private String nop;
    @SerializedName("bln")
    @Expose
    private String bln;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getNop() {
        return nop;
    }

    public void setNop(String nop) {
        this.nop = nop;
    }

    public String getBln() {
        return bln;
    }

    public void setBln(String bln) {
        this.bln = bln;
    }
}
