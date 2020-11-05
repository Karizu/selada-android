package com.boardinglabs.mireta.selada.component.network.oldresponse;

import android.text.TextUtils;

import org.parceler.Parcel;

/**
 * Created by Dhimas on 11/28/17.
 */

@Parcel
public class TransactionTopupResponse {
    public String topupSaldo;
    public String orderId;
    public String bankName;
    public String bankAccount;
    public String bankLogo;
    public String bankId;
    public String accountId;
    public String time;
    public String date;
    public String notes;
    public boolean isSuccess;
    public String info;
    public String expiredAt;
    public String createAt;
    public boolean isTopupTransaction;
    public boolean isFromHome;
    public String status;
    public boolean isFail;
    public int jenisTransaksi;
    public String statusTrx;

    public String customer_name;
    public String customer_no;
    public String customer_avatar;
    public String sub_customer_name;
    public String sub_customer_no;
    public String sub_customer_avatar;
    public String balance_before;
    public String balance_after;

    public String merchant_name;

    public String service_category;
    public String service_name;

    public String getNotes() {
        if (TextUtils.isEmpty(notes)) {
            return "";
        }
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getStatusTrx() {
        if (statusTrx == null) {
            return "";
        }
        return statusTrx;
    }

    public void setStatusTrx(String status) {
        this.statusTrx = status;
    }

    public String getStatus() {
        if (status == null) {
            return "";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
    }

    public boolean isFromHome() {
        return isFromHome;
    }

    public void setFromHome(boolean fromHome) {
        isFromHome = fromHome;
    }

    public boolean isTopupTransaction() {
        return isTopupTransaction;
    }

    public void setTopupTransaction(boolean topupTransaction) {
        isTopupTransaction = topupTransaction;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTopupSaldo() {
        if (topupSaldo == null) {
            return "0";
        }
        return topupSaldo;
    }

    public void setTopupSaldo(String topupSaldo) {
        this.topupSaldo = topupSaldo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_no() {
        return customer_no;
    }

    public void setCustomer_no(String customer_no) {
        this.customer_no = customer_no;
    }

    public String getSub_customer_name() {
        return sub_customer_name;
    }

    public void setSub_customer_name(String sub_customer_name) {
        this.sub_customer_name = sub_customer_name;
    }

    public String getSub_customer_no() {
        return sub_customer_no;
    }

    public void setSub_customer_no(String sub_customer_no) {
        this.sub_customer_no = sub_customer_no;
    }

    public String getBalance_before() {
        return balance_before;
    }

    public void setBalance_before(String balance_before) {
        this.balance_before = balance_before;
    }

    public String getBalance_after() {
        return balance_after;
    }

    public void setBalance_after(String balance_after) {
        this.balance_after = balance_after;
    }

    public String getCustomer_avatar() {
        return customer_avatar;
    }

    public void setCustomer_avatar(String customer_avatar) {
        this.customer_avatar = customer_avatar;
    }

    public String getSub_customer_avatar() {
        return sub_customer_avatar;
    }

    public void setSub_customer_avatar(String sub_customer_avatar) {
        this.sub_customer_avatar = sub_customer_avatar;
    }

    public int getJenisTransaksi() {
        return jenisTransaksi;
    }

    public void setJenisTransaksi(int jenisTransaksi) {
        this.jenisTransaksi = jenisTransaksi;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getService_category() {
        return service_category;
    }

    public void setService_category(String service_category) {
        this.service_category = service_category;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }
}
