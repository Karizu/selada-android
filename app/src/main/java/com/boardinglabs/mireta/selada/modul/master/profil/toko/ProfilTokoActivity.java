package com.boardinglabs.mireta.selada.modul.master.profil.toko;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.akun.AkunActivity;
import com.boardinglabs.mireta.selada.modul.akun.pengaturan.PengaturanAkunActivity;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilTokoActivity extends BaseActivity {

    private Context context;
    private static String INTENT_PROFIL = "intentProfil";
    private boolean isIntentFromSelada = false;

    @BindView(R.id.etNamaToko)
    EditText etNamaToko;
    @BindView(R.id.etAlamatToko)
    EditText etAlamatToko;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etNoTelp)
    EditText etNoTelp;

    @OnClick(R.id.btnSimpan)
    void onClick(){
        if (etNamaToko.getText().toString().equals("") || etAlamatToko.getText().toString().equals("")){
            Toast.makeText(context, "Silahkan input data dengan benar", Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.isValidMobile(etNoTelp.getText().toString()) && Utils.isValidEmail(etEmail.getText().toString())){
                doSaveProfil();
            } else if (!Utils.isValidEmail(etEmail.getText().toString())) {
                etEmail.setError("Input email dengan benar");
            } else if (!Utils.isValidMobile(etNoTelp.getText().toString())){
                etNoTelp.setError("Input nomor dengan benar");
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profil_toko;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("PROFILE TOKO");
        context = this;

        //FLAG IF INTENT FROM SELADA
        Intent intent = getIntent();
        if (intent.getStringExtra(INTENT_PROFIL)!=null){
            isIntentFromSelada = true;
        }

        getDetailProfil();

    }

    private void getDetailProfil(){
        Loading.show(context);
        ApiLocal.apiInterface().getDetailLocation(loginStockLocation.id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<DetailLocationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DetailLocationResponse>> call, Response<ApiResponse<DetailLocationResponse>> response) {
                Loading.hide(context);
                try {
                        DetailLocationResponse res = Objects.requireNonNull(response.body()).getData();
                        etNamaToko.setText(res.getName());
                        etAlamatToko.setText(res.getAddress());
                        etEmail.setText(res.getEmail());
                        etNoTelp.setText(res.getPhone());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DetailLocationResponse>> call, Throwable t) {
                Loading.hide(context);
            }
        });
    }

    private void doSaveProfil(){
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", etNamaToko.getText().toString())
                .addFormDataPart("business_id", loginBusiness.id)
                .addFormDataPart("brand_id", loginStockLocation.brand_id)
                .addFormDataPart("email", etEmail.getText().toString())
                .addFormDataPart("phone", etNoTelp.getText().toString())
                .addFormDataPart("address", etAlamatToko.getText().toString())
                .build();

        ApiLocal.apiInterface().updateProfilToko(loginStockLocation.id, requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        StockLocation stockLocation = new StockLocation();
                        stockLocation.id = loginStockLocation.id;
                        stockLocation.name = etNamaToko.getText().toString();
                        stockLocation.address = etAlamatToko.getText().toString();
                        stockLocation.telp = etNoTelp.getText().toString();
                        stockLocation.brand_id = loginStockLocation.brand_id;
                        PreferenceManager.saveStockLocation(stockLocation);
                        Toast.makeText(context, "Berhasil update profil", Toast.LENGTH_SHORT).show();

                        if (isIntentFromSelada) {

                            HashMap<String, String> data = new HashMap<String, String>();
                            data.put("menu", "profil");
                            data.put("storeName", etNamaToko.getText().toString());
                            Utils.openApp(ProfilTokoActivity.this, "id.co.tornado.billiton", data);

                            Intent intent = new Intent(context, PengaturanAkunActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, AkunActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
            }
        });
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

    private void getDetailTenant(){

    }
}
