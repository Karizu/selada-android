package com.boardinglabs.mireta.selada.modul.master.stok.inventori;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.CategoriesResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.akun.AkunActivity;
import com.boardinglabs.mireta.selada.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.ItemResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahBarangActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 1;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private String subKategoriesId, kategoriesId, isDaily;
    private Context context;
    private File imageCheck;
    private List<String> kategori;
    private List<String> kategoriId;
    private List<String> subKategori;
    private List<String> subKategoriId;
    private Bitmap selectedImage;
    private Bitmap photoImage;
    private RequestBody requestBody;
    private Dialog dialog;
    private Spinner spinnerSubKategori;

    @BindView(R.id.imgBarang)
    ImageView imgBarang;
    @BindView(R.id.etKodeBarang)
    EditText etKodeBarang;
    @BindView(R.id.etNamaBarang)
    EditText etNamaBarang;
    @BindView(R.id.etSubKategori)
    Spinner etSubKategori;
    @BindView(R.id.etJumlahPieces)
    EditText etJumlahPieces;
    @BindView(R.id.etDeskripsi)
    EditText etDeskripsi;
    @BindView(R.id.etStok)
    EditText etStok;
    @BindView(R.id.etHargaPieces)
    EditText etHargaPieces;
    @BindView(R.id.etStokAwal)
    EditText etStokAwal;
    @BindView(R.id.etKategori)
    TextView etKategori;
    @BindView(R.id.switchStok)
    Switch switchStok;
    @BindView(R.id.spinnerKategori)
    Spinner spinnerKategori;

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.etKategori)
    void onClickKategori(){
        showDialogKategori();
        TextView tvTitleBarang = dialog.findViewById(R.id.tvTitle);
        TextView tvNoData = dialog.findViewById(R.id.tvNoData);
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
        tvTitleBarang.setText("Pilih Kategori");
        Spinner spinnerKategori = dialog.findViewById(R.id.spinnerKategori);
        spinnerSubKategori = dialog.findViewById(R.id.spinnerSubKategori);
        spinnerSubKategori.setVisibility(View.GONE);
        settingKategoriSpinner(spinnerKategori, tvNoData, "");
        Button button = dialog.findViewById(R.id.btnSimpan);
        button.setOnClickListener(v1 -> {
            if (subKategoriesId != null) {
                getDetailCategory(subKategoriesId, etKategori);
                dialog.dismiss();
            } else {
                getDetailCategory(kategoriesId, etKategori);
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.tambahKategori)
    void onClickTambahKategori(){
        showDialog();
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText("Tambah Kategori");
        EditText nama = dialog.findViewById(R.id.etNamaKategori);
        Button simpan = dialog.findViewById(R.id.btnSimpanKategori);

        simpan.setOnClickListener(v -> {

            if (nama.getText().toString().equals("")) {
                nama.setError("Nama kategori harus diisi");
            } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", nama.getText().toString())
                            .addFormDataPart("business_id", loginBusiness.id)
                            .build();

                String token = "Bearer "+ PreferenceManager.getSessionTokenMireta();
                ApiLocal.apiInterface().postCategoriesInCreateItem(requestBody, token).enqueue(new Callback<ApiResponse<CategoriesResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CategoriesResponse>> call, Response<ApiResponse<CategoriesResponse>> response) {
                        try {
                            Log.d("TAG RESPONSE KATEGORI", response.message());
                            if (response.isSuccessful()){
                                CategoriesResponse categoriesResponse = response.body().getData();
                                kategoriesId = categoriesResponse.getId()+"";
                                subKategoriesId = null;
                                String name = categoriesResponse.getName();
                                Toast.makeText(context, "Berhasil tambah kategori", Toast.LENGTH_SHORT).show();
                                etKategori.setText(name);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CategoriesResponse>> call, Throwable t) {
                        Log.d("TAG FAILURE KATEGORI", t.getMessage());
                    }
                });
            }
        });
    }

    @OnClick(R.id.btnTambah)
    void createItem() {
        doCreateItem();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tambah_barang;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Barang");

        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        switchStok.setOnCheckedChangeListener((buttonView, isChecked) -> isDaily = ""+isChecked);
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

    @OnClick(R.id.imgBarang)
    void onClickImage() {
        selectImage();
    }

    @SuppressLint("LongLogTag")
    private void createStok(String id) {

        Loading.show(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location_id", loginStockLocation.id)
                .addFormDataPart("item_id", id)
                .addFormDataPart("qty", etStokAwal.getText().toString())
                .build();

        ApiLocal.apiInterface().createStock(requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Log.d("TAG Create Stok", response.message());
                        Toast.makeText(context, "Berhasil tambah barang", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AkunActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Gagal tambah stok", Toast.LENGTH_SHORT).show();
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

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            } else if (options[item].equals("Choose From Gallery")) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }

        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoImage = (Bitmap) data.getExtras().get("data");
            photoImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imgBarang.setImageBitmap(photoImage);
            try {
                File outputDir = getCacheDir();
                imageCheck = File.createTempFile("photo", "jpeg", outputDir);
                FileOutputStream outputStream = openFileOutput("photo.jpeg", Context.MODE_PRIVATE);
                outputStream.write(stream.toByteArray());
                outputStream.close();
                Log.d("Write File", "Success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Write File", "Failed2");
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imgBarang.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void settingKategoriSpinner(Spinner spinner, TextView tvNoData, String nestedId) {
        Loading.show(context);
        kategori = new ArrayList<>();
        kategoriId = new ArrayList<>();

        ApiLocal.apiInterface().getListCategory(loginBusiness.id, nestedId, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryModel>>> call, Response<ApiResponse<List<CategoryModel>>> response) {
                Loading.hide(context);
                try {
                    List<CategoryModel> res = response.body().getData();
                    if (res.size() < 1) {
                        spinner.setVisibility(View.GONE);
                        spinnerSubKategori.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        kategori.add("Pilih Kategori");
                        kategoriId.add("KI");
                        for (int i = 0; i < res.size(); i++) {
                            CategoryModel category = res.get(i);
                            kategori.add(category.getName());
                            kategoriId.add(category.getId());
                        }

                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TambahBarangActivity.this, R.layout.layout_spinner_text, kategori) {
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

        ApiLocal.apiInterface().getListCategory(loginBusiness.id, kategoriId.get(position), "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<CategoryModel>>>() {
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TambahBarangActivity.this, R.layout.layout_spinner_text, subKategori) {
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

    private void doCreateItem(){
        if (etNamaBarang.getText().toString().equals("") ||
                etHargaPieces.getText().toString().equals("") || kategoriesId == null) {
            Toast.makeText(context, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
        } else {
//            List<Item.NewStocks> newStocks = new ArrayList<>();
//            newStocks.add(new Item.NewStocks(loginStockLocation.id, etKodeBarang.getText().toString(), etJumlahPieces.getText().toString()));
//
//            Log.d("TAG Kategori", kategoriesId);
//            Item item = null;
//            if (subKategoriesId != null) {
//                item = new Item(etNamaBarang.getText().toString(),
//                        etDeskripsi.getText().toString(),
//                        loginBusiness.id, loginStockLocation.brand_id, etHargaPieces.getText().toString(),
//                        subKategoriesId, isDaily, newStocks);
//            } else {
//                item = new Item(etNamaBarang.getText().toString(),
//                        etDeskripsi.getText().toString(),
//                        loginBusiness.id, loginStockLocation.brand_id, etHargaPieces.getText().toString(),
//                        kategoriesId, isDaily, newStocks);
//            }
            Loading.show(context);
            RequestBody requestBody;

            if (photoImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                photoImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                if (subKategoriesId != null) {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", subKategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                            .build();
                } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", kategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .addFormDataPart("image", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                            .build();
                }
            } else if (selectedImage != null) {
                File file = createTempFile(selectedImage);
                if (subKategoriesId != null) {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", subKategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                            .build();
                } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", kategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                            .build();
                }
            } else {
                if (subKategoriesId != null) {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", subKategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .build();
                } else {
                    requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("code", etKodeBarang.getText().toString())
                            .addFormDataPart("brand_id", loginStockLocation.brand_id)
                            .addFormDataPart("business_id", loginBusiness.id)
                            .addFormDataPart("category_id", kategoriesId)
                            .addFormDataPart("name", etNamaBarang.getText().toString())
                            .addFormDataPart("description", etDeskripsi.getText().toString())
                            .addFormDataPart("price", etHargaPieces.getText().toString())
                            .build();
                }
            }

            ApiLocal.apiInterface().createItem(requestBody, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<ItemResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ItemResponse>> call, Response<ApiResponse<ItemResponse>> response) {
                    try {
                        Loading.hide(context);
                        if (response.isSuccessful()){
                            if (etStokAwal.getText().toString().equals("")){
                                Toast.makeText(context, "Berhasil tambah barang", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, AkunActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                ItemResponse itemResponse = Objects.requireNonNull(response.body()).getData();
                                createStok(itemResponse.getId());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ItemResponse>> call, Throwable t) {
                    Loading.hide(context);
                    Log.d("TAG", t.getMessage());
                }
            });
        }
    }

    private void addImageBarang(String id) {
        Loading.show(context);
        if (photoImage != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Log.d("TAG MASUK CAMERA", String.valueOf(photoImage));
            photoImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
//            File file = createTempFile(selectedImage);
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("_method", "put")
                    .addFormDataPart("item_images[]", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                    .build();

        } else if (selectedImage != null) {
            File file = createTempFile(selectedImage);
            Log.d("TAG MASUK GALLERY", String.valueOf(file));
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("_method", "put")
                    .addFormDataPart("item_images[]", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                    .build();

        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("_method", "put")
                    .build();
        }
        String token = "Bearer " + PreferenceManager.getSessionTokenMireta();
        ApiLocal.apiInterface().postImageItem(id, requestBody, token).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                Log.d("TAG UPLOAD IMAGE", response.message());
                Toast.makeText(context, "Berhasil tambah barang", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TambahBarangActivity.this, StokActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.WEBP, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(TambahBarangActivity.this));
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

    private void showDialogKategori() {
        dialog = new Dialog(Objects.requireNonNull(TambahBarangActivity.this));
        //set content
        dialog.setContentView(R.layout.dialog_kategori);
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
