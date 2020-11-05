package com.boardinglabs.mireta.selada.component.network.gson;

/**
 * Created by Dhimas on 12/25/17.
 */

public class GTransactionTopup {
    public String id;
    public String agent_id;
    public String sub_agent_id;
    public String sub_customer_id;
    public String bank_id;
    public String account_id;
    public String voucher_id;
    public String amount;
    public String amount_charged;
    public String unique_amount;
    public String agent_name;
    public String notes;
    public String balance_before;
    public String balance_after;
    public String method_payment;
    public String expired_at;
    public String status;
    public String created_by;
    public String created_at;
    public GAgent customer;
    public GAgent sub_agent;
    public GAgent sub_customer;
    public GAccounts account;
    public GBanks bank;
    public String status_label;
    public GPayment payment;
}
