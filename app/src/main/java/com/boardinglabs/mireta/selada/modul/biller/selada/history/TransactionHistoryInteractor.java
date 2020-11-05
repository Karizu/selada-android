package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionListResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;

import rx.Observable;

/**
 * Created by Dhimas on 12/8/17.
 */

public interface TransactionHistoryInteractor {
    Observable<SeladaTransactionResponse> detailTransaction(String transactionId);
    Observable<SeladaTransactionListResponse> transactionHistory(String merchantId);

}
