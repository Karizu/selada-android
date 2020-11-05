package com.boardinglabs.mireta.selada.component.network.gson;

public class GFeedChatPreview {
    public String id;
    public String name;
    public String message;
    public String time;
    public String avatarUrl;

    public GFeedChatPreview(String name, String message){
        this.name = name;
        this.message = message;
    }
}
