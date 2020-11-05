package com.boardinglabs.mireta.selada.modul.akun.ubahpassword;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.ApiSelada;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.akun.AkunActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahPaswordActivity extends BaseActivity {

    private Context context;

    @BindView(R.id.etPasswordLama)
    EditText etPasswordLama;
    @BindView(R.id.etPasswordBaru)
    EditText etPasswordBaru;
    @BindView(R.id.etUlangiPassword)
    EditText etUlangiPassword;

    @OnClick(R.id.btnUbahPassword)
    void onClickBtnUbahPassword() {
        if (etUlangiPassword.getText().toString().equals(etPasswordBaru.getText().toString())){
            doChangePassword();
        } else {
            etUlangiPassword.setError("Password Tidak Sesuai");
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_ubah_pasword;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("UBAH PASSWORD");
        context = this;

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

    private void doChangePassword() {
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("old_password", etPasswordLama.getText().toString())
                .addFormDataPart("new_password", etPasswordBaru.getText().toString())
                .addFormDataPart("user_id", PreferenceManager.getSeladaUserId())
                .addFormDataPart("user_id_mireta", PreferenceManager.getUser().id)
                .build();

        ApiSelada.apiInterface().changePassword(requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Toast.makeText(context, "Berhasil merubah password", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UbahPaswordActivity.this, AkunActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            }
        });
    }
}
