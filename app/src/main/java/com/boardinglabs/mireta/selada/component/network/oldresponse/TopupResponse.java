package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GAccounts;
import com.boardinglabs.mireta.selada.component.network.gson.GPagination;
import com.boardinglabs.mireta.selada.component.network.gson.GTransactionTopup;
import com.boardinglabs.mireta.selada.component.network.gson.GVoucher;

import java.util.List;

/**
 * Created by Dhimas on 11/27/17.
 */

public class TopupResponse {
    public String id;
    public String agent_id;
    public String bank_id;
    public String account_id;
    public String voucher_id;
    public String amount;
    public String unique_amount;
    public String agent_name;
    public String created_at;
    public String expired_at;
    public String customer_name;
    public String notes;
    public String balance_before;
    public String balance_after;
    public String method_payment;
    public int status;
    public String created_by;
    public GVoucher voucher;
    public GAccounts account;
//    public GCostumer customer;

    public List<GTransactionTopup> items;
    public GPagination pagination;
}
