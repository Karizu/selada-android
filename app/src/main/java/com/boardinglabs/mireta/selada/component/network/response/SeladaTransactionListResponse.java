package com.boardinglabs.mireta.selada.component.network.response;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;

import java.util.List;

public class SeladaTransactionListResponse {
    public Boolean success;
    public List<GSeladaTransaction> data;
    public String message;
}
