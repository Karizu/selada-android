package com.boardinglabs.mireta.selada.modul.history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.selada.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Scanner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<TransactionModel> transactionModels;
    private TransactionAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private long mTotal;
    private Dialog dialog;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER = 999;
    private String pathAll, pathHistory, pathStatus, pathSettle, dateToday, dateMonth, dateYear;

    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.tvTotalPenjualan)
    TextView tvTotalPenjualan;
    @BindView(R.id.imgFilter)
    ImageView imgFilter;
    @BindView(R.id.imgScan)
    ImageView imgScan;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    private String checkedStatusFlag = "";
    private String checkedTransaksiFlag = "";
    private String checkedSettledFlag = "";

    @OnClick(R.id.imgScan)
    void onClickImgScan() {
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(HistoryActivity.this), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(HistoryActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            Intent intent = new Intent(HistoryActivity.this, Scanner.class);
            startActivityForResult(intent, REQUEST_SCANNER);
        }
    }

    @OnClick(R.id.imgClose)
    void onClickClosed() {
        etSearch.setText("");
        item_name.setVisibility(View.VISIBLE);
        imgSearch.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgSearch)
    void onClickSearchs() {
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    List<TransactionModel> newWorker = new ArrayList<>();
                    String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                    for (TransactionModel user : transactionModels) {
                        if (user.getOrder_no().toLowerCase().contains(newTextLowerCase)) {
                            newWorker.add(user);
                        }
                    }
                    if (newWorker.size() >= 1) {
                        tvNoData.setVisibility(View.GONE);
                        adapter.updateData(newWorker);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                        adapter.updateData(newWorker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_history;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("History");
        imgFilter.setVisibility(View.VISIBLE);
        imgScan.setVisibility(View.VISIBLE);
        transactionModels = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.pullToRefresh);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        dateToday = s.format(new Date());
        Log.d("DATE TODAY", dateToday);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s2 = new SimpleDateFormat("MM");
        dateMonth = s2.format(new Date());
        Log.d("DATE MONTH", dateMonth);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s3 = new SimpleDateFormat("yyyy");
        dateYear = s3.format(new Date());
        Log.d("DATE YEAR", dateYear);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("tag") != null) {
                refreshLayout.setOnRefreshListener(() -> {
                    mTotal = 0;
                    transactionModels.clear();
                    adapter.notifyDataSetChanged();
                    getListHistory();
                });
                getListHistory();
            } else {
                refreshLayout.setOnRefreshListener(() -> {
                    mTotal = 0;
                    transactionModels.clear();
                    adapter.notifyDataSetChanged();
                    getListHistoryToday();
                });
                item_name.setText("History Transaksi Hari ini");
                getListHistoryToday();
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCANNER && resultCode == Activity.RESULT_OK) {
            String resultData = data.getStringExtra("scan_data");
            Log.d("Data Scan", resultData);
            etSearch.setText(resultData);
            try {
                List<TransactionModel> newWorker = new ArrayList<>();
                String newTextLowerCase = resultData.toLowerCase();
                for (TransactionModel user : transactionModels) {
                    if (user.getOrder_no().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(user);
                    }
                }
                if (newWorker.size() >= 1) {
                    tvNoData.setVisibility(View.GONE);
                    adapter.updateData(newWorker);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    adapter.updateData(newWorker);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.imgFilter)
    void onClickImgFilter() {
        showDialog();

        pathHistory = ""; pathStatus = ""; pathSettle = "";

        CheckedTextView ctvAll = dialog.findViewById(R.id.ctvAll);
        CheckedTextView ctvToday = dialog.findViewById(R.id.ctvToday);
        CheckedTextView ctvMonth = dialog.findViewById(R.id.ctvMonth);
        CheckedTextView ctvStatusAll = dialog.findViewById(R.id.ctvStatusAll);
        CheckedTextView ctvStatusSukses = dialog.findViewById(R.id.ctvStatusSukses);
        CheckedTextView ctvStatusPending = dialog.findViewById(R.id.ctvStatusPending);
        CheckedTextView ctvStatusBatal = dialog.findViewById(R.id.ctvStatusBatal);
        CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
        CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
        CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);

        if (checkedTransaksiFlag.equals("ctvAll")){
            ctvAll.setChecked(true);
            ctvToday.setChecked(false);
            ctvMonth.setChecked(false);
            checkedTransaksiFlag = "ctvAll";
            pathHistory = "";
        }

        if (checkedTransaksiFlag.equals("ctvToday")){
            ctvAll.setChecked(false);
            ctvToday.setChecked(true);
            ctvMonth.setChecked(false);
            checkedTransaksiFlag = "ctvToday";
            pathHistory = "";
        }

        if (checkedTransaksiFlag.equals("ctvMonth")){
            ctvAll.setChecked(false);
            ctvToday.setChecked(false);
            ctvMonth.setChecked(true);
            checkedTransaksiFlag = "ctvMonth";
            pathHistory = "month=" + dateMonth + "&year=" + dateYear + "&";
        }

        if (checkedStatusFlag.equals("ctvStatusAll")){
            ctvStatusAll.setChecked(true);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusAll";
            pathStatus = "";
        }

        if (checkedStatusFlag.equals("ctvStatusSukses")){
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(true);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusSukses";
            pathStatus = "status=2&";
        }

        if (checkedStatusFlag.equals("ctvStatusPending")){
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(true);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusPending";
            pathStatus = "status=1&";
        }

        if (checkedStatusFlag.equals("ctvStatusBatal")){
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(true);
            checkedStatusFlag = "ctvStatusBatal";
            pathStatus = "status=3&";
        }

        if (checkedSettledFlag.equals("ctvSettleAll")){
            ctvSettleAll.setChecked(true);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(false);
            checkedSettledFlag = "ctvSettleAll";
            pathSettle = "";
        }

        if (checkedSettledFlag.equals("ctvSettleTrue")){
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(true);
            ctvSettleFalse.setChecked(false);
            checkedSettledFlag = "ctvSettleTrue";
            pathSettle = "is_settled=1&";
        }

        if (checkedSettledFlag.equals("ctvSettleFalse")){
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(true);
            checkedSettledFlag = "ctvSettleFalse";
            pathSettle = "is_settled=0&";
        }

        ctvAll.setOnClickListener(v -> {
            ctvAll.setChecked(true);
            ctvToday.setChecked(false);
            ctvMonth.setChecked(false);
            checkedTransaksiFlag = "ctvAll";
            pathHistory = "";
        });

        ctvToday.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvToday.setChecked(true);
            ctvMonth.setChecked(false);
            checkedTransaksiFlag = "ctvToday";
            pathHistory = "date=" + dateToday + "&";
        });

        ctvMonth.setOnClickListener(v -> {
            ctvAll.setChecked(false);
            ctvToday.setChecked(false);
            ctvMonth.setChecked(true);
            checkedTransaksiFlag = "ctvMonth";
            pathHistory = "month=" + dateMonth + "&year=" + dateYear + "&";
        });

        ctvStatusAll.setOnClickListener(v -> {
            ctvStatusAll.setChecked(true);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusAll";
            pathStatus = "";
        });

        ctvStatusSukses.setOnClickListener(v -> {
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(true);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusSukses";
            pathStatus = "status=2&";
        });

        ctvStatusPending.setOnClickListener(v -> {
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(true);
            ctvStatusBatal.setChecked(false);
            checkedStatusFlag = "ctvStatusPending";
            pathStatus = "status=1&";
        });

        ctvStatusBatal.setOnClickListener(v -> {
            ctvStatusAll.setChecked(false);
            ctvStatusSukses.setChecked(false);
            ctvStatusPending.setChecked(false);
            ctvStatusBatal.setChecked(true);
            checkedStatusFlag = "ctvStatusBatal";
            pathStatus = "status=3&";
        });

        ctvSettleAll.setOnClickListener(v -> {
            ctvSettleAll.setChecked(true);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(false);
            checkedSettledFlag = "ctvSettleAll";
            pathSettle = "";
        });

        ctvSettleTrue.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(true);
            ctvSettleFalse.setChecked(false);
            checkedSettledFlag = "ctvSettleTrue";
            pathSettle = "is_settled=1&";
        });

        ctvSettleFalse.setOnClickListener(v -> {
            ctvSettleAll.setChecked(false);
            ctvSettleTrue.setChecked(false);
            ctvSettleFalse.setChecked(true);
            checkedSettledFlag = "ctvSettleFalse";
            pathSettle = "is_settled=0&";
        });

        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> {
            dialog.dismiss();
            pathAll = "transactions?" + pathHistory + pathStatus + pathSettle;
            Log.d("PATH", pathAll);
            mTotal = 0;
            transactionModels.clear();
            adapter.notifyDataSetChanged();
            getHistoryByFilter(pathAll);
            refreshLayout.setOnRefreshListener(() -> {
                mTotal = 0;
                transactionModels.clear();
                adapter.notifyDataSetChanged();
                getHistoryByFilter(pathAll);
            });
            item_name.setText("History Transaksi");
        });

        Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
        btnResetFilter.setOnClickListener(v -> {
            dialog.dismiss();
            mTotal = 0;
            checkedTransaksiFlag = "";
            checkedStatusFlag = "";
            checkedSettledFlag = "";
            transactionModels.clear();
            adapter.notifyDataSetChanged();
            item_name.setText("History Transaksi Hari ini");
            getListHistoryToday();
        });

//        LinearLayout btnAllHistory = dialog.findViewById(R.id.history_all);
//        btnAllHistory.setOnClickListener(v -> {
//            mTotal = 0;
//            transactionModels.clear();
//            getListHistory();
//
//            refreshLayout.setOnRefreshListener(() -> {
//                mTotal = 0;
//                transactionModels.clear();
//                getListHistory();
//            });
//            item_name.setText("History Transaksi");
//            dialog.dismiss();
//        });
//
//        LinearLayout btnTodayHistory = dialog.findViewById(R.id.history_today);
//        btnTodayHistory.setOnClickListener(v -> {
//            mTotal = 0;
//            transactionModels.clear();
//            getListHistoryToday();
//            refreshLayout.setOnRefreshListener(() -> {
//                mTotal = 0;
//                transactionModels.clear();
//                getListHistoryToday();
//            });
//            item_name.setText("History Transaksi Hari ini");
//            dialog.dismiss();
//        });

//        LinearLayout btnBerhasil = dialog.findViewById(R.id.trxBerhasil);
//        btnBerhasil.setOnClickListener(v -> {
//            refreshLayout.setRefreshing(true);
//            List<TransactionModel> newWorker = new ArrayList<>();
//            String newTextLowerCase = "2";
//            for (TransactionModel user : transactionModels) {
//                if (user.getStatus().toLowerCase().contains(newTextLowerCase)) {
//                    newWorker.add(user);
//                }
//            }
//            if (newWorker.size() >= 1){
//                refreshLayout.setRefreshing(false);
//                adapter.updateData(newWorker);
//                dialog.dismiss();
//            }
//        });
//
//        LinearLayout btnPending = dialog.findViewById(R.id.trxPending);
//        btnPending.setOnClickListener(v -> {
//            refreshLayout.setRefreshing(true);
//            List<TransactionModel> newWorker = new ArrayList<>();
//            String newTextLowerCase = "1";
//            for (TransactionModel user : transactionModels) {
//                if (user.getStatus().toLowerCase().contains(newTextLowerCase)) {
//                    newWorker.add(user);
//                }
//            }
//            if (newWorker.size() >= 1){
//                refreshLayout.setRefreshing(false);
//                adapter.updateData(newWorker);
//                dialog.dismiss();
//            }
//        });
    }

    private void getHistoryByFilter(String path) {
        refreshLayout.setRefreshing(true);
        ApiLocal.apiInterface().getHistoryWithFilter(path, loginStockLocation.id, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod() + "",
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        mTotal += Long.parseLong(transaction.getTotalPrice());

                    }

                    Log.d("mTotalDay", mTotal + "");

                    tvTotalPenjualan.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void getListHistoryToday() {
        refreshLayout.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        ApiLocal.apiInterface().getHistoryToday(loginStockLocation.id, null, format1, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod() + "",
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus() == 2) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotalDay", mTotal + "");

                    tvTotalPenjualan.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Log.d("TAG", t.getMessage());
            }
        });
    }

    private void getListHistory() {
        refreshLayout.setRefreshing(true);
        ApiLocal.apiInterface().getHistory(loginStockLocation.id, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                refreshLayout.setRefreshing(false);
                try {
                    List<Transactions> res = Objects.requireNonNull(response.body()).getData();
                    if (res.size() < 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < res.size(); i++) {
                        Transactions transaction = res.get(i);
                        transactionModels.add(new TransactionModel(transaction.getId() + "",
                                transaction.getLocationId(),
                                transaction.getId() + "",
                                transaction.getTransactionCode(),
                                transaction.getCreatedAt(),
                                transaction.getPaymentType() + "",
                                transaction.getPaymentMethod() + "",
                                transaction.getTotalPrice(),
                                transaction.getTotalDiscount(),
                                transaction.getStatus() + ""));

                        if (transaction.getStatus().equals(2)) {
                            mTotal += Long.parseLong(transaction.getTotalPrice());
                        }
                    }

                    Log.d("mTotal", mTotal + "");

                    tvTotalPenjualan.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(mTotal)));

                    adapter = new TransactionAdapter(transactionModels, context);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(HistoryActivity.this));
        //set content
        dialog.setContentView(R.layout.layout_filter_history);
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
