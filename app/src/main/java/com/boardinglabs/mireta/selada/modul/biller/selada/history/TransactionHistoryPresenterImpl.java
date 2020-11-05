package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.gson.GBalance;
import com.boardinglabs.mireta.selada.component.network.oldresponse.AgentResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.CalculateResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionListResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dhimas on 12/8/17.
 */

public class TransactionHistoryPresenterImpl implements TransactionHistoryPresenter {
    CommonInterface cInterface;
    TransactionHistoryView mView;
    TransactionHistoryInteractor mInteractor;


    public TransactionHistoryPresenterImpl(CommonInterface cInterface, TransactionHistoryView view) {
        this.cInterface = cInterface;
        mView = view;
        mInteractor = new TransactionHistoryInteractorImpl(this.cInterface.getService());
    }

    @Override
    public void detailTransaction(String transactionId) {
        cInterface.showProgressLoading();
        mInteractor.detailTransaction(transactionId).subscribe(new Subscriber<SeladaTransactionResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(SeladaTransactionResponse transactionResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetDetailTransaction(transactionResponse.data);
            }
        });
    }

    @Override
    public void transactionHistory(String merchantId) {
        cInterface.showProgressLoading();
        mInteractor.transactionHistory(merchantId).subscribe(new Subscriber<SeladaTransactionListResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(SeladaTransactionListResponse transactionResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetHistoryTransaction(transactionResponse.data);
            }
        });
    }

    //    @Override
//    public void payTransaction(String transactionId) {
//        cInterface.showProgressLoading();
//        mInteractor.payTransaction(transactionId).subscribe(new Subscriber<TransactionResponse>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                cInterface.hideProgresLoading();
//                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
//            }
//
//            @Override
//            public void onNext(TransactionResponse transactionResponse) {
//                cInterface.hideProgresLoading();
//                mView.onSuccessPayTransaction(transactionResponse.transactions);
//            }
//        });
//    }

}
