package com.boardinglabs.mireta.selada.modul.auth.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.auth.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.hometoolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.hometoolbar_logo)
    ImageView hometoolbar_logo;
    @BindView(R.id.hometoolbar_imgBtnMenu)
    ImageView hometoolbar_imgBtnMenu;

    @OnClick(R.id.login_submit)
    void onClickSubmit(){
        if (input_email.getText().toString().equals("")){
            Toast.makeText(context, "Mohon isi email anda", Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.isValidEmail(input_email.getText().toString())) {
                doForgotPassword();
            } else {
                Toast.makeText(context, "Masukan email dengan benar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        context = this;

        hometoolbar_imgBtnMenu.setVisibility(View.GONE);
        hometoolbar_logo.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText("LUPA PASSWORD");

    }

    private void doForgotPassword(){
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", input_email.getText().toString())
                .build();

        ApiLocal.apiInterface().postForgotPassword(requestBody).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Toast.makeText(context, "Silahkan cek email anda untuk melihat password baru yang kami kirimkan", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }
}
