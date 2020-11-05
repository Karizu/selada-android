package com.boardinglabs.mireta.selada.modul.master.stok.inventori;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.response.ItemsResponse;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import okhttp3.ResponseBody;
import rx.Subscriber;

public class StokPresenter {
    private CommonInterface cInterface;
    private StokView mView;
    private StokInteractor mInteractor;

    public StokPresenter(CommonInterface cInterface, StokView view) {
        mView = view;
        this.cInterface = cInterface;
        mInteractor = new StokInteractor(this.cInterface.getService());
    }

    public void stockItems(String businessId) {
        cInterface.showProgressLoading();

        mInteractor.getStockItems(businessId).subscribe(new Subscriber<ItemsResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ItemsResponse itemsResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetItems(itemsResponse.data);
            }
        });
    }

    public void createTransaction(TransactionPost transactionPost, String token) {
        cInterface.showProgressLoading();

        mInteractor.createTransaction(transactionPost, token).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                cInterface.hideProgresLoading();
                mView.onSuccessCreateTransaction(responseBody);
            }
        });
    }
}
