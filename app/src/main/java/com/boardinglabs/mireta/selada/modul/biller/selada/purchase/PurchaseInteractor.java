package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.oldresponse.ServicesResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.SPIInquiryResponse;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;

/**
 * Created by Dhimas on 12/14/17.
 */

public interface PurchaseInteractor {
    Observable<ServicesResponse> getServices(String type, String amount, String no, String cat, String rToken, String keyGen, String serialNumber);

    Observable<com.boardinglabs.mireta.selada.component.network.response.ServicesResponse> getSeladaServices(String cat, String provider, String rToken, String keyGen, String serialNumber);

    Observable<SPIInquiryResponse> inquirySPICmd(String cmd, String nop, String voc, String rToken, String keyGen);

    Observable<ResponseBody> inquirySPICmdResponse(String cmd, String nop, String voc, String rToken, String keyGen);

    Observable<SPIInquiryResponse> inquirySPICmdBPJS(String cmd, String nop, String bln, String rToken, String keyGen);

    Observable<TransactionResponse> setInquiry(String serviceId, String customerNo, String amount);

    Observable<TransactionResponse> getTransaction(String serviceId, String customerNo);

    Observable<ApiResponse<List<GSeladaProduct>>> getListProductSelada(String provider_id);

    Observable<ApiResponse<List<GSeladaProvider>>> getListProviderSelada(String category_id);
}
