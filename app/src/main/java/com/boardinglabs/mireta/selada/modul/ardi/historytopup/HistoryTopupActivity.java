package com.boardinglabs.mireta.selada.modul.ardi.historytopup;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.HistoryTopup.HistoryTopup;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.ardi.historytopup.adapter.HistoryTopupAdapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTopupActivity extends BaseActivity {

    private List<HistoryTopup> historyTopups;
    private Context context;
    private long mTotal;
    private HistoryTopupAdapter adapter;
    private DatePickerDialog datePickerDialog;
    private Calendar newCalendar;
    private int flag;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvTotalTopup)
    TextView tvTotalTopup;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout refreshLayout;
    private String date;

    @OnClick(R.id.imgFilter)
    void onClickFilter(){
        newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            flag = 1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            date = f.format(newDate.getTime());
            getListTopupWithDate(f.format(newDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_history_topup;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("History Topup");
        context = this;

        historyTopups = new ArrayList<>();
        getListTopup();

        refreshLayout.setOnRefreshListener(() -> {
            mTotal = 0;
            adapter.notifyDataSetChanged();
            if (flag == 1){
                getListTopupWithDate(date);
            } else {
                getListTopup();
            }
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

    private void getListTopup(){
        refreshLayout.setRefreshing(true);
        historyTopups.clear();
        Api.apiInterface().getListTopup(PreferenceManager.getBoothId(), "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<HistoryTopup>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<HistoryTopup>>> call, Response<ApiResponse<List<HistoryTopup>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<HistoryTopup> res = response.body().getData();
                    historyTopups.addAll(res);
                    for (int i = 0; i < res.size(); i++){
                        HistoryTopup historyTopup = res.get(i);
                        mTotal += historyTopup.getAmount();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

                tvTotalTopup.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal));
                adapter = new HistoryTopupAdapter(historyTopups, context);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                        layoutManager.getOrientation());
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<HistoryTopup>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void getListTopupWithDate(String date){
        refreshLayout.setRefreshing(true);
        historyTopups.clear();
        Api.apiInterface().getListTopupWithDate(PreferenceManager.getBoothId(), date, "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<HistoryTopup>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<HistoryTopup>>> call, Response<ApiResponse<List<HistoryTopup>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<HistoryTopup> res = response.body().getData();
                    historyTopups.addAll(res);
                    for (int i = 0; i < res.size(); i++){
                        HistoryTopup historyTopup = res.get(i);
                        mTotal += historyTopup.getAmount();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

                tvTotalTopup.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal));
                adapter = new HistoryTopupAdapter(historyTopups, context);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                        layoutManager.getOrientation());
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<HistoryTopup>>> call, Throwable t) {

            }
        });
    }
}
