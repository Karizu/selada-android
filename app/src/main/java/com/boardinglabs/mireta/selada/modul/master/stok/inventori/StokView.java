package com.boardinglabs.mireta.selada.modul.master.stok.inventori;

import com.boardinglabs.mireta.selada.component.network.entities.Item;

import java.util.List;

import okhttp3.ResponseBody;

public interface StokView {
    void onSuccessGetItems(List<Item> transactionItems);
    void onSuccessCreateTransaction(ResponseBody responseBody);
}
