package com.boardinglabs.mireta.selada.modul.auth.login;

import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.response.LoginResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginInteractor {
    private NetworkService mService;

    public LoginInteractor(NetworkService service) {
        mService = service;

    }

    public Observable<LoginResponse> login(String username, String password) {
        return mService.loginBusiness(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
