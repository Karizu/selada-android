package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;

/**
 * Created by Dhimas on 2/1/18.
 */

public class QRTransactionResponse {
    public boolean success;
    public QRResponse item;
    public String message;
    public GTransaction transaction;
}
