package com.boardinglabs.mireta.selada.modul.master.brand.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.brand.model.Categories;
import com.boardinglabs.mireta.selada.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.selada.modul.master.categories.CategoryActivity;

import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {
    private List<Categories> transactionModels;
    private Context context;
    private Dialog dialog;
    private String businessId;

    public BrandAdapter(List<Categories> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    public BrandAdapter(List<Categories> transactionModels, Context context, String businessId){
        this.transactionModels = transactionModels;
        this.context = context;
        this.businessId = businessId;
    }

    @NonNull
    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_brand, parent, false);

        return new BrandAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull BrandAdapter.ViewHolder holder, int position){
        final Categories transactionModel = transactionModels.get(position);
        final String id = transactionModel.getId();
        final String name = transactionModel.getName();
//        final String description = transactionModel.getName();
//        final String discount = transactionModel.getBusiness_id();
//        final String order_no = transactionModel.getId_category_nested();

        holder.tvName.setText(name);
//        holder.tvDescription.setText(order_date);
        holder.imgOptions.setOnClickListener(v -> {
            View v1 = v.findViewById(R.id.imgOptions);
            PopupMenu pm = new PopupMenu(Objects.requireNonNull(context), v1);
            pm.getMenuInflater().inflate(R.menu.menu_options, pm.getMenu());
            pm.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_edit:
                        showDialog();
                        TextView title = dialog.findViewById(R.id.tvTitle);
                        EditText nama = dialog.findViewById(R.id.etNamaKategori);
                        Button simpan = dialog.findViewById(R.id.btnSimpanKategori);

                        title.setText("EDIT KATEGORI");
                        nama.setText(name);

                        simpan.setOnClickListener(v2 -> {

                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("name", nama.getText().toString())
                                    .build();

                            String token = "Bearer "+ PreferenceManager.getSessionTokenMireta();
                            ApiLocal.apiInterface().kelolaCategories(id, requestBody, token).enqueue(new Callback<ApiResponse<CategoryModel>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<CategoryModel>> call, Response<ApiResponse<CategoryModel>> response) {
                                    try {
                                        if (response.isSuccessful()){
                                            Log.d("TAG RESPONSE KATEGORI", response.message());
                                            Toast.makeText(context, "Berhasil mengubah kategori", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            Intent intent = new Intent(context, CategoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, response.message()+"\n"+"Gagal mengubah kategori", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<CategoryModel>> call, Throwable t) {
                                    Log.d("TAG FAILURE KATEGORI", t.getMessage());
                                }
                            });

                        });
                        break;
                    case R.id.navigation_delete:

                        showDialog();
                        TextView title1 = dialog.findViewById(R.id.tvTitle);
                        EditText nama1 = dialog.findViewById(R.id.etNamaKategori);
                        Button simpan1 = dialog.findViewById(R.id.btnSimpanKategori);

                        simpan1.setText("DELETE");
                        nama1.setText(name);
                        title1.setText("DELETE KATEGORI");

                        simpan1.setOnClickListener(v2 -> {

                            String token = "Bearer "+ PreferenceManager.getSessionTokenMireta();
                            ApiLocal.apiInterface().deleteCategories(id, token).enqueue(new Callback<ApiResponse<CategoryModel>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<CategoryModel>> call, Response<ApiResponse<CategoryModel>> response) {
                                    try {
                                        if (response.isSuccessful()){
                                            Log.d("TAG RESPONSE KATEGORI", response.message());
                                            Toast.makeText(context, "Berhasil delete kategori", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            Intent intent = new Intent(context, CategoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, response.message()+"\n"+"Gagal delete kategori", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<CategoryModel>> call, Throwable t) {
                                    Log.d("TAG FAILURE KATEGORI", t.getMessage());
                                }
                            });

                        });

                        break;
                }
                return true;
            });
            pm.show();
        });

        holder.layout.setOnClickListener(view -> {
//            Intent intent = new Intent(context, CategoryActivity.class);
//            intent.putExtra("nested_id", id);
//            context.startActivity(intent);
//            Toast.makeText(context, "On Progress", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDescription;
        ImageView imgOptions;
        CardView layout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            imgOptions = v.findViewById(R.id.imgOptions);
            layout = v.findViewById(R.id.layoutBrands);
        }
    }

   private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(context));
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
