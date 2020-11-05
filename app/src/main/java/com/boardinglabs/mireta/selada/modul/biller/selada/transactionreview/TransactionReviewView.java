package com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;

/**
 * Created by Dhimas on 12/7/17.
 */

public interface TransactionReviewView {
    void onSuccessGetBalance(String balance);

    void onSuccessRegisterPremium(MessageResponse mResponse);

    void onSuccessPayTransaction(GTransaction transaction);

    void onSuccessSeladaPayTransaction(GSeladaTransaction transaction);

    void onSuccessCheckReferral(String refferalId);

    void chargeAmount(String totalAmount, String fee);

    void charge(QRTransactionResponse json);
}
