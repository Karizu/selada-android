package com.boardinglabs.mireta.selada.component.network.gson;

import java.util.List;

/**
 * Created by Dhimas on 11/27/17.
 */

public class GPost {
    public String id;
    public String customer_id;
    public String text;
    public String accounts;
    public String comments_count;
    public String likes_count;
    public GCostumer customer;
    public List<GPostComment> comments;
    public List<GCostumer> user_likes;
    public boolean is_like;
    public String created_at;
}
