package com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview;

import android.util.Log;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dhimas on 12/8/17.
 */

public class TransactionReviewInteractorImpl implements TransactionReviewInteractor {
    private NetworkService mService;
    private String bearerToken = "Bearer "+ PreferenceManager.getSessionToken();
    private String key = "dev-selada";

    public TransactionReviewInteractorImpl(NetworkService mService) {
        this.mService = mService;
    }

    @Override
    public Observable<MessageResponse> subscribePremium(String refferalId) {
        return mService.subscribePremium(refferalId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<TransactionResponse> payTransaction(String transactionId) {
        return mService.payTransaction(transactionId, bearerToken).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<SeladaTransactionResponse> paySeladaTransaction(String serviceId, String merchantId, String merchantNo, String totalPrice, String billerPrice, String stan) {
        return mService.createSeladaTransaction(serviceId, merchantId, merchantNo,totalPrice, billerPrice, "", bearerToken, key).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }
}
