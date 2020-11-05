package com.boardinglabs.mireta.selada.modul.home;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.TransactionListResponse;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import java.util.List;

import rx.Subscriber;

public class HomePresenter {
    private CommonInterface cInterface;
    private HomeView mView;
    private HomeInteractor mInteractor;

    public HomePresenter(CommonInterface cInterface, HomeView view) {
        mView = view;
        this.cInterface = cInterface;
        mInteractor = new HomeInteractor(this.cInterface.getService());
    }

    public void lastTransactions(String stockLocationId) {
        cInterface.showProgressLoading();

        mInteractor.todayTransactions(stockLocationId).subscribe(new Subscriber<TransactionListResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(TransactionListResponse homeResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetLatestTransactions(homeResponse.data);
            }
        });
    }

    public void lastTransactionsNow() {
        cInterface.showProgressLoading();

        mInteractor.getLastTransactionNow().subscribe(new Subscriber<List<TransactionResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(List<TransactionResponse> transactionResponses) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetLatestTransactionsNow(transactionResponses);
            }

        });
    }
}
