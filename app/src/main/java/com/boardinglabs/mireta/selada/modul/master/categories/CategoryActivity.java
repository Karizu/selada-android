package com.boardinglabs.mireta.selada.modul.master.categories;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.brand.adapter.BrandAdapter;
import com.boardinglabs.mireta.selada.modul.master.brand.model.Categories;
import com.boardinglabs.mireta.selada.modul.master.brand.model.CategoryModel;

import org.json.JSONObject;

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

public class CategoryActivity extends BaseActivity {

    private List<Categories> categories;
    private Dialog dialog;
    private RequestBody requestBody;
    private String nestedId = null;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.btnTambah)
    LinearLayout btnTambah;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_category;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("KATEGORI");

        Intent intent = getIntent();
        if (intent.getStringExtra("nested_id") != null) {
            nestedId = intent.getStringExtra("nested_id");
            getListCategory(intent.getStringExtra("nested_id"));
            swipeRefresh.setOnRefreshListener(() -> {
                categories.clear();
                getListCategory(intent.getStringExtra("nested_id"));
            });
        } else {
            getListCategory("");
            swipeRefresh.setOnRefreshListener(() -> {
                categories.clear();
                getListCategory("");
            });
        }
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

    @OnClick(R.id.btnTambah)
    void onClickBtnTambah() {

        showDialog();
        EditText nama = dialog.findViewById(R.id.etNamaKategori);
        Button simpan = dialog.findViewById(R.id.btnSimpanKategori);

        simpan.setOnClickListener(v -> {

            if (nama.getText().toString().equals("")) {
                nama.setError("Nama kategori harus diisi");
            } else {

                if (nestedId != null) {
                    Log.d("TAG nestedId", nestedId);
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", nama.getText().toString())
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", nestedId)
                            .build();
                } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", nama.getText().toString())
                            .addFormDataPart("business_id", loginBusiness.id)
                            .build();
                }

                String token = "Bearer "+ PreferenceManager.getSessionTokenMireta();
                ApiLocal.apiInterface().postCategories(requestBody, token).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        try {
                            if (response.isSuccessful()){
                                Log.d("TAG RESPONSE KATEGORI", response.message());
                                Toast.makeText(context, "Berhasil tambah kategori", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                                startActivity(getIntent());
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
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.d("TAG FAILURE KATEGORI", t.getMessage());
                    }
                });
            }
        });

    }

    private void getListCategory(String nested_id) {
        swipeRefresh.setRefreshing(true);
        categories = new ArrayList<>();
        ApiLocal.apiInterface().getListCategory(loginBusiness.id, nested_id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryModel>>> call, Response<ApiResponse<List<CategoryModel>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    List<CategoryModel> res = response.body().getData();
                    if (res.size() < 1){
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        CategoryModel category = res.get(i);
                        categories.add(new Categories(category.getId(),
                                category.getName(),
                                category.getId_category_nested(),
                                category.getBusiness_id()));
                    }

                    BrandAdapter adapter = new BrandAdapter(categories, CategoryActivity.this, loginBusiness.id);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryModel>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.d("TAG OnFailure", t.getMessage());
            }
        });
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(CategoryActivity.this));
        //set content
        dialog.setContentView(R.layout.layout_tambah_kategori);
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
