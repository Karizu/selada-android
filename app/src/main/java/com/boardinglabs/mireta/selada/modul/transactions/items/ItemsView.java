package com.boardinglabs.mireta.selada.modul.transactions.items;

import com.boardinglabs.mireta.selada.component.network.entities.Item;

import java.util.List;

import okhttp3.ResponseBody;

public interface ItemsView {
    void onSuccessGetItems(List<Item> transactionItems);
    void onSuccessCreateTransaction(ResponseBody responseBody);
}
