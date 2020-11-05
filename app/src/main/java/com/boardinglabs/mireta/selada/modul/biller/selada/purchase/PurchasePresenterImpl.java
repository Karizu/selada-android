package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.oldresponse.ServicesResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.SPIInquiryResponse;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Dhimas on 12/14/17.
 */

public class PurchasePresenterImpl implements PurchasePresenter{
    private PurchaseView mView;
    private CommonInterface cInterface;
    private PurchaseInteractor mInteractor;

    public PurchasePresenterImpl(CommonInterface cInterface, PurchaseView view) {
        mView = view;
        this.cInterface = cInterface;
        mInteractor = new PurchaseInteractorImpl(this.cInterface.getService());
    }

    @Override
    public void getSeladaServices(String cat, String provider, String rToken, String keyGen, String serialNumber) {
        cInterface.showProgressLoading();
        mInteractor.getSeladaServices(cat, provider, rToken, keyGen, serialNumber).subscribe(new Subscriber<com.boardinglabs.mireta.selada.component.network.response.ServicesResponse>() {
            @Override
            public void onCompleted() {


            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(com.boardinglabs.mireta.selada.component.network.response.ServicesResponse servicesResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetSeladaService(servicesResponse.data);
            }
        });
    }

    @Override
    public void getListProductSelada(String provider_id) {
        cInterface.showProgressLoading();
        mInteractor.getListProductSelada(provider_id).subscribe(new Subscriber<ApiResponse<List<GSeladaProduct>>>(){

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ApiResponse<List<GSeladaProduct>> listApiResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetListProductSelada(listApiResponse.getData());
            }
        });
    }

    @Override
    public void getListProviderSelada(String category_id) {
        cInterface.showProgressLoading();
        mInteractor.getListProviderSelada(category_id).subscribe(new Subscriber<ApiResponse<List<GSeladaProvider>>>(){

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ApiResponse<List<GSeladaProvider>> listApiResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetListProviderSelada(listApiResponse.getData());
            }
        });
    }

    @Override
    public void inquiryCommand(String cmd, String customerNo, String voc, String rToken, String keyGen) {
        cInterface.showProgressLoading();
        mInteractor.inquirySPICmd(cmd, customerNo, voc, rToken, keyGen).subscribe(new Subscriber<SPIInquiryResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(SPIInquiryResponse servicesResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessSPICmdInquiry(servicesResponse.data);
            }
        });
    }

    @Override
    public void inquiryCommandResponse(String cmd, String customerNo, String voc, String rToken, String keyGen) {
        cInterface.showProgressLoading();
        mInteractor.inquirySPICmdResponse(cmd, customerNo, voc, rToken, keyGen).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ResponseBody servicesResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessSPICmdInquiryResponse(servicesResponse);
            }
        });
    }

    @Override
    public void inquiryCommandBPJS(String cmd, String customerNo, String bln, String rToken, String keyGen) {
        cInterface.showProgressLoading();
        mInteractor.inquirySPICmdBPJS(cmd, customerNo, bln, rToken, keyGen).subscribe(new Subscriber<SPIInquiryResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(SPIInquiryResponse servicesResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessSPICmdInquiry(servicesResponse.data);
            }
        });
    }

    @Override
    public void getServices(String type, String amount, String no, String cat, String rToken, String keyGen, String serialNumber) {
        cInterface.showProgressLoading();
        mInteractor.getServices(type, amount, no, cat, rToken, keyGen, serialNumber).subscribe(new Subscriber<ServicesResponse>() {
            @Override
            public void onCompleted() {


            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(ServicesResponse servicesResponse) {
                cInterface.hideProgresLoading();
                mView.onSuccessGetService(servicesResponse.services);
            }
        });
    }

    @Override
    public void setInquiry(String serviceId, String customerNo, boolean isUsing, String amount) {
        cInterface.showProgressLoading();
        if (isUsing) {
            mInteractor.setInquiry(serviceId, customerNo, amount).subscribe(new Subscriber<TransactionResponse>() {
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
                    if (transactionResponse.success) {
                        mView.onSuccessInquiry(transactionResponse.transactions);
                    } else {
                        cInterface.onFailureRequest(transactionResponse.message);
                    }

                }
            });
        } else {
            mInteractor.getTransaction(serviceId, customerNo).subscribe(new Subscriber<TransactionResponse>() {
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
                    mView.onSuccessInquiry(transactionResponse.transactions);
                }
            });
        }
    }
}
