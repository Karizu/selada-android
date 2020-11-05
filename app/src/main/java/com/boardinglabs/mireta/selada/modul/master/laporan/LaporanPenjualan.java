package com.boardinglabs.mireta.selada.modul.master.laporan;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.BuildConfig;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.Report.ReportModels;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.master.laporan.adapter.ReportAdapter;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.KatalogModel;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanPenjualan extends BaseActivity {

    private List<KatalogModel> katalogModels;
    private ArrayList<ReportModels> reportModels;
    private ReportAdapter adapter;
    private List<String> itemImage = new ArrayList<>();
    private Context context;
    private Long grandTotal;
    private int mGrandTotal, mPrice, mQty;
    private String pathSettle, pathAll;
    private String checkedSettledFlag = "";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvTotalPenjualan)
    TextView tvTotalPenjualan;
    @BindView(R.id.imgFilter2)
    ImageView imgFilter;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.imgOptions)
    ImageView imgOptions;

    private Dialog dialog;
    private String manufacturer;
    private PrinterDevice printerDevice;
    private Format format;
    private String str;
    private String versionName;

    @OnClick(R.id.imgFilter2)
    void onClickFilter() {
        showDialog();

        pathSettle = "";

        CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
        CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
        CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);

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
            pathAll = "transactions/report?" + pathSettle;
            Log.d("PATH", pathAll);
            mGrandTotal = 0;
            reportModels.clear();
            adapter.notifyDataSetChanged();
            getReportByFilter(pathAll);
            swipeRefresh.setOnRefreshListener(() -> {
                mGrandTotal = 0;
                reportModels.clear();
                adapter.notifyDataSetChanged();
                getReportByFilter(pathAll);
            });
        });

        Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
        btnResetFilter.setOnClickListener(v -> {
            dialog.dismiss();
            mGrandTotal = 0;
            reportModels.clear();
            checkedSettledFlag = "";
            adapter.notifyDataSetChanged();
            getReport();
        });
    }

    @OnClick(R.id.imgOptions)
    void onClickImgPrint() {

        View v1 = findViewById(R.id.imgOptions);
        PopupMenu pm = new PopupMenu(Objects.requireNonNull(context), v1);
        pm.getMenuInflater().inflate(R.menu.menu_laporan, pm.getMenu());
        pm.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_filter:
                    menuItem.setIcon(R.drawable.ic_filter_list_black_24dp);
                    showDialog();

                    pathSettle = "";

                    CheckedTextView ctvSettleAll = dialog.findViewById(R.id.ctvSettleAll);
                    CheckedTextView ctvSettleTrue = dialog.findViewById(R.id.ctvSettleTrue);
                    CheckedTextView ctvSettleFalse = dialog.findViewById(R.id.ctvSettleFalse);

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
                        pathAll = "transactions/report?" + pathSettle;
                        Log.d("PATH", pathAll);
                        mGrandTotal = 0;
                        reportModels.clear();
                        adapter.notifyDataSetChanged();
                        getReportByFilter(pathAll);
                        swipeRefresh.setOnRefreshListener(() -> {
                            mGrandTotal = 0;
                            reportModels.clear();
                            adapter.notifyDataSetChanged();
                            getReportByFilter(pathAll);
                        });
                    });

                    Button btnResetFilter = dialog.findViewById(R.id.btnResetFilter);
                    btnResetFilter.setOnClickListener(v -> {
                        dialog.dismiss();
                        mGrandTotal = 0;
                        checkedSettledFlag = "";
                        reportModels.clear();
                        adapter.notifyDataSetChanged();
                        getReport();
                    });
                    break;
                case R.id.navigation_print:
                    menuItem.setIcon(R.drawable.ic_print_blue_24dp);
                    if (reportModels.size() < 1){
                        Toast.makeText(context, "Tidak ada data yang harus diprint", Toast.LENGTH_SHORT).show();
                    } else {
                        if (manufacturer.equals("wizarPOS")) {
                            printStruk();
                        } else {
//            printText();
                        }
                    }
                    break;
            }
            return true;
        });
        pm.show();

    }

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

    public void printStruk()    {
        try {
            str = context.getString(R.string.openingPrint) + "\n";
            handler.post(myRunnable);
            printerDevice.open();
            str += context.getString(R.string.printerOpenSuc) + "\n";
            handler.post(myRunnable);
            format = new Format();
            try {
                if (printerDevice.queryStatus() == printerDevice.STATUS_OUT_OF_PAPER) {
                    str += context.getString(R.string.queryStatus) + "\n";
                    handler.post(myRunnable);
                    Toast.makeText(context, "The printer is short of paper", Toast.LENGTH_SHORT).show();
                } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                    str += context.getString(R.string.statusNor) + "\n";
                    handler.post(myRunnable);
                    Thread thread = new Thread(() -> {
                        // TODO Auto-generated method stub
                        try {
                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, loginBusiness.name.toUpperCase() + "\n" +
                                    loginBusiness.address + "\n");
                            printerDevice.printText(format, loginStockLocation.telp != null ? loginStockLocation.telp + "\n" : "-" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "--------------------------------");
                            printerDevice.printlnText(format, "Laporan Penjualan");
                            printerDevice.printlnText(format, "--------------------------------");

                            String item, grandPrice;

                            for (int i = 0; i < reportModels.size(); i++) {

                                ReportModels models = reportModels.get(i);
                                int mAmount = Integer.parseInt(models.getItem_price());
                                int mQty = models.getItem_qty();
                                int mGrandPrice = mAmount * mQty;

                                item = models.getItem_name();
                                String qty = models.getItem_qty() + " x ";
                                String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mAmount);
                                grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandPrice);

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, item + "\n" +
                                        qty + price + "\n");
                                format.clear();
                                format.setParameter("align", "right");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, grandPrice + "\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                            }

                            printerDevice.printText(format, "--------------------------------\n");

                            item = "TOTAL :";
                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandTotal);

                            printerDevice.printText(format, item + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, grandPrice + "\n");

                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "small");
                            printerDevice.printlnText(format, "Mireta v"+versionName);
                            printerDevice.printlnText(format, "\n");
                            printerDevice.printlnText(format, "\n");

                            closePrinter();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(context, "The printer is short of paper", Toast.LENGTH_SHORT).show();
                }
            } catch (DeviceException de) {
                str += context.getString(R.string.checkStatus) + "\n";
                handler.post(myRunnable);
                de.printStackTrace();
            }
        } catch (DeviceException de) {
            de.printStackTrace();
            str += context.getString(R.string.openFailed) + "\n";
            handler.post(myRunnable);
        }
    }

    private void closePrinter() {
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReportByFilter(String pathAll) {
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getReportByFilter(pathAll, loginStockLocation.id, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject((Map) response.body().getData());
                    parseJson(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_laporan_penjualan;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("LAPORAN PENJUALAN");
        context = this;
//        imgFilter.setVisibility(View.VISIBLE);
        imgOptions.setVisibility(View.VISIBLE);

        versionName = BuildConfig.VERSION_NAME;

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");
        manufacturer = Build.MANUFACTURER;
        getReport();
        swipeRefresh.setOnRefreshListener(() -> {
            mGrandTotal = 0;
            reportModels.clear();
            adapter.notifyDataSetChanged();
            getReport();
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

    private void getReport() {
        swipeRefresh.setRefreshing(true);
        ApiLocal.apiInterface().getReport(Constant.BELUM_SETTLE, loginStockLocation.id, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                swipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject((Map) response.body().getData());
                    parseJson(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void parseJson(JSONObject data) {

        try {
            JSONObject items = data.getJSONObject("items");
            Iterator<String> categories = items.keys();

            LinkedHashMap<String, ArrayList<ReportModels>> itemCategory = new LinkedHashMap<>();
            ArrayList<ReportModels> itemInsideCat = null;
            while (categories.hasNext()) {
                String key = categories.next();
                JSONObject category = items.getJSONObject(key);

                Iterator<String> itemKeys = category.keys();

                itemInsideCat = new ArrayList<>();

                while (itemKeys.hasNext()) {
                    String itemKey = itemKeys.next();
                    JSONObject item = category.getJSONObject(itemKey);
                    itemInsideCat.add(new ReportModels(item.getInt("item_id"),
                            item.getString("item_name"),
                            item.getString("item_price"),
                            item.getInt("item_qty"),
                            item.getInt("category_id"),
                            item.getString("category_name")));

                    mPrice = Integer.parseInt(item.getString("item_price"));
                    mQty = item.getInt("item_qty");
                    mGrandTotal += mPrice * mQty;
//                    grandTotal = (long) mGrandTotal;
                }

                Gson gson = new Gson();
                String json = gson.toJson(itemInsideCat);
                System.out.println(json);

                itemCategory.put(key, itemInsideCat);
            }


            tvTotalPenjualan.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mGrandTotal));

            reportModels = new ArrayList<>();
            for (Map.Entry<String, ArrayList<ReportModels>> entry : itemCategory.entrySet()) {
                ArrayList<ReportModels> reportModel = itemCategory.get(entry.getKey());
                reportModels.addAll(reportModel);
            }

            if (reportModels.size() < 1) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }

            Gson gson = new Gson();
            String json = gson.toJson(reportModels);
            System.out.println(json);

            adapter = new ReportAdapter(reportModels, context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
//            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(LaporanPenjualan.this));
        //set content
        dialog.setContentView(R.layout.layout_filter_laporan);
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
