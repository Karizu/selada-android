package com.boardinglabs.mireta.selada.component.network.gson;

public class GFeedTrx {
    public String id;
    public String icon;
    public String name;
    public String status;
    public String keterangan;
    public String nominal;
    public String datetime;

    public GFeedTrx(String name){
        this.name = name;
    }
}
