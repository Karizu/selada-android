package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

/**
 * Created by Dhimas on 12/14/17.
 */

public interface PurchasePresenter {
    void getServices(String type, String amount, String no, String cat, String rToken, String keyGen, String serialNumber);

    void setInquiry(String serviceId, String customerNo, boolean usingInquiry, String amount);

    void inquiryCommand(String cmd, String customerNo, String voc, String rToken, String keyGen);

    void inquiryCommandResponse(String cmd, String customerNo, String voc, String rToken, String keyGen);

    void inquiryCommandBPJS(String cmd, String customerNo, String bln, String rToken, String keyGen);

    void getSeladaServices(String cat, String provider, String rToken, String keyGen, String serialNumber);

    void getListProductSelada(String provider_id);

    void getListProviderSelada(String category_id);

}
