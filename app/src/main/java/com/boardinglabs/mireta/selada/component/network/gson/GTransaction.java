package com.boardinglabs.mireta.selada.component.network.gson;

import java.util.List;

/**
 * Created by Dhimas on 12/18/17.
 */

public class GTransaction {
    public String id;
    public String agent_id;
    public String service_id;
    public String vendor_id;
    public String customer_no;
    public String amount;
    public String unique_amount;
    public String vendor_price;
    public String default_price;
    public String omzet;
    public String cashback_agent;
    public String status;
    public String account_id;
    public String image_id;
    public String notes;
    public String data;
    public String merchant_name;
    public String created_at;
    public String status_label;
    public String amount_charged;
    public String amount_transaction;
    public String voucher_id;
    public String balance_after;
    public String balance_before;

    public List<GLogo> images;
    public String customer_username;
    public String customer_name;
    public GServices service;
    public GAgent agent;
    public GVendor vendor;
    public GTransactionTopup topup;

    //Handle Transfer or Request
    public String sub_costumer_id;
    public GAgent customer;
    public GAgent sub_customer;


    //Handle Bank Information
    public String bank_id;



}
