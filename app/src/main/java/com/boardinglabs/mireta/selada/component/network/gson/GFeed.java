package com.boardinglabs.mireta.selada.component.network.gson;

public class GFeed {
    public String id;
    public String name;
    public String username;
    public String content;
    public String date;
    public String avatarUrl;

    public GFeed(String name, String username, String content){
        this.name = name;
        this.username = username;
        this.content =  content;
    }
}
