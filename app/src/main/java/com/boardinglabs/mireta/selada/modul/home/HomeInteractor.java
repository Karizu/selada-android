package com.boardinglabs.mireta.selada.modul.home;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.TransactionListResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeInteractor {
    private NetworkService mService;

    public HomeInteractor(NetworkService service) {
        mService = service;

    }

    public Observable<TransactionListResponse> todayTransactions(String stockLocationId) {
        return mService.getTransactions(stockLocationId, false, "Bearer "+ PreferenceManager.getSessionToken()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<List<TransactionResponse>> getLastTransactionNow() {
        return mService.getLastTransactionNow("1", "03023de0-96d7-41e6-a729-93cb9d3c8d34", "Bearer "+ PreferenceManager.getSessionToken()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
