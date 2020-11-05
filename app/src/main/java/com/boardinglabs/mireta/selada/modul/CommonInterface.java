package com.boardinglabs.mireta.selada.modul;

import com.boardinglabs.mireta.selada.component.network.NetworkService;

/**
 * Created by Dhimas on 11/23/17.
 */

public interface CommonInterface {
    void showProgressLoading();

    void hideProgresLoading();

    NetworkService getService();

    void onFailureRequest(String msg);
}
