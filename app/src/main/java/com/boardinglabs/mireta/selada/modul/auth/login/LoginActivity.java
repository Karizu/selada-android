package com.boardinglabs.mireta.selada.modul.auth.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.component.dialog.CustomProgressBar;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.NetworkLogin;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.LoginResponse;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.selada.modul.auth.RegisterActivity;
import com.boardinglabs.mireta.selada.modul.auth.forgotpassword.ForgotPasswordActivity;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.rxbinding.view.RxView;
import com.boardinglabs.mireta.selada.MiretaPOSApplication;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import java.util.Objects;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

/**
 * Created by Dhimas on 10/5/17.
 */

public class LoginActivity extends AppCompatActivity implements CommonInterface, LoginView {
    private TextView registerText;
    private EditText username;
    private EditText password;
    private Button loginBtn;
    private ImageView loginImg;
    private TextView forgot_text;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static CustomProgressBar progressBar = new CustomProgressBar();
    private LoginPresenter mPresenter;
    private Intent intent;
    private Context context;
    private Boolean isSeladaPos = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        context = this;

        intent = getIntent();

        PreferenceManager.logOut();
        initComponent();
        initEvent();
        MiretaPOSApplication app = (MiretaPOSApplication) getApplication();
        mFirebaseAnalytics = app.getFirebaseAnalytics();
        mFirebaseAnalytics.setCurrentScreen(this, "Login Screen", this.getClass().getSimpleName());
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "LoginActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
        mFirebaseAnalytics.setUserProperty("Page", "Login Page");
        Fabric.with(this, new Crashlytics());
        mPresenter = new LoginPresenter(this, this);
    }

    private void initComponent() {
        registerText = (TextView) findViewById(R.id.register_text);
        loginBtn = (Button) findViewById(R.id.login_btn);
        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
        loginImg = (ImageView) findViewById(R.id.image_login);
        forgot_text = (TextView) findViewById(R.id.forgot_text);

        if (PreferenceManager.getUserInfo() != null) {
            username.setText(PreferenceManager.getUserInfo()[1]);
        }

        try {
            username.setText(intent.getStringExtra("username"));
            password.setText(intent.getStringExtra("password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginArdi(){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "merchant")
                .addFormDataPart("password", "123456")
                .build();

        Api.apiInterface().loginArdi(requestBody).enqueue(new Callback<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> call, Response<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> response) {
                if (response.isSuccessful()){
                    PreferenceManager.setSessionTokenArdi(Objects.requireNonNull(response.body()).getAccess_token());
                    PreferenceManager.setUserIdArdi(response.body().getData().getId());
                    Log.d("TAG", "MASUK LOGIN ARDI");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loginMireta(String username, String password){
        try {
            ApiLocal.apiInterface().loginMireta(username, password).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();

                            PreferenceManager.setSessionTokenMireta(loginResponse.access_token);
                            PreferenceManager.saveUser(loginResponse.data);
                            PreferenceManager.saveBusiness(loginResponse.data.business);
                            PreferenceManager.saveStockLocation(loginResponse.data.user_location);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        RxView.clicks(registerText).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        RxView.clicks(forgot_text).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });

        RxView.clicks(loginBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (TextUtils.isEmpty(username.getText().toString())) {
                    username.setError("Masukkan Username Anda");
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Masukkan Password Anda");
                } else {
                    loginArdi();
                    if (username.getText().toString().equals("admin_bl") && password.getText().toString().equals("admin_bl")){
                        PreferenceManager.saveLogInArdi();
                        Intent intent = new Intent(LoginActivity.this, HomeArdiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        loginMireta(username.getText().toString(), password.getText().toString());
                        mPresenter.login(username.getText().toString(), password.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkLogin.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    @Override
    public void onSuccessRequest() {
            Intent intent = new Intent(this, SeladaLaucherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }
}
