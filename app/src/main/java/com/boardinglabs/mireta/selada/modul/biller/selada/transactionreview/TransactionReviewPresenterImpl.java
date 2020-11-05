package com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.gson.GBalance;
import com.boardinglabs.mireta.selada.component.network.oldresponse.AgentResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.CalculateResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dhimas on 12/8/17.
 */

public class TransactionReviewPresenterImpl implements TransactionReviewPresenter {
    CommonInterface cInterface;
    TransactionReviewView mView;
    TransactionReviewInteractor mInteractor;

    public TransactionReviewPresenterImpl(CommonInterface cInterface, TransactionReviewView view) {
        this.cInterface = cInterface;
        mView = view;
        mInteractor = new TransactionReviewInteractorImpl(this.cInterface.getService());
    }

    @Override
    public void onRegisterPremium(String refferalId) {
        cInterface.showProgressLoading();
        mInteractor.subscribePremium(refferalId).subscribe(new Subscriber<MessageResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(MessageResponse messageResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessRegisterPremium(messageResponse);
            }
        });
    }

    @Override
    public void getBalance() {
        cInterface.showProgressLoading();

    }

    @Override
    public void payTransaction(String transactionId) {
        cInterface.showProgressLoading();
        mInteractor.payTransaction(transactionId).subscribe(new Subscriber<TransactionResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(TransactionResponse transactionResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessPayTransaction(transactionResponse.transactions);
            }
        });
    }

    @Override
    public void checkRefferal(String code) {
        cInterface.showProgressLoading();

    }

    @Override
    public void calculate(String amount, String type, String merchantId) {
        cInterface.showProgressLoading();
        calculateAmount(amount, type, merchantId).subscribe(new Subscriber<CalculateResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(CalculateResponse calculateResponse) {
                cInterface.hideProgresLoading();
                int fee = Integer.parseInt(calculateResponse.charge) - Integer.parseInt(calculateResponse.amount);
                mView.chargeAmount(calculateResponse.charge, fee+"");
            }
        });
    }

    @Override
    public void chargeTransaction(String transactionId) {
        cInterface.showProgressLoading();
        charge(transactionId).subscribe(new Subscriber<QRTransactionResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(QRTransactionResponse jsonObject) {
                cInterface.hideProgresLoading();
                mView.charge(jsonObject);
            }
        });
    }

    @Override
    public void paySeladaTransaction(String serviceId, String merchantId, String merchantNo, String totalPrice, String billerPrice, String stan) {
        cInterface.showProgressLoading();
        mInteractor.paySeladaTransaction(serviceId, merchantId, merchantNo, totalPrice, billerPrice, stan).subscribe(new Subscriber<SeladaTransactionResponse>() {
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
                mView.onSuccessSeladaPayTransaction(transactionResponse.data);
            }
        });
    }

    Observable<CalculateResponse> calculateAmount(String amount, String type, String merchantId) {
        return cInterface.getService().calculateAmount(amount, type, merchantId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    Observable<QRTransactionResponse> charge(String transactionId) {
        return null;
    }
}
