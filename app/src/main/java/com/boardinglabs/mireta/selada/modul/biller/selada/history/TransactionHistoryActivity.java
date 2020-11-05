package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.adapter.TransactionHistoryAdapter;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.util.EndlessRecyclerViewScrollListener;
import com.boardinglabs.mireta.selada.modul.CommonInterface;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity  extends BaseActivity implements CommonInterface,TransactionHistoryView {

    public static String MERCHANT_ID = "MERCHANT_ID";

    private int limit;
    private int offset;
    private Activity activity;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<GSeladaTransaction> transactions;
    private TransactionHistoryAdapter adapter;

    RecyclerView recyclerView;
    TextView tvNoData;
    SwipeRefreshLayout refreshLayout;

    private TransactionHistoryPresenter transactionHistoryPresenter;

    String merchantId;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_transaction_history;
    }

    @Override
    protected void setContentViewOnChild() {
        setSeladaToolbarTitle("History Transaksi");
        activity = this;
        transactions = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.pullToRefresh);

        //                refreshLayout.setRefreshing(false);
        refreshLayout.setOnRefreshListener(this::getTransactionHistory);
        refreshLayout.setColorSchemeResources(R.color.primaryAccentBlue);

        adapter = new TransactionHistoryAdapter(transactions, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
//        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
//            }
//        };
        // Adds the scroll listener to RecyclerView
//        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(adapter);


        Log.d("MERCHANT_ID", merchant.getId().toString());
    }

    @Override
    protected void onCreateAtChild() {
        merchantId = getIntent().getStringExtra(MERCHANT_ID);
        transactionHistoryPresenter = new TransactionHistoryPresenterImpl(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getTransactionHistory();
        refreshLayout.setRefreshing(true);
    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    public void getTransactionHistory() {
        transactionHistoryPresenter.transactionHistory(merchant.getId().toString());
    }

    @Override
    public void showProgressLoading() {
//        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
//        progressBar.getDialog().dismiss();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getSeladaCoreInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessGetHistoryTransaction(List<GSeladaTransaction> transaction) {
        transactions.clear();
        refreshLayout.setRefreshing(false);
        adapter.updateData(transaction);
    }

    public void loadNextDataFromApi(int offset) {
//        transactionHistoryPresenter.transactionHistory(limit, limit*offset, merchant.getId().toString());
    }

    @Override
    public void onSuccessGetDetailTransaction(GSeladaTransaction transaction) {
//        this.transaction = transaction;
////        tvMerchantName = findViewById(R.id.tvMerchantNames);
////        tvTotalBayar = findViewById(R.id.tvTotalBayar);
////        tvStatusTransaksi = findViewById(R.id.tvStatusTransaksi);
////        tvTanggal = findViewById(R.id.tvTanggal);
////        tvWaktu = findViewById(R.id.tvWaktu);
////        tvTransactionInfo = findViewById(R.id.tvTransactionInfo);
//        String date = "-";
//        String time = "-";
//        try {
//            Date d = null, d2 = null;
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            d = sdf.parse(transaction.updated_at);
//            sdf.applyPattern("dd MMM yyyy");
//            date = sdf.format(d);
//            d2 = sdf2.parse(transaction.updated_at);
//            sdf2.applyPattern("HH:mm aa");
//            time = sdf2.format(d2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int status = transaction.status;
//
//
//        tvMerchantName.setText(transaction.merchant.name);
//
//        switch (status) {
//            case 0:
//                tvStatusTransaksi.setText("PENDING");
//                tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Yellow));
//                btnPrintStruk.setVisibility(View.VISIBLE);
//                break;
//            case 1:
//                tvStatusTransaksi.setText("BERHASIL");
//                tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Green));
//                btnPrintStruk.setVisibility(View.VISIBLE);
//            case 2:
//                tvStatusTransaksi.setText("GAGAL");
//                tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Red));
//                btnPrintStruk.setVisibility(View.GONE);
//                break;
//        }
//        tvTotalBayar.setText("Rp " + MethodUtil.toCurrencyFormat(transaction.price));
//        tvTanggal.setText(date);
//        tvWaktu.setText(time);
//        tvType.setText(transaction.service.product.provider.category.name);
//        tvTransactionInfo.setText(transaction.service.product.name + " ("+ transaction.merchant_no +")");

    }
}

//    private List<HistoryTopup> historyTopups;
//    private Context context;
//    private long mTotal;
//    private HistoryTopupAdapter adapter;
//    private DatePickerDialog datePickerDialog;
//    private Calendar newCalendar;
//    private int flag;
//
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;
//    @BindView(R.id.tvTotalTopup)
//    TextView tvTotalTopup;
//    @BindView(R.id.tvNoData)
//    TextView tvNoData;
//    @BindView(R.id.pullToRefresh)
//    SwipeRefreshLayout refreshLayout;
//    private String date;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history_topup2);
//        ButterKnife.bind(this);
//        context = this;
//
//        historyTopups = new ArrayList<>();
//        getListTopup();
//
//        refreshLayout.setOnRefreshListener(() -> {
////            mTotal = 0;
////            adapter.notifyDataSetChanged();
////            if (flag == 1){
////                getListTopupWithDate(date);
////            } else {
////                getListTopup();
////            }
//            getListTopup();
//        });
//
//    }
//
//    private void getListTopup(){
//        refreshLayout.setRefreshing(true);
//        historyTopups.clear();
//        Api.apiInterface().getListTopupSelada("7a180792-ea58-40bb-aad1-9d39c1b3c3e6", "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<HistoryTopup>>>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(Call<ApiResponse<List<HistoryTopup>>> call, Response<ApiResponse<List<HistoryTopup>>> response) {
//                refreshLayout.setRefreshing(false);
//                try {
//                    List<HistoryTopup> res = response.body().getData();
//                    historyTopups.addAll(res);
//                    for (int i = 0; i < res.size(); i++){
//                        HistoryTopup historyTopup = res.get(i);
//                        mTotal += historyTopup.getAmount();
//                    }
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                tvTotalTopup.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal));
//                adapter = new HistoryTopupAdapter(historyTopups, context);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
//                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                        layoutManager.getOrientation());
//                recyclerView.addItemDecoration(dividerItemDecoration);
//                recyclerView.setLayoutManager(layoutManager);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<HistoryTopup>>> call, Throwable t) {
//                refreshLayout.setRefreshing(false);
//                t.printStackTrace();
//            }
//        });
//    }

