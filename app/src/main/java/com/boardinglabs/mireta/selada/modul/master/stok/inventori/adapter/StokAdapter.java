package com.boardinglabs.mireta.selada.modul.master.stok.inventori.adapter;

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
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.StokActivity;
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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StokAdapter extends RecyclerView.Adapter<StokAdapter.ViewHolder> {
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
    private SwipeRefreshLayout swipeRefresh;

    public StokAdapter(List<KatalogModel> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    public StokAdapter(List<KatalogModel> transactionModels, Context context, List<String> itemImages, String stock_location_id, SwipeRefreshLayout swipeRefresh){
        this.transactionModels = transactionModels;
        this.context = context;
        this.itemImages = itemImages;
        this.stock_location_id = stock_location_id;
        this.swipeRefresh = swipeRefresh;
    }

    @NonNull
    @Override
    public StokAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_restock, parent, false);

        return new StokAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull StokAdapter.ViewHolder holder, int position){
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

        getStok(id, holder.qtyInput);

        holder.tvDate.setVisibility(View.GONE);
        holder.tvDate.setText(sdf.format(d));
        holder.tvName.setText(name);
        holder.tvDescription.setText(description);
        holder.tvHarga.setText("Rp "+ MethodUtil.toCurrencyFormat(harga) + "");

//        Picasso.get().load(image).placeholder(R.drawable.resto_default).fit().into(holder.imgBarang);

        Glide.with(context).load(image)
                .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                .into(holder.imgBarang);

        holder.imgButton.setOnClickListener(v -> {
            showDialog(R.layout.dialog_restock, context);
            @SuppressLint("CutPasteId") ImageView btnClose = dialog.findViewById(R.id.btnCloseDialog);
            EditText etStok = dialog.findViewById(R.id.etTambahStok);
            Button buttonSimpan = dialog.findViewById(R.id.btnSimpanStock);
            btnClose.setVisibility(View.GONE);
            btnClose.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            buttonSimpan.setOnClickListener(v1 -> {
                if (etStok.getText().toString().equals("")){
                    Toast.makeText(context, "Silahkan isi stok", Toast.LENGTH_SHORT).show();
                } else {
                    if (Objects.requireNonNull(holder.qtyInput).getText().toString().equals("-")){
                        dialog.dismiss();
                        if (etStok.getText().toString().equals("0")){
                            Toast.makeText(context, "Silahkan input nominal yang benar", Toast.LENGTH_SHORT).show();
                        } else {
                            createStok(id,etStok.getText().toString());
                        }
                    } else {
                        dialog.dismiss();
                        getStokForUpdate(id, etStok.getText().toString());
                    }
                }
            });
        });

        holder.layout.setOnClickListener(view -> {
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
                    if (Objects.requireNonNull(holder.qtyInput).getText().toString().equals("-")){
                        dialog.dismiss();
                        if (etStok.getText().toString().equals("0")){
                            Toast.makeText(context, "Silahkan input nominal yang benar", Toast.LENGTH_SHORT).show();
                        } else {
                            createStok(id,etStok.getText().toString());
                        }
                    } else {
                        dialog.dismiss();
                        getStokForUpdate(id, etStok.getText().toString());
                    }
                }
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

        ApiLocal.apiInterface().getListCategory(((StokActivity)context).business_id, nestedId, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
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

                                setSpinnerNestedKatgori(position);

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

        ApiLocal.apiInterface().getListCategory(((StokActivity)context).business_id, kategoriId.get(position), "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
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

    private void updateStok(String stock_id, String qtyStok) {
        Log.d("stock_id, qty", stock_id+" "+qtyStok);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("qty", qtyStok)
                .build();

        ApiLocal.apiInterface().updateStock(stock_id, requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()){
                    Log.d("TAG Update Stok", response.message());
                    Toast.makeText(context, "Berhasil tambah stok", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, StokActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    try {
                        Toast.makeText(context, response.body().getMessage()!=null ? response.body().getMessage() : response.message(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        e.printStackTrace();
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

    @SuppressLint("LongLogTag")
    private void createStok(String id, String stok2) {
        Loading.show(context);
        Log.d("location_id, item_id, stok", ((StokActivity)context).location_id+" "+id+" "+stok2);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", ((StokActivity)context).location_id)
                .addFormDataPart("item_id", id)
                .addFormDataPart("qty", stok2)
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
                        Intent intent = new Intent(context, StokActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
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
        TextView tvDate;
        FrameLayout frameLayout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvHarga = v.findViewById(R.id.tvHarga);
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
        swipeRefresh.setRefreshing(true);
        Log.d("TAG", "Masuk getStok 2");
        ApiLocal.apiInterface().getKatalogStok(stock_location_id, item_id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<StockResponse>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<StockResponse>>> call, Response<ApiResponse<List<StockResponse>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    List<StockResponse> res = Objects.requireNonNull(response.body()).getData();
                    for (int i = 0; i < res.size(); i++){
                        StockResponse stockResponse = res.get(i);
                        stock_id = stockResponse.getId()+"";
                        stok.setText(stockResponse.getQty() == null ? "-" : stockResponse.getQty());
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Log.d("TAG", "Masuk Exception");
                    stok.setText("0 Pcs");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StockResponse>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void getStokForUpdate(String item_id, String qty){
        Loading.show(context);
        Log.d("TAG", "Masuk getStokForUpdate");
        ApiLocal.apiInterface().getKatalogStok(stock_location_id, item_id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<StockResponse>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<StockResponse>>> call, Response<ApiResponse<List<StockResponse>>> response) {

                try {
                    List<StockResponse> res = Objects.requireNonNull(response.body()).getData();
                    for (int i = 0; i < res.size(); i++){
                        StockResponse stockResponse = res.get(i);
                        stock_id = stockResponse.getId()+"";
                        updateStok(stock_id, qty);
                    }

                } catch (Exception e){
                    Loading.hide(context);
                    e.printStackTrace();
                    Log.d("TAG", "Masuk Exception");
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
