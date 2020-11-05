package com.boardinglabs.mireta.selada.modul.master.laporan;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.laporan.adapter.LaporanStockAdapter;
import com.boardinglabs.mireta.selada.modul.master.laporan.model.LaporanModel;
import com.boardinglabs.mireta.selada.modul.master.laporan.model.LaporanResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanStock extends BaseActivity {

    private List<LaporanModel> katalogModels;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_laporan_stock;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("LAPORAN STOCK BARANG");

        setData();

        swipeRefresh.setOnRefreshListener(() -> {
            katalogModels.clear();
            setData();
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

    private void setData(){
        swipeRefresh.setRefreshing(true);
        katalogModels = new ArrayList<>();

        ApiLocal.apiInterface().getLaporanStock(loginStockLocation.id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<LaporanResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LaporanResponse>>> call, Response<ApiResponse<List<LaporanResponse>>> response) {
                swipeRefresh.setRefreshing(false);

                try {
                    List<LaporanResponse> res = response.body().getData();

                    if (res.size() < 1){
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++){
                        LaporanResponse laporanResponse = res.get(i);

                        if (laporanResponse.getTotal_today_qty() != null){
                            katalogModels.add(new LaporanModel(laporanResponse.getName(),
                                    laporanResponse.getTotal_today_qty().getTotal_daily_qty(),
                                    laporanResponse.getQtyOut()));
                        } else {
                            katalogModels.add(new LaporanModel(laporanResponse.getName(),
                                    "0",
                                    laporanResponse.getQtyOut()));
                        }
                    }

                    LaporanStockAdapter adapter = new LaporanStockAdapter(katalogModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<List<LaporanResponse>>> call, Throwable t) {
                t.printStackTrace();
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
