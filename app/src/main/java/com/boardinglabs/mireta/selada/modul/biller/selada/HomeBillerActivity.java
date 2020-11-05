package com.boardinglabs.mireta.selada.modul.biller.selada;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiSelada;
import com.boardinglabs.mireta.selada.component.network.entities.Items.Category;
import com.boardinglabs.mireta.selada.component.network.entities.Merchant;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.TransactionHistoryActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeBillerActivity extends AppCompatActivity {

    private Activity context;
    private Merchant merchant;
    private HomeBillerAdapter adapter;
    private List<Category> newCategories;

    @BindView(R.id.textViewTID)
    TextView textViewTID;
    @BindView(R.id.textViewMID)
    TextView textViewMID;
    @BindView(R.id.textView4)
    TextView textViewMerchantName;
    @BindView(R.id.textViewMName)
    TextView textViewAlamat;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout pullRefresh;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.menuInfo)
    void onClickTopup() {
        HashMap<String, String> data = new HashMap<>();
        data.put("menu", "MA00065");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @OnClick(R.id.menuTopupHistory)
    void onClickTopupHistory() {
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        intent.putExtra(TransactionHistoryActivity.MERCHANT_ID, merchant.getId().toString());
        startActivity(intent);
    }

    @OnClick(R.id.menuSetting)
    void onClickSettings() {
        Intent intent = new Intent(this, PengaturanAkunActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        ButterKnife.bind(this);
        context = this;
        merchant = PreferenceManager.getMerchant();
        newCategories = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        pullRefresh.setOnRefreshListener(() -> pullRefresh.setRefreshing(false));
        pullRefresh.setColorSchemeResources(R.color.primaryAccentBlue);

        getMenuFromCache();

        try {
            String TID = PreferenceManager.getTID() != null ? PreferenceManager.getTID() : "13050199";
            String MID = PreferenceManager.getMID() != null ? PreferenceManager.getMID() : "000063708571";

            textViewTID.setText("TID: " + TID);
            textViewMID.setText("MID: " + MID);
            textViewMerchantName.setText(PreferenceManager.getMerchantName());
            textViewAlamat.setText(PreferenceManager.getMerchantAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMenuFromCache() {
        boolean isMenuAvailable = PreferenceManager.getMenu() != null;
        if (isMenuAvailable) {
            List<Category> categories = PreferenceManager.getMenu();
            adapter = new HomeBillerAdapter(categories, context);
            recyclerView.setAdapter(adapter);
        } else {
            getMenuFromServer();
        }
    }


    private void getMenuFromServer() {
        pullRefresh.setRefreshing(true);
        ApiSelada.apiInterface().getListCategory("Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                pullRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        List<Category> categories = Objects.requireNonNull(response.body()).getData();
                        for (int i = 0; i < categories.size(); i++) {
                            for (int j = 0; j < categories.size(); j++) {
                                Category category = categories.get(j);
                                if (Integer.parseInt(category.getSequence()) == i) {
                                    newCategories.add(category);
                                }
                            }
                        }

                        PreferenceManager.setMenu(newCategories);
                        adapter = new HomeBillerAdapter(newCategories, context);
                        recyclerView.setAdapter(adapter);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                pullRefresh.setRefreshing(false);
                t.printStackTrace();
            }
        });

    }
}
