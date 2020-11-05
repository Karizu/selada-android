package com.boardinglabs.mireta.selada.component.network.response;

import com.boardinglabs.mireta.selada.component.network.entities.Item;

import java.util.List;

public class ItemsResponse extends BaseResponse {
    public List<Item> data;
    public int total_row;
}
