package com.boardinglabs.mireta.selada.component.network.gson;

import java.util.List;

public class GSeladaTransaction {
    public int id;
    public String service_id;
    public String code;
    public String merchant_id;
    public String merchant_no;
    public String price;
    public String vendor_price;
    public String note;
    public String stan;
    public int status;
    public int payment_status;
    public String created_at;
    public String updated_at;
    public String deleted_at;
    public GSeladaService service;
    public GMerchant merchant;
}
