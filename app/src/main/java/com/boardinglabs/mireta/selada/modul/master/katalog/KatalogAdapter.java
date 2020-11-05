package com.boardinglabs.mireta.selada.modul.master.katalog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.ItemResponse;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.KatalogModel;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KatalogAdapter extends RecyclerView.Adapter<KatalogAdapter.ViewHolder> {
    private List<KatalogModel> transactionModels;
    private Context context;
    private Dialog dialog;
    private BottomSheetDialog dialogBottom;
    private List<String> itemImages;
    private String stock_location_id;
    private String stock_id;
    private String subKategoriesId, kategoriesId;
    private ArrayList<String> kategori, kategoriId, subKategori, subKategoriId;
    private Spinner spinnerSubKategori;

    public KatalogAdapter(List<KatalogModel> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    public KatalogAdapter(List<KatalogModel> transactionModels, Context context, List<String> itemImages, String stock_location_id){
        this.transactionModels = transactionModels;
        this.context = context;
        this.itemImages = itemImages;
        this.stock_location_id = stock_location_id;
    }

    @NonNull
    @Override
    public KatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_restock, parent, false);

        return new KatalogAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull KatalogAdapter.ViewHolder holder, int position){
        final KatalogModel transactionModel = transactionModels.get(position);
        final String id = transactionModel.getId();
        final String name = transactionModel.getName();
        final String description = transactionModel.getDeskripsi();
        final String date = transactionModel.getDate();
        final String categoryName = transactionModel.getKategori();
        String qty = transactionModel.getTotal_qty();
        String is_daily_stok = transactionModel.getIs_daily_stock();
        String image = transactionModel.getImage();
        final String harga = transactionModel.getHarga();
//        final String order_no = transactionModel.getId_category_nested();

        Date d = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sdf.parse(date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        sdf.applyPattern("dd-MM-yyyy");

//        getStok(id, holder.qtyInput);
        holder.textViewStok.setVisibility(View.GONE);
        holder.tvDate.setVisibility(View.GONE);
        holder.tvDate.setText(sdf.format(d));
        holder.tvName.setText(name);
        holder.tvDescription.setText(description);
        holder.tvHarga.setText("Rp "+ MethodUtil.toCurrencyFormat(harga) + "");

//        Picasso.get().load(image).placeholder(R.drawable.resto_default).fit().into(holder.imgBarang);

        Glide.with(context).load(image)
                .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                .into(holder.imgBarang);

        holder.imgButton.setVisibility(View.GONE);
        holder.qtyInput.setVisibility(View.GONE);
        holder.imgButton.setOnClickListener(v -> {
            showDialog(R.layout.dialog_restock, context);
            @SuppressLint("CutPasteId") ImageView btnClose = dialog.findViewById(R.id.btnCloseDialog);
            EditText etStok = dialog.findViewById(R.id.etTambahStok);
            Button buttonSimpan = dialog.findViewById(R.id.btnSimpanStock);
            btnClose.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            buttonSimpan.setOnClickListener(v1 -> {
                if (etStok.getText().toString().equals("")){
                    Toast.makeText(context, "Silahkan isi stok", Toast.LENGTH_SHORT).show();
                } else {
                    if (Objects.requireNonNull(holder.qtyInput).getText().toString().equals("0")){
                        createStok(id,etStok);
                        Intent intent = new Intent(context, KatalogActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } else {
                        updateStok(stock_id, etStok);
                        Intent intent = new Intent(context, KatalogActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }
                dialog.dismiss();
            });
        });

        holder.frameLayout.setOnClickListener(v -> {
            Loading.show(context);
            ApiLocal.apiInterface().deleteItems(id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Loading.hide(context);
                    try {
                        Log.d("TAG DELETE", response.message());
                        Toast.makeText(context, "Berhasil hapus barang", Toast.LENGTH_SHORT).show();
                        dialogBottom.dismiss();
                        transactionModels.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, transactionModels.size());
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
        });

        holder.layout.setOnClickListener(view -> {
            showBottomDialog(view);
            TextView title = dialogBottom.findViewById(R.id.tvTitle);
            EditText nama = dialogBottom.findViewById(R.id.etNamaBarang);
            EditText deskripsi = dialogBottom.findViewById(R.id.etDeskripsi);
            EditText price = dialogBottom.findViewById(R.id.etHargaPieces);
            TextView stok = dialogBottom.findViewById(R.id.tvStok);
            ImageButton btnTambah = dialogBottom.findViewById(R.id.btnTambahStok);
            TextView etKategori = dialogBottom.findViewById(R.id.etKategori);
            Button btnSimpan = dialogBottom.findViewById(R.id.btnSimpan);
            Switch switchStok = dialogBottom.findViewById(R.id.switchStok);
            CircleImageView imgBarangs = dialogBottom.findViewById(R.id.imgBarang);
            ImageView btnRefresh = dialogBottom.findViewById(R.id.btnRefresh);

            Objects.requireNonNull(etKategori).setText(categoryName);
            etKategori.setOnClickListener(v -> {
                showDialog(R.layout.dialog_kategori, context);
                ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
                btnCloseDialog.setOnClickListener(v1 -> dialog.dismiss());
                Spinner spinnerKategori = dialog.findViewById(R.id.spinnerKategori);
                setDataSpinner(spinnerKategori, "");
                spinnerSubKategori = dialog.findViewById(R.id.spinnerSubKategori);
                spinnerSubKategori.setVisibility(View.GONE);
                Button button = dialog.findViewById(R.id.btnSimpan);
                button.setOnClickListener(v1 -> {
                    doUpdateKategori(id, etKategori);
                });
            });

//            getStok(id, stok);

            title.setText(name);
            nama.setText(name);
            deskripsi.setText(description);
            price.setText(harga);
            Glide.with(context).load(image)
                    .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                    .into(imgBarangs);

            imgBarangs.setOnClickListener(v -> {
                if (context instanceof KatalogActivity){
                    ((KatalogActivity)context).selectImage();
                }
                if (context instanceof KatalogActivity){
                    if (((KatalogActivity)context).getBitmapCamera() != null){
                        imgBarangs.setImageBitmap(((KatalogActivity)context).getBitmapCamera());
                    } else if (((KatalogActivity)context).getBitmapGallery() != null){
                        imgBarangs.setImageBitmap(((KatalogActivity)context).getBitmapGallery());
                    } else {
                        Glide.with(context).load(image)
                                .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                                .into(imgBarangs);
                    }
                }
            });

            btnRefresh.setOnClickListener(v -> {
                if (context instanceof KatalogActivity){
                    if (((KatalogActivity)context).getBitmapCamera() != null){
                        imgBarangs.setImageBitmap(((KatalogActivity)context).getBitmapCamera());
                    } else if (((KatalogActivity)context).getBitmapGallery() != null){
                        imgBarangs.setImageBitmap(((KatalogActivity)context).getBitmapGallery());
                    }
                }
            });

            btnTambah.setOnClickListener(v -> {
                showDialog(R.layout.dialog_tambah_stok, context);
                TextView title2 = dialog.findViewById(R.id.tvTitle);
                EditText stok2 = dialog.findViewById(R.id.etStok);
                EditText desc = dialog.findViewById(R.id.etDescription);
                Button simpan = dialog.findViewById(R.id.btnSimpan);
                ImageView close = dialog.findViewById(R.id.btnClose);

                close.setOnClickListener(v1 -> dialog.dismiss());

                title2.setText(name);
                simpan.setOnClickListener(v1 -> {

                    if (stok2.getText().toString().equals("")){
                        stok2.setError("Stok harus diisi");
                    } else {
                        if (Objects.requireNonNull(stok).getText().toString().equals("0")){
                            createStok(id,stok2);
                        } else {
                            updateStok(stock_id, stok2);
                        }
                    }
                });
            });

//            if (transactionModel.getIs_daily_stock().equals("true")){
//                switchStok.setChecked(true);
//                stok.setText(transactionModel.getTotal_today_qty() == null ? "0 Pcs" : transactionModel.getTotal_today_qty() + " Pcs");
//            } else {
//                stok.setText(transactionModel.getTotal_qty() == null ? "0 Pcs" : transactionModel.getTotal_qty() + " Pcs");
//                switchStok.setChecked(false);
//            }

            switchStok.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Loading.show(context);
                RequestBody requestBody;
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("_method", "put")
                        .addFormDataPart("is_daily_stock", ""+isChecked)
                        .build();

                String toast;
                if (isChecked){
                    toast = "Is Daily True";
                } else {
                    toast = "Is Daily False";
                }

                String token = "Bearer "+ PreferenceManager.getSessionTokenMireta();

                ApiLocal.apiInterface().showIsDailyStok(id, requestBody, token).enqueue(new Callback<ApiResponse<ItemResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ItemResponse>> call, Response<ApiResponse<ItemResponse>> response) {
                        Loading.hide(context);
                        try {
                            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                            dialogBottom.dismiss();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ItemResponse>> call, Throwable t) {
                        Loading.hide(context);
                        t.printStackTrace();
                    }
                });
            });

            btnSimpan.setOnClickListener(v -> {
                Loading.show(context);
                RequestBody requestBody;

                if (context instanceof KatalogActivity){
                    if (((KatalogActivity)context).getBitmapCamera() != null){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ((KatalogActivity)context).getBitmapCamera().compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("description", ""+ deskripsi.getText().toString())
                                .addFormDataPart("name", ""+ nama.getText().toString())
                                .addFormDataPart("price", ""+ price.getText().toString())
                                .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                                .build();
                    } else if (((KatalogActivity)context).getBitmapGallery() != null){
                        System.out.println(((KatalogActivity)context).getBitmapGallery());
                        File file = createTempFile(((KatalogActivity)context).getBitmapGallery());
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("description", ""+ deskripsi.getText().toString())
                                .addFormDataPart("name", ""+ nama.getText().toString())
                                .addFormDataPart("price", ""+ price.getText().toString())
                                .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                                .build();
                    } else {
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("description", ""+ deskripsi.getText().toString())
                                .addFormDataPart("name", ""+ nama.getText().toString())
                                .addFormDataPart("price", ""+ price.getText().toString())
                                .build();
                    }
                } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("description", ""+ deskripsi.getText().toString())
                            .addFormDataPart("name", ""+ nama.getText().toString())
                            .addFormDataPart("price", ""+ price.getText().toString())
                            .build();
                }

                ApiLocal.apiInterface().updateItem(id, requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Loading.hide(context);
                        try {
                            Toast.makeText(context, "Berhasil edit barang", Toast.LENGTH_SHORT).show();
                            dialogBottom.dismiss();
                            Intent intent = new Intent(context, KatalogActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
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
                dialogBottom.dismiss();
            });
        });
    }

    private void doUpdateKategori(String id, TextView etKategori) {
        Loading.show(context);
        RequestBody requestBody;
        if (subKategoriesId != null) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("category_id", subKategoriesId)
                    .build();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("category_id", kategoriesId)
                    .build();
        }
        ApiLocal.apiInterface().updateKategori(id, requestBody, "Bearer "+PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<ItemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemResponse>> call, Response<ApiResponse<ItemResponse>> response) {
                Loading.hide(context);
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Berhasil merubah kategori", Toast.LENGTH_SHORT).show();
                    getDetailCategory(Objects.requireNonNull(response.body()).getData().getCategory_id(), etKategori);
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemResponse>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void getDetailCategory(String category_id, TextView etKategori) {
        Loading.show(context);
        ApiLocal.apiInterface().getDetailKategori(category_id, "Bearer "+PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>> call, Response<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>> response) {
                Loading.hide(context);
                try {
                    com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse itemResponse = response.body().getData();
                    String name = itemResponse.getName();
                    Objects.requireNonNull(etKategori).setText(name);
                } catch (Exception e){
                    Toast.makeText(context, "Gagal menampilkan data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>> call, Throwable t) {
                Loading.hide(context);
                Toast.makeText(context, "Gagal menampilkan data", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void setDataSpinner(Spinner spinner, String nestedId) {
        Loading.show(context);
        kategori = new ArrayList<>();
        kategoriId = new ArrayList<>();

        ApiLocal.apiInterface().getListCategory(((KatalogActivity)context).business_id, nestedId, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryModel>>> call, Response<ApiResponse<List<CategoryModel>>> response) {
                Loading.hide(context);
                try {
                    List<CategoryModel> res = response.body().getData();
                    if (res.size() < 1) {

                    } else {
                        kategori.add("Pilih Kategori");
                        kategoriId.add("KI");
                        for (int i = 0; i < res.size(); i++) {
                            CategoryModel category = res.get(i);
                            kategori.add(category.getName());
                            kategoriId.add(category.getId());
                        }

                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_text, kategori) {
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
                                if (subKategori != null) {
                                    subKategori.clear();
                                    subKategoriId.clear();
                                }

//                                setSpinnerNestedKatgori(position);

                                if (position != 0) {
                                    kategoriesId = kategoriId.get(position);
                                } else {
                                    kategoriesId = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryModel>>> call, Throwable t) {
//                swipeRefresh.setRefreshing(false);
                Loading.hide(context);
                Log.d("TAG OnFailure", t.getMessage());
            }
        });
    }

    private void setSpinnerNestedKatgori(int position) {
        Loading.show(context);

        subKategori = new ArrayList<>();
        subKategoriId = new ArrayList<>();

        ApiLocal.apiInterface().getListCategory(((KatalogActivity)context).business_id, kategoriId.get(position), "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryModel>>> call, Response<ApiResponse<List<CategoryModel>>> response) {
                Loading.hide(context);
                try {
                    List<CategoryModel> res = response.body().getData();
                    if (res.size() < 1) {

                    } else {
                        subKategori.add("Pilih Sub Kategori");
                        subKategoriId.add("KI");
                        for (int i = 0; i < res.size(); i++) {
                            CategoryModel category = res.get(i);
                            subKategori.add(category.getName());
                            subKategoriId.add(category.getId());
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_text, subKategori) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };

                        Log.d("TAG SubKategori 1", subKategori.get(1));
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
                        // attaching data adapter to spinner
                        spinnerSubKategori.setAdapter(dataAdapter);

                        spinnerSubKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position != 0) {
                                    subKategoriesId = subKategoriId.get(position);
                                } else {
                                    subKategoriesId = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryModel>>> call, Throwable t) {
                Log.d("TAG OnFailure selected", t.getMessage());
//                swipeRefresh.setRefreshing(false);
                Loading.hide(context);
            }
        });

    }

    private void updateStok(String stock_id, EditText stok2) {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("qty", stok2.getText().toString())
                .build();

        ApiLocal.apiInterface().updateStock(stock_id, requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()){
                    Log.d("TAG Update Stok", response.message());
                    Toast.makeText(context, "Berhasil tambah stok", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogBottom.dismiss();
                    Intent intent = new Intent(context, KatalogActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, response.body().getMessage()!=null ? response.body().getMessage() : response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void createStok(String id, EditText stok2) {
        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", ((KatalogActivity)context).location_id)
                .addFormDataPart("item_id", id)
                .addFormDataPart("qty", stok2.getText().toString())
                .build();

        ApiLocal.apiInterface().createStock(requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Log.d("TAG Update Stok", response.message());
                        Toast.makeText(context, "Berhasil tambah stok", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        dialogBottom.dismiss();
                        Intent intent = new Intent(context, KatalogActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, response.body().getMessage()!=null ? response.body().getMessage() : response.message(), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDescription;
        TextView tvHarga;
        ImageView imgBarang;
        LinearLayout layout;
        ImageButton imgButton;
        TextView qtyInput;
        TextView tvDate, textViewStok;
        FrameLayout frameLayout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvHarga = v.findViewById(R.id.tvHarga);
            textViewStok = v.findViewById(R.id.textViewStok);
            imgButton = v.findViewById(R.id.add_button);
            tvDate = v.findViewById(R.id.tvDate);
            qtyInput = v.findViewById(R.id.qty_input);
            imgBarang = v.findViewById(R.id.imgBarang);
            layout = v.findViewById(R.id.layoutKatalog);
            frameLayout = v.findViewById(R.id.user_feed_row_bottom);
        }
    }

    private void showDialog(int layout, Context context) {
        dialog = new Dialog(Objects.requireNonNull(context));
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

    private void showBottomDialog(View view){
        View viewSheet = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_item, null);
        Log.d( "onClick: ",String.valueOf(viewSheet));
        dialogBottom = new BottomSheetDialog(view.getContext());
        dialogBottom.setContentView(viewSheet);
        dialogBottom.show();
    }

    public void updateData(List<KatalogModel> newUser){
        transactionModels = new ArrayList<>();
        transactionModels.addAll(newUser);
        notifyDataSetChanged();
    }

    private File createTempFile(Bitmap bitmap) {
        byte[] bitmapdata = new byte[0];
        File file = null;
        try {
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    , System.currentTimeMillis() + "_image.webp");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 0, bos);
            bitmapdata = bos.toByteArray();
            //write the bytes in file
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(Objects.requireNonNull(file));
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void getStok(String item_id, TextView stok){
        Loading.show(context);
        Log.d("TAG", "Masuk getStok");
        ApiLocal.apiInterface().getKatalogStok(stock_location_id, item_id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<StockResponse>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<StockResponse>>> call, Response<ApiResponse<List<StockResponse>>> response) {
                Loading.hide(context);
                try {
                    List<StockResponse> res = Objects.requireNonNull(response.body()).getData();
                    for (int i = 0; i < res.size(); i++){
                        StockResponse stockResponse = res.get(i);
                        stock_id = stockResponse.getId()+"";
                        stok.setText(stockResponse.getQty() == null ? "0" : stockResponse.getQty());
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Log.d("TAG", "Masuk Exception");
                    stok.setText("0 Pcs");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockResponse>>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }
}
