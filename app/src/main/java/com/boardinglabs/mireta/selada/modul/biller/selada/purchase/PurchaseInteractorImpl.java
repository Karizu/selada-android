package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.oldresponse.ServicesResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.SPIInquiryResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dhimas on 12/14/17.
 */

public class PurchaseInteractorImpl implements PurchaseInteractor {
    private NetworkService mService;
    private String bearerToken = "Bearer "+ PreferenceManager.getSessionToken();
    private String key = "dev_selada";

    public PurchaseInteractorImpl(NetworkService service) {
        mService = service;
    }

    @Override
    public Observable<com.boardinglabs.mireta.selada.component.network.response.ServicesResponse> getSeladaServices(String cat, String provider ,String rToken, String keyGen, String serialNumber) {
        return mService.getSeladaService(cat, provider, bearerToken, rToken, keyGen, Constant.KEY_VERSION).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

//    @Override
//    public Observable<com.boardinglabs.mireta.selada.component.network.response.ServicesResponse> getSeladaServices(String cat, String provider ,String rToken, String keyGen, String serialNumber) {
//        return mService.getSeladaService(Utils.encodeJson("getSeladaServices", keyGen, serialNumber, cat, provider, "", "", "", ""), bearerToken, rToken, keyGen).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

    @Override
    public Observable<SPIInquiryResponse> inquirySPICmd(String cmd, String nop, String voc, String rToken, String keyGen) {
        return mService.spiCMDInquiry(cmd, nop, voc, cmd, nop, voc, bearerToken, rToken, keyGen, key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ResponseBody> inquirySPICmdResponse(String cmd, String nop, String voc, String rToken, String keyGen) {
        return mService.spiCMDInquiryResponse(cmd, nop, voc, cmd, nop, voc, bearerToken, rToken, keyGen, key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

//    @Override
//    public Observable<SPIInquiryResponse> inquirySPICmd(String cmd, String nop, String voc, String rToken, String keyGen) {
//        return mService.spiCMDInquiry(Utils.encodeJson("inquirySPICmd", keyGen, cmd, nop, voc, "", "", ""), bearerToken, rToken, keyGen).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

    @Override
    public Observable<SPIInquiryResponse> inquirySPICmdBPJS(String cmd, String nop, String bln, String rToken, String keyGen) {
        return mService.spiCMDInquiryBPJS(cmd, nop, bln, cmd, nop, bln, bearerToken, rToken, keyGen, key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

//    @Override
//    public Observable<SPIInquiryResponse> inquirySPICmdBPJS(String cmd, String nop, String bln, String rToken, String keyGen) {
//        return mService.spiCMDInquiryBPJS(Utils.encodeJson("inquirySPICmdBPJS", keyGen, cmd, nop, bln, "", "", ""), bearerToken, rToken, keyGen).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

    @Override
    public Observable<ServicesResponse> getServices(String type, String amount, String no, String cat, String rToken, String keyGen, String serialNumber) {
        return mService.getService(type, amount, no, cat, bearerToken, rToken, keyGen, Constant.KEY_VERSION).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

//    @Override
//    public Observable<ServicesResponse> getServices(String type, String amount, String no, String cat, String rToken, String keyGen, String serialNumber) {
//        return mService.getService(Utils.encodeJson("getService", keyGen, serialNumber, type, amount, no, cat, "", ""), bearerToken, rToken, keyGen).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

    @Override
    public Observable<TransactionResponse> setInquiry(String serviceId, String customerNo, String amount) {
        return mService.setInquiry(customerNo, serviceId, amount, bearerToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<TransactionResponse> getTransaction(String serviceId, String customerNo) {
        return mService.getTransactionWithoutInquiry(customerNo, serviceId, bearerToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ApiResponse<List<GSeladaProduct>>> getListProductSelada(String provider_id) {
        return mService.getListProductSelada(provider_id, bearerToken, key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ApiResponse<List<GSeladaProvider>>> getListProviderSelada(String category_id) {
        return mService.getListProviderSelada(category_id, bearerToken, key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
