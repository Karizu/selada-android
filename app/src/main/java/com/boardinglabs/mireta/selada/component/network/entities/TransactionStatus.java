package com.boardinglabs.mireta.selada.component.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionStatus implements Serializable
{

    @SerializedName("cmd")
    @Expose
    private String cmd;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("tgl")
    @Expose
    private String tgl;
    @SerializedName("nop")
    @Expose
    private String nop;
    @SerializedName("voc")
    @Expose
    private String voc;
    @SerializedName("hrg")
    @Expose
    private String hrg;
    @SerializedName("vsn")
    @Expose
    private String vsn;
    @SerializedName("sts")
    @Expose
    private Integer sts;
    @SerializedName("msg")
    @Expose
    private String msg;

    private final static long serialVersionUID = 915585455692787538L;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getNop() {
        return nop;
    }

    public void setNop(String nop) {
        this.nop = nop;
    }

    public String getVoc() {
        return voc;
    }

    public void setVoc(String voc) {
        this.voc = voc;
    }

    public String getHrg() {
        return hrg;
    }

    public void setHrg(String hrg) {
        this.hrg = hrg;
    }

    public String getVsn() {
        return vsn;
    }

    public void setVsn(String vsn) {
        this.vsn = vsn;
    }

    public Integer getSts() {
        return sts;
    }

    public void setSts(Integer sts) {
        this.sts = sts;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}