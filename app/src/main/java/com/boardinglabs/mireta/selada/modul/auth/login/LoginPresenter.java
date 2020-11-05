package com.boardinglabs.mireta.selada.modul.auth.login;

import android.util.Log;

import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.entities.ErrorParser;
import com.boardinglabs.mireta.selada.component.network.response.LoginResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.zendesk.logger.Logger;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;

public class LoginPresenter {
    private CommonInterface cInterface;
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenter(CommonInterface cInterface, LoginView view) {
        mView = view;
        this.cInterface = cInterface;
        mInteractor = new LoginInteractor(this.cInterface.getService());
    }

    public void login(String username, String password) {
        cInterface.showProgressLoading();
        mInteractor.login(username, password).subscribe(new Subscriber<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable t) {
                cInterface.hideProgresLoading();

                if (t instanceof HttpException) {
                    ResponseBody body = ((HttpException) t).response().errorBody();
                    Gson gson = new Gson();
                    TypeAdapter<ErrorParser> adapter = gson.getAdapter
                            (ErrorParser.class);
                    try {
                        ErrorParser errorParser =
                                adapter.fromJson(Objects.requireNonNull(body).string());
                        Log.i("LOGIN", "Error:" + errorParser.getError());
                        cInterface.onFailureRequest(errorParser.getError());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                cInterface.hideProgresLoading();
                PreferenceManager.saveMerchant(loginResponse.data.merchant);
                PreferenceManager.setIsSeladaPos(loginResponse.data.is_user_mireta);
                PreferenceManager.saveLogIn(loginResponse.token, loginResponse.data.id, loginResponse.data.fullname, loginResponse.data.username, loginResponse.data.ref_ardi);
                mView.onSuccessRequest();
            }
        });
    }
}
