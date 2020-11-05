package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;

import java.util.List;

/**
 * Created by Dhimas on 12/7/17.
 */

public interface TransactionHistoryView {

    void onSuccessGetDetailTransaction(GSeladaTransaction transaction);
    void onSuccessGetHistoryTransaction(List<GSeladaTransaction> transaction);

}
