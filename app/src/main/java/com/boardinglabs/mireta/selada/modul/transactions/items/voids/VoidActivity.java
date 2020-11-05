package com.boardinglabs.mireta.selada.modul.transactions.items.voids;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Scanner;
import com.boardinglabs.mireta.selada.modul.akun.AkunActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoidActivity extends BaseActivity {

    private Dialog dialog;
    private Context context;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER = 999;

    @BindView(R.id.etTransactionCode)
    EditText etTransactionCode;
    @BindView(R.id.imgScan)
    ImageView imgScan;

    @OnClick(R.id.btnVoid)
    void onClickbtnVoid() {
        showDialogLayout(R.layout.layout_input_password);
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnProses = dialog.findViewById(R.id.btnProses);
        btnProses.setOnClickListener(v -> {
            if (etPassword.getText().toString().equals(PreferenceManager.getPassVoid())) {
                doCheckSettled();
                dialog.dismiss();
            } else {
                dialog.dismiss();
                showDialogLayout(R.layout.layout_wrong_pass);
                Button btnOk = dialog.findViewById(R.id.btnOK);
                btnOk.setOnClickListener(v1 -> dialog.dismiss());
            }
        });
    }

    @OnClick(R.id.imgScan)
    void onClickImgScan(){

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(VoidActivity.this), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(VoidActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            Intent intent = new Intent(VoidActivity.this, Scanner.class);
            startActivityForResult(intent, REQUEST_SCANNER);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_void;
    }

    @Override
    protected void setContentViewOnChild() {
        setToolbarTitle("Void");
        ButterKnife.bind(this);
        context = this;
        imgScan.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private void reLogin() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "merchant")
                .addFormDataPart("password", "123456")
                .build();

        Api.apiInterface().loginArdi(requestBody).enqueue(new Callback<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> call, Response<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> response) {
                if (response.isSuccessful()) {
                    PreferenceManager.setSessionTokenArdi(Objects.requireNonNull(response.body()).getAccess_token());
                    PreferenceManager.setUserIdArdi(response.body().getData().getId());
                    Log.d("TAG", "MASUK LOGIN ARDI");
                    doVoidArdi();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void doCheckSettled(){
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("transaction_code", etTransactionCode.getText().toString())
                .build();
        ApiLocal.apiInterface().doCheckSettled(requestBody, "Bearer "+PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()){
                    Log.d("TAG", "MASUK CEK SETTLE");
                    doVoidArdi();
                } else {
                    if (response.message().equals("Forbidden")){
                        Toast.makeText(context, "Transaksi yang sudah disettle tidak bisa dibatalkan", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void doVoidArdi() {
        Loading.show(context);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = s1.format(new Date());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("transaction_code", etTransactionCode.getText().toString())
                .addFormDataPart("user_id", PreferenceManager.getUserIdArdi())
                .addFormDataPart("date", date)
                .build();

        Api.apiInterface().doVoidArdi(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Loading.hide(context);
                    Toast.makeText(context, "Berhasil membatalkan transaksi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VoidActivity.this, AkunActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    try {
                        Loading.hide(context);
                        if (response.message().equals("Unauthorized")) {
                            reLogin();
                            Toast.makeText(context, Objects.requireNonNull(response.body()).getError() != null ? response.body().getError() : response.message(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, Objects.requireNonNull(response.body()).getError() != null ? response.body().getError() : response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Loading.hide(context);
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCANNER && resultCode == Activity.RESULT_OK) {
            String resultData = data.getStringExtra("scan_data");
            Log.d("Data Scan", resultData);
            etTransactionCode.setText(resultData);
        }
    }

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(VoidActivity.this));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
