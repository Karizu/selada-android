package com.boardinglabs.mireta.selada.modul.ardi.pengaturan;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Booths;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengaturanActivity extends BaseActivity {


    private String spinnerBoothId;
    private List<String> booths_id, booths_name;
    private Dialog dialog;
    private Spinner spinner;
    private Context context;

    @BindView(R.id.spinnerBooth)
    Spinner spinnerBooth;

    @OnClick(R.id.btnKeluar)
    void onClickKeluar(){
        goToLoginPage();
    }

    @OnClick(R.id.btnPilihBooth)
    void onClickBooth(){
        setDialog(R.layout.layout_pilih_booth);
        spinner = dialog.findViewById(R.id.spinnerBooth);
//        initData();
        EditText etBoothId = dialog.findViewById(R.id.etBoothId);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        etBoothId.setText(PreferenceManager.getBoothId());
        btnSimpan.setOnClickListener(v -> {
            if (etBoothId.getText().toString().equals("")){
                Toast.makeText(context, "Silahkan input ID booth", Toast.LENGTH_SHORT).show();
            } else {
                updateBoothStatus(etBoothId.getText().toString(), "1");
            }
        });
    }

    @OnClick(R.id.btnUbahMasterKey)
    void onClickMasterKey(){
        setDialog(R.layout.layout_master_key);
        Log.d("masterKey", PreferenceManager.getMasterKey());
        EditText etMasterKey = dialog.findViewById(R.id.tvContent);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        etMasterKey.setText(PreferenceManager.getMasterKey());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            if (etMasterKey.getText().toString().equals("")){
                Toast.makeText(context, "Silahkan isi master key", Toast.LENGTH_SHORT).show();
            } else {
                PreferenceManager.setMasterKey(etMasterKey.getText().toString());
                Toast.makeText(context, "Berhasil menyimpan master key", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.btnClearData)
    void onClickClearData(){
        setDialog(R.layout.layout_clear_data);
        Button btnSimpan = dialog.findViewById(R.id.btnClearData);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            if (PreferenceManager.getBoothId().equals("")){
                Toast.makeText(context, "Anda belum menginput ID booth", Toast.LENGTH_SHORT).show();
            } else {
                updateBoothStatus(PreferenceManager.getBoothId(), "0");
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pengaturan;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pengaturan");

        context = this;
        booths_name = new ArrayList<>();
        booths_id = new ArrayList<>();
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

    private void initData(){
        Loading.show(PengaturanActivity.this);
        Api.apiInterface().getListBooth("Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<Booths>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booths>>> call, Response<ApiResponse<List<Booths>>> response) {
                Loading.hide(PengaturanActivity.this);
                try {
                    booths_name.add("Pilih Booth");
                    booths_id.add(null);
                    List<Booths> res = response.body().getData();
                    for (int i = 0; i < res.size(); i++) {
                        Booths booths = res.get(i);
                        booths_name.add(booths.getName());
                        booths_id.add(booths.getId());
                    }

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PengaturanActivity.this, R.layout.layout_spinner_text, booths_name){
                        @Override
                        public boolean isEnabled(int position) {
                            return position != 0;
                        }
                    };

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);

                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {
                                spinnerBoothId = booths_id.get(position);
                                PreferenceManager.setBoothId(spinnerBoothId);
                                Log.d("booth_id", spinnerBoothId);
                            } else {
                                spinnerBoothId = null;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booths>>> call, Throwable t) {
                Loading.hide(PengaturanActivity.this);
                t.printStackTrace();
            }
        });
    }

    private void updateBoothStatus(String booth_id, String status){
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("is_used", status)
                .build();

        Api.apiInterface().updateBoothStatus(booth_id, requestBody, "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        if (status.equals("1")){
                            PreferenceManager.setBoothId(booth_id);
                            Toast.makeText(context, "Berhasil memperbarui booth", Toast.LENGTH_SHORT).show();
                        } else {
                            PreferenceManager.setBoothId("");
                            PreferenceManager.setMasterKey("");
                            Toast.makeText(context, "Berhasil clear data", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Gagal memperbarui booth", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void setDialog(int layout) {

        dialog = new Dialog(Objects.requireNonNull(PengaturanActivity.this));
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
