package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GTransferRequestLogGroup;

import java.util.List;

public class TransferRequestLogGroupResponse {

    public boolean success;
    public String message;
    public List<GTransferRequestLogGroup> log_groups;
}
