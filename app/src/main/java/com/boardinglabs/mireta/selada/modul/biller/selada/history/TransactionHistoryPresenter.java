package com.boardinglabs.mireta.selada.modul.biller.selada.history;

/**
 * Created by Dhimas on 12/8/17.
 */

public interface TransactionHistoryPresenter {
    void detailTransaction(String transactionId);
    void transactionHistory(String merchantId);
}
