package com.boardinglabs.mireta.selada.modul.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.adapter.RecyReportSummaryAdapter;
import com.boardinglabs.mireta.selada.component.adapter.RecyTransactionAdapter;
import com.boardinglabs.mireta.selada.component.adapter.TransactionAdapter;
import com.boardinglabs.mireta.selada.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.selada.component.listener.ListActionListener;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.Business;
import com.boardinglabs.mireta.selada.component.network.entities.ErrorParser;
import com.boardinglabs.mireta.selada.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.selada.component.network.entities.SummaryReport;
import com.boardinglabs.mireta.selada.component.network.entities.Transaction;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.akun.AkunActivity;
import com.boardinglabs.mireta.selada.modul.bjb.common.CommonConfig;
import com.boardinglabs.mireta.selada.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.selada.modul.master.laporan.LaporanActivity;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.ItemsActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.PembayaranActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.PembayaranSuksesActivity;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.paging.listview.PagingListView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements HomeView, CommonInterface, RecyReportSummaryAdapter.OnClickItem, ListActionListener {
    private HomePresenter mPresenter;

    protected TextView greetingName;
    protected TextView greetingWords;
        protected Button createTrxButton;
    protected Button historyButton;
    protected Button accountButton;
    protected Button masterButton;
    protected LinearLayout restock, setting, report;
    protected LinearLayout layoutTrx;
    protected ImageView restocks;

    protected RecyclerView reportRecyclerView;
    private RecyReportSummaryAdapter mAdapter;

    private List<Transaction> transactions;
    private PagingListView transactionsListView;
    private RecyTransactionAdapter transactionsAdapter;
    private List<TransactionModel> transactionModels;
    private TransactionAdapter adapter;
    private int currentPage;
    private int total_post;
    private ArrayList<SummaryReport> summaryList;
    long sum = 0;
    long sumMonth = 0;
    int amount, amountMonth;
    long rest, restMonth;
    private String json, stan;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.tvRecyEmpty)
    TextView recyclerViewEmpty;
    @BindView(R.id.greeting_name)
    RobotoBoldTextView greeting_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new HomePresenter(this, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        getPenjualanPendapatan();
        loadTransactionsData();
        getDetailProfil();
        Log.d("Token", PreferenceManager.getSessionToken());

        pullToRefresh.setOnRefreshListener(() -> {
            transactionModels.clear();
            getDetailProfil();
            loadTransactionsData();
            summaryList.clear();
            sum = 0;
            sumMonth = 0;
            getPenjualanPendapatan();
            pullToRefresh.setRefreshing(false);
        });

        Intent intent = getIntent();
        Log.d("HOME", "GET INTENT");

        if (intent.getExtras() != null) {
            Log.d("HOME", "GET EXTRA");
            if (intent.getExtras().getString("json") != null) {
                json = intent.getExtras().getString("json");
                stan = intent.getExtras().getString("stan");

                Log.d("HOME", "GET JSON = "+json);
                Log.d("HOME", "GET STAN = "+stan);

                Gson gson = new Gson();
                TransactionPost transactionPost = gson.fromJson(json, TransactionPost.class);
                transactionPost = new TransactionPost(transactionPost.location_id, transactionPost.transaction_code, transactionPost.total_qty, transactionPost.total_price+"", 1,2,2, stan, transactionPost.details);
                Log.d("TRANSACTION POST", gson.toJson(transactionPost));
                onClickBayarWithoutArdi(transactionPost);
            }
        }
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new HomePresenter(this, this);
    }

    @Override
    protected void onBackBtnPressed() {

    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @SuppressLint("SetTextI18n")
    private void initComponent(int amount, long rest, int amountMonth, long restMonth) {
        mPresenter = new HomePresenter(this, this);
        greetingWords = (TextView) findViewById(R.id.greeting_words);

//        createTrxButton = (Button) findViewById(R.id.create_trx_button);
//        historyButton = (Button) findViewById(R.id.history_button);
//        accountButton = (Button) findViewById(R.id.account_button);
//        masterButton = (Button) findViewById(R.id.master_button);

        restock = findViewById(R.id.restock);
        report = findViewById(R.id.report);
        setting = findViewById(R.id.setting);
        layoutTrx = findViewById(R.id.layoutTrx);
        restocks = findViewById(R.id.restocks);

//        RxView.clicks(restocks).subscribe(aVoid -> {
//            Intent intent = new Intent(HomeActivity.this, MasterActivity.class);
//            startActivity(intent);
//
//        });

        RxView.clicks(layoutTrx).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, ItemsActivity.class);
            startActivity(intent);

        });

        RxView.clicks(restock).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, StokActivity.class);
            startActivity(intent);

        });

        RxView.clicks(report).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, LaporanActivity.class);
            startActivity(intent);

        });

        RxView.clicks(setting).subscribe(aVoid -> {
            Intent intent = new Intent(HomeActivity.this, AkunActivity.class);
            startActivity(intent);

        });

//        RxView.clicks(createTrxButton).subscribe(aVoid -> {
//            Intent intent = new Intent(HomeActivity.this, ItemsActivity.class);
//            startActivity(intent);
//
//        });
//
//        RxView.clicks(historyButton).subscribe(aVoid -> startActivity(new Intent(HomeActivity.this, HistoryActivity.class)));
//
//        RxView.clicks(accountButton).subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
//            }
//        });
//
//        RxView.clicks(masterButton).subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                Intent intent = new Intent(HomeActivity.this, MasterActivity.class);
//                startActivity(intent);
//            }
//        });

        //Get the time of day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 10 && hour < 14) {
            greeting = "Selamat Siang";
        } else if (hour >= 14 && hour < 19) {
            greeting = "Selamat Sore";
        } else if (hour >= 19) {
            greeting = "Selamat Malam";
        } else {
            greeting = "Selamat Pagi";
        }
        greetingWords.setText(greeting);

        mAdapter = new RecyReportSummaryAdapter(this);
        reportRecyclerView = (RecyclerView) findViewById(R.id.report_recyclerView);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                horizontalLayoutManagaer.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        reportRecyclerView.setLayoutManager(horizontalLayoutManagaer);
        reportRecyclerView.setAdapter(mAdapter);

//        summaryList = new ArrayList();

//        SummaryReport summary1 = new SummaryReport();
//        SummaryReport summary2 = new SummaryReport();
//        SummaryReport summary3 = new SummaryReport();
//        SummaryReport summary4 = new SummaryReport();

//        Log.d("TAG Total", sum+ " "+sumMonth);

//        summary1.report_name = "Penjualan";
//        summary1.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amount + "");
//        summary1.report_period = "Hari ini";
//
//        summary3.report_name = "Pendapatan";
//        summary3.report_value = "Rp. "+MethodUtil.toCurrencyFormat(rest + "");
//        summary3.report_period = "Hari ini";

//        summary2.report_name = "Penjualan";
//        summary2.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amountMonth + "");
//        summary2.report_period = "Bulan ini";
//
//        summary4.report_name = "Pendapatan";
//        summary4.report_value = "Rp. "+MethodUtil.toCurrencyFormat(restMonth + "");
//        summary4.report_period = "Bulan ini";

//        summaryList.add(summary1);
//        summaryList.add(summary3);
//        summaryList.add(summary2);
//        summaryList.add(summary4);
//
//        mAdapter.setData(summaryList);
//
//        Log.d("TAG SUMMONTH", String.valueOf(sumMonth));

//        String amountToday = String.valueOf(sum);
//        String amountMonth = String.valueOf(sumMonth);
//        int totalPenjualanToday = Integer.parseInt(amountToday);
//        int totalPenjualanMonth = Integer.parseInt(amountMonth);

        total_post = 0;
        currentPage = 0;

//        transactionsAdapter = new RecyTransactionAdapter();
//        transactionsAdapter.setListener(this);
//
//        transactionsListView = (PagingListView) findViewById(R.id.today_transaction_list);
//        transactionsListView.setAdapter(transactionsAdapter);

//        transactionsListView.setPagingableListener(new PagingListView.Pagingable() {
//            @Override
//            public void onLoadMoreItems() {
//                transactionsListView.onFinishLoading(false, null);
//            }
//        });
//        transactionsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                if (transactionsListView.getChildAt(0) != null) {
//                    pullToRefresh.setEnabled(transactionsListView.getFirstVisiblePosition() == 0 && transactionsListView.getChildAt(0).getTop() == 0);
//                }
//            }
//        });
    }

    private void getDetailProfil() {
        pullToRefresh.setRefreshing(true);
        try {
            ApiLocal.apiInterface().getDetailLocation(loginStockLocation.id, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<DetailLocationResponse>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ApiResponse<DetailLocationResponse>> call, Response<ApiResponse<DetailLocationResponse>> response) {
                    pullToRefresh.setRefreshing(false);
                    try {
                        DetailLocationResponse res = Objects.requireNonNull(response.body()).getData();
                        Log.d("GREETING NAME", res.getName());
                        greeting_name.setText(res.getName());
                        Business business = new Business();
                        business.name = res.getName();
                        business.address = res.getAddress();
                        business.id = res.getBusinessId();
                        business.brand_id = res.getBrandId();

                        PreferenceManager.saveBusiness(business);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<DetailLocationResponse>> call, Throwable t) {
                    pullToRefresh.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPenjualanPendapatan() {
        summaryList = new ArrayList();
        pullToRefresh.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format = s1.format(new Date());
        try {
            ApiLocal.apiInterface().getLastTransaction("2", loginStockLocation.id, format, "Bearer "+ PreferenceManager.getSessionTokenMireta()).
                    enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                            pullToRefresh.setRefreshing(false);
                            try {
                                List<Transactions> res = response.body().getData();

                                for (int i = 0; i < res.size(); i++) {
                                    Transactions transaction = res.get(i);
                                    sum += Long.parseLong(transaction.getTotalPrice());
                                }

                                String mAmoun = String.valueOf(sum);
                                amount = Integer.parseInt(mAmoun);

                                long amounts = Long.parseLong(mAmoun);
//                            rest = (amounts / 100) * 90;
                                rest = amounts;

                                SummaryReport summary1 = new SummaryReport();
                                SummaryReport summary3 = new SummaryReport();

                                summary1.report_name = "Penjualan";
                                summary1.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amount + "");
                                summary1.report_period = "Hari ini";

                                summary3.report_name = "Pendapatan";
                                summary3.report_value = "Rp. " + MethodUtil.toCurrencyFormat(rest + "");
                                summary3.report_period = "Hari ini";

                                summaryList.add(summary1);
                                summaryList.add(summary3);

                                mAdapter.setData(summaryList);

                                Log.d("TAG SUM", amount + " " + rest);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                            pullToRefresh.setRefreshing(false);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }

        pullToRefresh.setRefreshing(true);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s11 = new SimpleDateFormat("MM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s2 = new SimpleDateFormat("yyyy");
        String month = s11.format(new Date());
        String yeear = s2.format(new Date());

        try {
            ApiLocal.apiInterface().getMonthTransaction("2", loginStockLocation.id, month, yeear, "Bearer "+ PreferenceManager.getSessionTokenMireta()).
                    enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                            pullToRefresh.setRefreshing(false);
                            try {
                                List<Transactions> res = response.body().getData();

                                for (int i = 0; i < res.size(); i++) {
                                    Transactions transaction = res.get(i);
                                    sumMonth += Long.parseLong(transaction.getTotalPrice());
                                }

                                String mAmoun = String.valueOf(sumMonth);
                                amountMonth = Integer.parseInt(mAmoun);

                                long amounts = Long.parseLong(mAmoun);
                                restMonth = amounts;
//                            restMonth = (amounts / 100) * 90;

                                SummaryReport summary2 = new SummaryReport();
                                SummaryReport summary4 = new SummaryReport();

                                summary2.report_name = "Penjualan";
                                summary2.report_value = "Rp. " + MethodUtil.toCurrencyFormat(amountMonth + "");
                                summary2.report_period = "Bulan ini";

                                summary4.report_name = "Pendapatan";
                                summary4.report_value = "Rp. " + MethodUtil.toCurrencyFormat(restMonth + "");
                                summary4.report_period = "Bulan ini";

                                summaryList.add(summary2);
                                summaryList.add(summary4);

                                mAdapter.setData(summaryList);

                                Log.d("TAG SUMMONTH", String.valueOf(sumMonth));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                            pullToRefresh.setRefreshing(false);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }

        Log.d("TAG amount", "" + amount + " " + rest + " " + amountMonth + " " + restMonth);
        initComponent(amount, rest, amountMonth, restMonth);

    }

    private void loadTransactionsData() {
        transactionModels = new ArrayList<>();
        currentPage = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        Log.d("TAG", format1);

        try {
            ApiLocal.apiInterface().getLastTransaction("2", loginStockLocation.id, format1, "Bearer "+ PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<List<Transactions>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Transactions>>> call, Response<ApiResponse<List<Transactions>>> response) {
                    Log.d("TAG OnResponse", response.message());
                    try {
                        List<Transactions> res = response.body().getData();
                        if (res.size() < 1) {
                            recyclerViewEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerViewEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
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
                            }

                            adapter = new TransactionAdapter(transactionModels, context);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    layoutManager.getOrientation());

                            recyclerView.addItemDecoration(dividerItemDecoration);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                            transactionsAdapter.setDataList(transactionModels, context);
                            transactionsListView.setHasMoreItems(false);
                            setListViewHeightBasedOnChildren(transactionsListView);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Transactions>>> call, Throwable t) {
                    Log.d("TAG OnFailure", t.getMessage());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

//        ArrayList<Transaction> dummyTransactions = new ArrayList<>();
//
//        Transaction transaction1 = new Transaction();
//        transaction1.transaction_code = "123456";
//        transaction1.stock_location = new StockLocation();
//        transaction1.stock_location.name = "Kantin RSHS Bandung";
//        transaction1.total_price = "Rp. 65.000";
//        transaction1.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction1);
//
//        Transaction transaction2 = new Transaction();
//        transaction2.transaction_code = "123456";
//        transaction2.stock_location = new StockLocation();
//        transaction2.stock_location.name = "Kantin RSHS Bandung";
//        transaction2.total_price = "Rp. 65.000";
//        transaction2.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction2);
//
//        Transaction transaction3 = new Transaction();
//        transaction3.transaction_code = "123456";
//        transaction3.stock_location = new StockLocation();
//        transaction3.stock_location.name = "Kantin RSHS Bandung";
//        transaction3.total_price = "Rp. 65.000";
//        transaction3.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction3);
//
//        Transaction transaction4 = new Transaction();
//        transaction4.transaction_code = "123456";
//        transaction4.stock_location = new StockLocation();
//        transaction4.stock_location.name = "Kantin RSHS Bandung";
//        transaction4.total_price = "Rp. 65.000";
//        transaction4.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction4);
//
//        Transaction transaction5 = new Transaction();
//        transaction5.transaction_code = "123456";
//        transaction5.stock_location = new StockLocation();
//        transaction5.stock_location.name = "Kantin RSHS Bandung";
//        transaction5.total_price = "Rp. 65.000";
//        transaction5.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction5);
//
//        Transaction transaction6 = new Transaction();
//        transaction6.transaction_code = "123456";
//        transaction6.stock_location = new StockLocation();
//        transaction6.stock_location.name = "Kantin RSHS Bandung";
//        transaction6.total_price = "Rp. 65.000";
//        transaction6.created_at = "2019-07-17 14:08:15";
//        dummyTransactions.add(transaction6);
//
//        transactionsAdapter.setDataList(dummyTransactions);
//        transactionsListView.setHasMoreItems(false);
//        setListViewHeightBasedOnChildren(transactionsListView);

    }

    private void loadMorePostsData() {
//        mPresenter.fetchData(currentPage+1);
    }


    private void initEvent() {
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.lastTransactions(loginStockLocation.id);
    }


    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }

    @Override
    public void onSuccessGetLatestTransactions(List<Transaction> transactions) {
        transactionsAdapter.setDataList(transactions);
        setListViewHeightBasedOnChildren(transactionsListView);
        transactionsListView.setHasMoreItems(false);
    }

    @Override
    public void onSuccessGetLatestTransactionsNow(List<TransactionResponse> transactions) {
        Gson gson = new Gson();
        String json = gson.toJson(transactions);
        Log.d("TAG OnSuccess", json);

        for (int i = 0; i < transactions.size(); i++) {
            TransactionResponse transaction1 = transactions.get(i);
            transactionModels.add(new TransactionModel(transaction1.getId(),
                    transaction1.getBusiness_id(),
                    transaction1.getTenant_id(),
                    transaction1.getOrder_no(),
                    transaction1.getOrder_date(),
                    transaction1.getPayment_type(),
                    transaction1.getPayment_method(),
                    transaction1.getAmount(),
                    transaction1.getDiscount(),
                    transaction1.getStatus()));
        }
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void itemDeleted(int position) {

    }

    void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void onClickBayarWithoutArdi(TransactionPost transactionPost) {
        Loading.show(HomeActivity.this);
        ApiLocal.apiInterface().createTransactions(transactionPost, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse>> call, Response<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse>> response) {
                Loading.hide(HomeActivity.this);
                try {
                    if (response.isSuccessful()) {
                        com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse apiResponse = response.body().getData();
                        Intent intent = new Intent(context, DetailTransactionActivity.class);
                        intent.putExtra("order_no", apiResponse.getId()+"");
                        intent.putExtra("total", apiResponse.getTotalPrice());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        try {
//                            sendReversalAdviceSale(stan);
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse>> call, Throwable t) {
                Loading.hide(HomeActivity.this);
                if (t instanceof HttpException) {
                    ResponseBody body = ((HttpException) t).response().errorBody();
                    Gson gson = new Gson();
                    TypeAdapter<ErrorParser> adapter = gson.getAdapter
                            (ErrorParser.class);
                    try {
                        ErrorParser errorParser =
                                adapter.fromJson(Objects.requireNonNull(body).string());
                        Log.i("FAILURE POS", "Error:" + errorParser.getError());
                        Toast.makeText(HomeActivity.this, errorParser.getError(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                sendReversalAdviceSale(stan);
            }
        });
    }

    public void sendReversalAdviceSale(String stan) {
//        dialog = ProgressDialog.show(context, "Reversal", "Sedang Mengirim Reversal", true);
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences preferences = context.getSharedPreferences(CommonConfig.SETTINGS_FILE, Context.MODE_PRIVATE);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final JSONObject msg = new JSONObject();
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            final String msgId = telephonyManager.getDeviceId() + sdf.format(new Date());
            msg.put("msg_id", msgId);
            msg.put("msg_ui", telephonyManager.getDeviceId());
            // Reversal Sale
            msg.put("msg_si", "R82561");
            msg.put("msg_dt", stan);

            final JSONObject msgRoot = new JSONObject();
            msgRoot.put("msg", msg);
            String hostname = preferences.getString("hostname", CommonConfig.HTTP_REST_URL);
            String postpath = preferences.getString("postpath", CommonConfig.POST_PATH);
            String httpPost = "https" + "://" + hostname + "/" + postpath;

            StringRequest jor = new StringRequest(Request.Method.POST,
                    httpPost,
                    response -> {
                        try {
                            Log.d("TERIMA", response);
                            JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[{\"visible\":true,\"comp_values\":{\"comp_value\":[{\"print\":\"Reversal Berhasil Dikirim\",\n" +
                                    "\"value\":\"Reversal Berhasil Dikirim\"}]},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
                                    "\"type\":\"3\",\"title\":\"Reversal\"}}");
//                                Toast.makeText(context, "Reversal Berhasil Dikirim", Toast.LENGTH_SHORT).show();

//                                processResponse(rps, msgId);
//                                dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            try {
                                Toast.makeText(context, "Request Timeout",
                                        Toast.LENGTH_LONG).show();
                                JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[" +
                                        "{\"visible\":true,\"comp_values\":{\"comp_value\":[" +
                                        "{\"print\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\",\n" +
                                        "\"value\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\"}]" +
                                        "},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
                                        "\"type\":\"3\",\"title\":\"Gagal\"}}");

    //                            Toast.makeText(context, "Transaksi ditolak\nTidak dapat mengirim Reversal", Toast.LENGTH_SHORT).show();

    //                            processResponse(rps, msgId);
    //                            dialog.dismiss();
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "text/plain; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {

                        return msgRoot == null ? null : msgRoot.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        Log.e("VOLLEY", "Unsupported Encoding while trying to get the bytes of " + msgRoot.toString() + "utf-8");
                        return null;
                    }
                }


            };
            jor.setRetryPolicy(new DefaultRetryPolicy(10000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue revrq = Volley.newRequestQueue(context);
            revrq.add(jor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
