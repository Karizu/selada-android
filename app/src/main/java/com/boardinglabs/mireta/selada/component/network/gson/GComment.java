package com.boardinglabs.mireta.selada.component.network.gson;

public class GComment {
    public String id;
    public String name;
    public String username;
    public String content;
    public String date;
    public String avatarUrl;

    public GComment(String name, String username, String content){
        this.name = name;
        this.username = username;
        this.content =  content;
    }
    public GComment(String id, String name, String username, String content){
        this.name = id;
        this.name = name;
        this.username = username;
        this.content =  content;
    }
}
