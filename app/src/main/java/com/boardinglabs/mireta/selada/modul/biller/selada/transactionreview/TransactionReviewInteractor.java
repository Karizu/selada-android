package com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview;

import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;

import rx.Observable;

/**
 * Created by Dhimas on 12/8/17.
 */

public interface TransactionReviewInteractor {
    Observable<MessageResponse> subscribePremium(String refferalId);

    Observable<TransactionResponse> payTransaction(String transactionId);

    Observable<SeladaTransactionResponse> paySeladaTransaction(String serviceId, String merchantId, String merchantNo, String totalPrice, String billerPrice, String stan);

}
