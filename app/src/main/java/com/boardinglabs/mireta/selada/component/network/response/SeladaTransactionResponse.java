package com.boardinglabs.mireta.selada.component.network.response;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;

public class SeladaTransactionResponse {
    public Boolean success;
    public GSeladaTransaction data;
    public String message;
    public String error;
}
