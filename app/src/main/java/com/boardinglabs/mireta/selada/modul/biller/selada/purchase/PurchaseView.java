package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

import com.boardinglabs.mireta.selada.component.network.gson.GSPICmd;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.component.network.gson.GServices;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Dhimas on 12/14/17.
 */

public interface PurchaseView {
    void onSuccessGetService(List<GServices> listServices);
    void onSuccessGetSeladaService(List<GSeladaService> listServices);
    void onSuccessSPICmdInquiry(GSPICmd spiCMD);
    void onSuccessSPICmdInquiryResponse(ResponseBody spiCMD);
    void onSuccessSPICmdInquiryBPJS(GSPICmd spiCMD);
    void onSuccessInquiry(GTransaction transaction);
    void onSuccessGetListProductSelada(List<GSeladaProduct> seladaProducts);
    void onSuccessGetListProviderSelada(List<GSeladaProvider> seladaProviders);
}
