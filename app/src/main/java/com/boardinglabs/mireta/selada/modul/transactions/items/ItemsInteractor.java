package com.boardinglabs.mireta.selada.modul.transactions.items;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.response.ItemsResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ItemsInteractor {
    private NetworkService mService;

    public ItemsInteractor(NetworkService service) {
        mService = service;

    }

    public Observable<ItemsResponse> getStockItems(String businessId) {
        return mService.getStockItems(businessId, "Bearer "+ PreferenceManager.getSessionTokenMireta()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<ResponseBody> createTransaction(TransactionPost transactionPost, String token) {
        return mService.createTransaction(transactionPost, token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
