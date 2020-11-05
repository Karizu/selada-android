package com.boardinglabs.mireta.selada.modul.master.katalog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.TambahBarangActivity;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.KatalogModel;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KatalogActivity extends BaseActivity {

    private List<KatalogModel> katalogModels;
    private List<String> itemImages;
    private Context context;
    private KatalogAdapter adapter;
    private static final int CAMERA_REQUEST_CODE = 1;
    private final int REQEUST_CAMERA = 1, REQUEST_GALLERY = 2;
    private Bitmap selectedImage;
    private Bitmap photoImage;
    public String location_id, business_id;

    @BindView(R.id.btnTambah)
    LinearLayout btnTambah;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.header_view)
    LinearLayout header_view;
    @BindView(R.id.etSearch)
    EditText etSearch;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_katalog;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("KATALOG");

        context = this;

        location_id = loginStockLocation.id;
        business_id = loginBusiness.id;

        katalogModels = new ArrayList<>();
        itemImages = new ArrayList<>();
        setListKatalog();

        swipeRefresh.setOnRefreshListener(() -> {
            katalogModels.clear();
            adapter.notifyDataSetChanged();
            setListKatalog();
        });

        btnTambah.setOnClickListener(v -> {
            startActivity(new Intent(KatalogActivity.this, TambahBarangActivity.class));
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

    public void selectImage() {
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
            Toast.makeText(context, "Please click button refresh", Toast.LENGTH_LONG).show();
            try {
                File outputDir = getCacheDir();
                File imageCheck = File.createTempFile("photo", "jpeg", outputDir);
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
                Toast.makeText(context, "Please click button refresh", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap getBitmapCamera(){
        return photoImage;
    }

    public Bitmap getBitmapGallery(){
        return selectedImage;
    }

    @OnClick(R.id.imgClose)
    void onCLickClose(){
        etSearch.setText("");
        header_view.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgSearch)
    void onClickSearch(){
        header_view.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<KatalogModel> newWorker = new ArrayList<>();
                String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                for (KatalogModel user : katalogModels) {
                    if (user.getName().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(user);
                    }
                }
                if (newWorker.size() >= 1){
                    adapter.updateData(newWorker);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setListKatalog(){
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getListKatalog(loginBusiness.id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<ItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ItemResponse>>> call, Response<ApiResponse<List<ItemResponse>>> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    List<ItemResponse> res = response.body().getData();
                    if (res.size() < 1){
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++){

                        ItemResponse katalog = res.get(i);

                        katalogModels.add(new KatalogModel(katalog.getId()+"",
                                katalog.getImage(),
                                katalog.getName(),
                                katalog.getDescription(),
                                katalog.getPrice(),
                                "0",
                                "false",
                                "",
                                katalog.getCreatedAt(),
                                katalog.getCategory().getName()));

                    }

                    adapter = new KatalogAdapter(katalogModels, context, itemImages, loginStockLocation.id);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ItemResponse>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
