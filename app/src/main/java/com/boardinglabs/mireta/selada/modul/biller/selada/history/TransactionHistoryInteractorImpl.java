package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionListResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dhimas on 12/8/17.
 */

public class TransactionHistoryInteractorImpl implements TransactionHistoryInteractor {
    private NetworkService mService;
    private String bearerToken = "Bearer "+ PreferenceManager.getSessionToken();

    public TransactionHistoryInteractorImpl(NetworkService mService) {
        this.mService = mService;
    }

    @Override
    public Observable<SeladaTransactionResponse> detailTransaction(String transactionId) {
        return mService.getSeladaTransactionDetail(transactionId, bearerToken, Constant.KEY_CODE).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<SeladaTransactionListResponse> transactionHistory(String merchantId) {
        return mService.getSeladaTransactionHistory(merchantId, bearerToken, Constant.KEY_CODE).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }

    //    @Override
//    public Observable<TransactionResponse> det(String transactionId) {
//        return mService.payTransaction(transactionId).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io());
//    }

}
