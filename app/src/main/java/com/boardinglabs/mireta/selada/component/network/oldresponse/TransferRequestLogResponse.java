package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GCostumer;
import com.boardinglabs.mireta.selada.component.network.gson.GTransferRequestLog;

import java.util.List;

public class TransferRequestLogResponse {

    public boolean success;
    public String message;
    public List<GTransferRequestLog> logs;
    public GCostumer customer;
    public GCostumer to_customer;
}
