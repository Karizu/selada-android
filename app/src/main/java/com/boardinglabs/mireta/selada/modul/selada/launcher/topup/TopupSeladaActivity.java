package com.boardinglabs.mireta.selada.modul.selada.launcher.topup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.selada.launcher.history.topup.DetailPembayaranActivity;

import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopupSeladaActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.hometoolbar_title)
    TextView hometoolbar_title;
    @BindView(R.id.hometoolbar_logo)
    ImageView hometoolbar_logo;
    @BindView(R.id.layoutToolbarName)
    LinearLayout layoutToolbarName;
    @BindView(R.id.etAmount)
    EditText etAmount;

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack() {
        onBackPressed();
    }

    @OnClick(R.id.btnProses)
    void onClickBtnProses() {
        long mAmount = 0;
        if (!etAmount.getText().toString().equals("")) {
            mAmount = Long.parseLong(etAmount.getText().toString());
        }
        if (etAmount.getText().toString().equals("")) {
            Toast.makeText(context, "Silahkan masukan nominal", Toast.LENGTH_SHORT).show();
        } else if (mAmount < 10000) {
            Toast.makeText(context, "Nominal kurang dari minimum topup", Toast.LENGTH_SHORT).show();
        } else {
            doTopup();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_selada);
        ButterKnife.bind(this);
        context = this;

        hometoolbar_logo.setVisibility(View.GONE);
        layoutToolbarName.setVisibility(View.VISIBLE);
        hometoolbar_title.setText("TOPUP");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addTextChanged(){
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())){
                    String nominal = s.toString();
                    if (nominal.length() > 3){

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doTopup() {
        RequestBody requestBody = null;

        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("member_id", PreferenceManager.getMemberId())
//                    .addFormDataPart("member_id", "7a180792-ea58-40bb-aad1-9d39c1b3c3e6")
                    .addFormDataPart("amount", etAmount.getText().toString())
                    .addFormDataPart("type", "2")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Loading.show(context);
        Api.apiInterface().doTopup(requestBody, PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Topup>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topup>> call, Response<ApiResponse<Topup>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        Topup topup = response.body().getData();
                        int uniqueAmount = topup.getAmount_unique();
                        String date = topup.getExpire();
                        String dateStart = topup.getDate();
                        Intent intent = new Intent(context, DetailPembayaranActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("topup_id", topup.getId());
                        intent.putExtra("uAmount", uniqueAmount);
                        intent.putExtra("date", date);
                        intent.putExtra("dateStart", dateStart);
                        startActivity(intent);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topup>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }
}
