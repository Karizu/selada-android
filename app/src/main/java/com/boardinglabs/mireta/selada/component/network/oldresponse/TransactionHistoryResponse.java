package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GPagination;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;

import java.util.List;

/**
 * Created by Dhimas on 12/21/17.
 */

public class TransactionHistoryResponse {
    public List<GTransaction> items;
    public GPagination pagination;
}
