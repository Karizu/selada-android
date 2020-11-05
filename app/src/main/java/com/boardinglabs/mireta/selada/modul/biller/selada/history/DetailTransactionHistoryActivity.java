package com.boardinglabs.mireta.selada.modul.biller.selada.history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.bjb.common.CommonConfig;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailTransactionHistoryActivity extends BaseActivity implements CommonInterface, TransactionHistoryView {

    public static String TRANSACTION_ID = "TRANSACTION_ID";
    public static String TRANSACTION_STATUS = "TRANSACTION_STATUS";
    private PrinterDevice printerDevice;
    private Format format;
    private String str;
    private String stan = "";
    private Context context;
    private GSeladaTransaction transaction;
    private String transactionId;
    private TransactionHistoryPresenter transactionHistoryPresenter;
    private boolean isIntentFromPay = false;
    private boolean isAgenStruk;

    TextView tvMerchantName;
    TextView tvTotalBayar;
    TextView tvStatusTransaksi;
    TextView tvTanggal;
    TextView tvTransactionCode;
    TextView tvTransactionInfo;
    TextView tvType;
    TextView tvDetailedInfo;
    ImageView categoryImage;
    ImageView providerImage;
    LinearLayout btnPrintStruk, btnPrintStrukNasabah;
    LinearLayout layoutInfoPembayaran;
    LinearLayout layoutPayByArdi;
    LinearLayout layoutDetailedInfo;
    SwipeRefreshLayout pullRefresh;
    private int status;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail_history_transaksi;
    }

    @Override
    protected void setContentViewOnChild() {
        setSeladaToolbarTitle("Detail Transaksi");
        context = this;
        tvMerchantName = findViewById(R.id.tvMerchantNames);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);
        tvStatusTransaksi = findViewById(R.id.tvStatusTransaksi);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvTransactionCode = findViewById(R.id.tvTransactionCode);
        tvTransactionInfo = findViewById(R.id.tvTransactionInfo);
        tvType = findViewById(R.id.tvType);
        btnPrintStruk = findViewById(R.id.btnPrintStruk);
        btnPrintStrukNasabah = findViewById(R.id.btnPrintStrukNasabah);
        pullRefresh = findViewById(R.id.swipeRefresh);
        layoutInfoPembayaran = findViewById(R.id.layoutInfoPembayaran);
        layoutPayByArdi = findViewById(R.id.layoutPayByArdi);
        categoryImage = findViewById(R.id.icon_category);
        providerImage = findViewById(R.id.icon_provider);
        tvDetailedInfo = findViewById(R.id.tvDetailedInfo);
        layoutDetailedInfo = findViewById(R.id.layoutDetailedInfo);

        btnPrintStruk.setOnClickListener(view -> {
            if (transaction != null) {
                isAgenStruk = true;
                printTransactionStruk();
            }
        });

        btnPrintStrukNasabah.setOnClickListener(view -> {
            if (transaction != null) {
                isAgenStruk = false;
                printTransactionStruk();
            }
        });

        pullRefresh.setOnRefreshListener(() -> {
            getTransactionDetail();
            pullRefresh.setRefreshing(false);
        });

        pullRefresh.setColorSchemeResources(R.color.primaryAccentBlue);
        getTransactionDetail();
    }

    @Override
    protected void onCreateAtChild() {
        transactionId = getIntent().getStringExtra(TRANSACTION_ID);
        stan = getIntent().getStringExtra("stan");
        int status = getIntent().getIntExtra(TRANSACTION_STATUS, 0);
        if (status != 2){
            handler.post(timedTask);
        }
        try {
            if (getIntent().getStringExtra("intent") != null) {
                isIntentFromPay = true;
            }
            Log.d("STR", str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        transactionHistoryPresenter = new TransactionHistoryPresenterImpl(this, this);
        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");
    }

    @Override
    protected void onBackBtnPressed() {
        if (isIntentFromPay) {
            Intent intent = new Intent(DetailTransactionHistoryActivity.this, SeladaLaucherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @Override
    public void onBackPressed() {
        if (isIntentFromPay) {
            handler.removeCallbacks(timedTask);
            Intent intent = new Intent(DetailTransactionHistoryActivity.this, SeladaLaucherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            handler.removeCallbacks(timedTask);
            super.onBackPressed();
        }
    }

    private Runnable timedTask = new Runnable() {
        @Override
        public void run() {
            getTransactionDetail();
            handler.postDelayed(timedTask, 10000);
        }
    };

    public void getTransactionDetail() {
        transactionHistoryPresenter.detailTransaction(transactionId);
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
        return NetworkManager.getSeladaCoreInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    public void onSuccessGetDetailTransaction(GSeladaTransaction transaction) {
        this.transaction = transaction;
        this.status = transaction.status;
        if (transaction.stan != null && !transaction.stan.equals("")) {
            stan = transaction.stan;
        }

        String date = "-";
//        String time = "-";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(transaction.updated_at);
            sdf.applyPattern("dd MMM yyyy HH:mm");
            date = sdf.format(d);
//            d2 = sdf2.parse(transaction.updated_at);
            sdf2.applyPattern("HH:mm aa");
//            time = sdf2.format(d2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        tvMerchantName.setText(transaction.merchant.name);
        layoutInfoPembayaran.setVisibility(View.GONE);
        layoutPayByArdi.setVisibility(View.GONE);

        switch (Integer.parseInt(transaction.service.category_id)) {
            case 1:
                categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pulsa));
                break;
            case 2:
                categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_data));
                break;
            case 3:
                categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pln_prabayar_lightning));
                break;
            case 4:
                categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pln_pascabayar_lightning));
                break;
            case 5:
                categoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_bpjs_kesehatan));
                break;
        }

        if (transaction.service.product.provider.name.contains("TELKOMSEL")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_telkomsel));
        } else if (transaction.service.product.provider.name.contains("XL")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_xl));
        } else if (transaction.service.product.provider.name.contains("AXIS")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.axis));
        } else if (transaction.service.product.provider.name.contains("INDOSAT")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_indosat));
        } else if (transaction.service.product.provider.name.contains("THREE")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_tri));
        } else if (transaction.service.product.provider.name.contains("SMARTFREN")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.smartfren));
        } else if (transaction.service.product.provider.name.contains("PLN")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pln));
        } else if (transaction.service.product.provider.name.contains("PDAM")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pdam));
        } else if (transaction.service.product.provider.name.contains("BPJS")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_bpjs_kesehatan));
        } else if (transaction.service.product.provider.name.contains("TELKOM")) {
            providerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.speedy));
        }

        // PROCESS TOKEN
        if (Integer.parseInt(transaction.service.category_id) == Constant.LISTRIK) {
            String note = transaction.note != null ? transaction.note : "";
            JSONObject jsonNote = null;
            try {
                jsonNote = new JSONObject(note);

                if (jsonNote != null) {
                    JSONObject dataPLN = null;
                    JSONObject spiOBJ = null;
                    if (jsonNote.has("data")) {
                        dataPLN = jsonNote.getJSONObject("data");
                        spiOBJ = jsonNote;
                    } else {
                        dataPLN = jsonNote;
                    }

                    if (dataPLN != null && dataPLN.has("token")) {
                        layoutDetailedInfo.setVisibility(View.VISIBLE);
                        String totalAmount = dataPLN.getString("amount");
                        String adminBank = dataPLN.getString("admin_bank");
                        String tipeDaya = dataPLN.getString("tipe") + "/" + dataPLN.getString("daya");
                        int amount = Integer.valueOf(totalAmount) - Integer.valueOf(adminBank);
                        String kwh = dataPLN.getString("jml_kwh");
                        String plnInfo =
                                "ID Pelanggan: " + dataPLN.getString("id_pelanggan") + "\n" +
                                        "Nama Pelanggan: " + dataPLN.getString("nama_pelanggan") + "\n" +
                                        "Tipe/Daya: " + tipeDaya + "\n" +
                                        "Nominal: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(amount)) + "\n" +
                                        "kWh: " + kwh + "\n" +
                                        "\n" +
                                        "STROOM/TOKEN\n" + dataPLN.getString("token");

                        tvDetailedInfo.setText(plnInfo);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Integer.parseInt(transaction.service.category_id) == Constant.LISTRIK_PASCABAYAR) {
            String note = transaction.note != null ? transaction.note : "";
            JSONObject jsonNote = null;
            try {
                jsonNote = new JSONObject(note);

                if (jsonNote != null) {
                    JSONObject dataPLN = null;
                    JSONObject spiOBJ = null;
                    if (jsonNote.has("data")) {
                        dataPLN = jsonNote.getJSONObject("data");
                        spiOBJ = jsonNote;
                    } else {
                        dataPLN = jsonNote;
                        spiOBJ = jsonNote;
                    }

                    if (dataPLN != null && spiOBJ.has("sts") && (spiOBJ.getInt("sts") == 500)
                            && spiOBJ.has("voc") && (spiOBJ.getString("voc").equals("PAYPLN20"))) {
                        layoutDetailedInfo.setVisibility(View.VISIBLE);


                        String plnInfo =
                                "ID Pelanggan: " + dataPLN.getString("id_pelanggan") + "\n" +
                                        "Nama Pelanggan: " + dataPLN.getString("nama_pelanggan") + "\n" +
                                        "Tarif/Daya: " + dataPLN.getString("tipe") + "\n" +
                                        "BL/TH: " + dataPLN.getString("blth") + "\n" +
                                        "STAND METER: " + dataPLN.getString("stan_meter") + "\n" +
                                        "RP TAG PLN: " + "Rp " + MethodUtil.toCurrencyFormat(dataPLN.getString("rptag")) + "\n" +
                                        "NO REF: " + dataPLN.getString("refno") + "\n" +
                                        "ADMIN SELADA: " + "Rp " + MethodUtil.toCurrencyFormat(transaction.service.markup);

                        tvDetailedInfo.setText(plnInfo);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Integer.parseInt(transaction.service.category_id) == Constant.TELKOM
                || Integer.parseInt(transaction.service.category_id) == Constant.MULTIFINANCE
                || Integer.parseInt(transaction.service.category_id) == Constant.BPJS_KESEHATAN
                || Integer.parseInt(transaction.service.category_id) == Constant.PDAM
                || Integer.parseInt(transaction.service.category_id) == Constant.TV_KABEL) {
            String note = transaction.note != null ? transaction.note : "";
            JSONObject jsonNote = null;
            try {
                jsonNote = new JSONObject(note);

                if (jsonNote != null) {
                    JSONObject data = null;
                    JSONObject spiOBJ = null;
                    if (jsonNote.has("data")) {
                        data = jsonNote.getJSONObject("data");
                        spiOBJ = jsonNote;
                    } else {
                        data = jsonNote;
                        spiOBJ = jsonNote;
                    }

                    if (data != null && spiOBJ.has("sts") && (spiOBJ.getInt("sts") == 500)
                            && spiOBJ.has("voc")) {
                        layoutDetailedInfo.setVisibility(View.VISIBLE);

                        try {
                            String dataInfo = "";
                            String objName = "";
                            String objValue = "";
                            for (int i = data.names().length() - 1; i >= 0; i--) {
                                try {
                                    objName = data.names().getString(i);
                                    objValue = data.getString(data.names().getString(i));
                                    if (!objName.equalsIgnoreCase("tgltrx") && !objName.equalsIgnoreCase("nopel") && !objName.equalsIgnoreCase("tagihan") && !objName.equalsIgnoreCase("admin") && !objName.equalsIgnoreCase("total")) {
                                        dataInfo += objName + ": " + objValue;
                                        if (i > 0) {
                                            dataInfo += "\n";
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                            tvDetailedInfo.setText(dataInfo);
                        } catch (Exception e) {
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Integer.parseInt(transaction.service.category_id) == Constant.PULSA_PASCABAYAR) {
            String note = transaction.note != null ? transaction.note : "";
            JSONObject jsonNote = null;
            try {
                jsonNote = new JSONObject(note);

                if (jsonNote != null) {
                    JSONObject dataPLN = null;
                    JSONObject spiOBJ = null;
                    if (jsonNote.has("data")) {
                        dataPLN = jsonNote.getJSONObject("data");
                        spiOBJ = jsonNote;
                    } else {
                        dataPLN = jsonNote;
                        spiOBJ = jsonNote;
                    }

                    if (dataPLN != null && spiOBJ.has("sts") && spiOBJ.getInt("sts") == 500) {
                        layoutDetailedInfo.setVisibility(View.VISIBLE);

                        String nama = dataPLN.getString("nama");
                        String nopel = dataPLN.getString("nopel");
                        String tagihan = dataPLN.getString("tagihan");
                        String adminBank = dataPLN.getString("admin");
                        String total = dataPLN.getString("total");
                        String plnInfo =
                                "Nomor: " + nopel + "\n" +
                                        "Nama Pelanggan: " + nama + "\n" +
                                        "Tagihan: " + tagihan + "\n" +
                                        "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(adminBank)) + "\n" +
                                        "Total: " + total + "\n";

                        tvDetailedInfo.setText(plnInfo);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject json = new JSONObject(transaction.note);
            switch (json.getInt("sts")) {
                case 100:
                    switch (transaction.status) {
                        case 0:
                            tvStatusTransaksi.setText("PENDING");
                            tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Orange));
                            btnPrintStruk.setVisibility(View.VISIBLE);
                            layoutInfoPembayaran.setVisibility(View.VISIBLE);
                            layoutPayByArdi.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            tvStatusTransaksi.setText("BERHASIL");
                            tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Green));
                            btnPrintStruk.setVisibility(View.VISIBLE);
                            layoutInfoPembayaran.setVisibility(View.VISIBLE);
                            layoutPayByArdi.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            tvStatusTransaksi.setText("GAGAL");
                            tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            btnPrintStruk.setVisibility(View.GONE);
                            btnPrintStrukNasabah.setVisibility(View.GONE);
                            break;
                    }
                    break;
                case 500:
                    status = 1;
                    tvStatusTransaksi.setText("BERHASIL");
                    tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Green));
                    btnPrintStruk.setVisibility(View.VISIBLE);
                    layoutInfoPembayaran.setVisibility(View.VISIBLE);
                    layoutPayByArdi.setVisibility(View.VISIBLE);
                    break;
                default:
                    status = 2;
                    tvStatusTransaksi.setText("GAGAL");
                    tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Red));
                    btnPrintStruk.setVisibility(View.GONE);
                    btnPrintStrukNasabah.setVisibility(View.GONE);
                    break;
            }
        } catch (JSONException e) {
            switch (transaction.status) {
                case 0:
                    tvStatusTransaksi.setText("PENDING");
                    tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Orange));
                    btnPrintStruk.setVisibility(View.VISIBLE);
                    layoutInfoPembayaran.setVisibility(View.VISIBLE);
                    layoutPayByArdi.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    tvStatusTransaksi.setText("BERHASIL");
                    tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Green));
                    btnPrintStruk.setVisibility(View.VISIBLE);
                    layoutInfoPembayaran.setVisibility(View.VISIBLE);
                    layoutPayByArdi.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tvStatusTransaksi.setText("GAGAL");
                    tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Red));
                    btnPrintStruk.setVisibility(View.GONE);
                    btnPrintStrukNasabah.setVisibility(View.GONE);
                    break;
            }
            e.printStackTrace();
        }

//        getTrxStatus(transaction.merchant_no, transaction.code);

        tvTotalBayar.setText("Rp " + MethodUtil.toCurrencyFormat(transaction.price));
        tvTanggal.setText(date);
        tvTransactionCode.setText(transaction.code + " (" + String.valueOf(transaction.id) + ")");
        tvType.setText(transaction.service.product.provider.category.name);
        tvTransactionInfo.setText(transaction.service.product.name + " (" + transaction.merchant_no + ")");
    }

    @Override
    public void onSuccessGetHistoryTransaction(List<GSeladaTransaction> transaction) {

    }

    private void proccessReversal() {
        String msgId = "";
        try {
            if (!Utils.isTransactionReversed(context, msgId)) {
                sendReversalAdviceSale(stan, transaction.updated_at);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

    public void printTransactionStruk() {
        try {
            String date = "-";
            String time = "-";

//            String stan = etStan.getText().toString();

            if (transaction.stan != null && !transaction.stan.equals("")) {
                stan = transaction.stan;
            }

            try {
                Date d = null, d2 = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                d = sdf.parse(transaction.created_at);
                sdf.applyPattern("dd MMM yyyy");
                date = sdf.format(d);
                d2 = sdf2.parse(transaction.created_at);
                sdf2.applyPattern("HH:mm");
                time = sdf2.format(d2);

            } catch (Exception e) {
                e.printStackTrace();
            }


            String statusString = "PENDING";
            switch (status) {
                case 0:
                    statusString = "Sedang diproses";
                    break;
                case 1:
                    statusString = "Sukses";
                    break;
                case 2:
                    statusString = "Gagal";
                    break;
            }

            // PROCESS TOKEN
            boolean isPLNToken = false;
            boolean isPascaPLN = false;
            boolean isNoHeader = false;
            String namaPelangganPLN = "";
            String nomorPelangganPLN = "";
            String tokenPLN = "";
            String totalAmountPLN = "";
            String rpTagPLN = "";
            String totalPLN = "";
            String blTHPLN = "";
            String adminBankPLN = "";
            String tipeDayaPLN = "";
            String standMeterPLN = "";
            String noRefPLN = "";
            String adminSelada = "";
            int amountPLN = 0;
            String kwhPLN = "";

            //PROCCESS PULSA PASCABAYAR
            boolean isPulsaPascabayar = false;
            String nomorPelanggan = "";
            String namaPelanggan = "";
            String tagihan = "";
            String admin = "";
            String total = "";
            String noreff = "";

            if (Integer.parseInt(transaction.service.category_id) == Constant.LISTRIK) {

                String note = transaction.note != null ? transaction.note : "";
                JSONObject jsonNote = null;
                try {
                    isNoHeader = true;
                    jsonNote = new JSONObject(note);

                    if (jsonNote != null) {
                        JSONObject dataPLN = null;
                        if (jsonNote.has("data")) {
                            dataPLN = jsonNote.getJSONObject("data");
                        } else {
                            dataPLN = jsonNote;
                        }

                        if (dataPLN != null && dataPLN.has("token")) {
                            isPLNToken = true;
                            nomorPelangganPLN = dataPLN.getString("id_pelanggan");
                            namaPelangganPLN = dataPLN.getString("nama_pelanggan");
                            tokenPLN = dataPLN.getString("token");

                            totalAmountPLN = dataPLN.getString("amount");
                            adminBankPLN = dataPLN.getString("admin_bank");
                            tipeDayaPLN = dataPLN.getString("tipe") + "/" + dataPLN.getString("daya");
                            amountPLN = Integer.parseInt(totalAmountPLN) - Integer.parseInt(adminBankPLN);
                            kwhPLN = dataPLN.getString("jml_kwh");

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (Integer.parseInt(transaction.service.category_id) == Constant.LISTRIK_PASCABAYAR) {

                String note = transaction.note != null ? transaction.note : "";
                JSONObject jsonNote = null;
                try {
                    isNoHeader = true;
                    jsonNote = new JSONObject(note);

                    if (jsonNote != null) {
                        JSONObject dataPLN = null;
                        JSONObject spiOBJ = null;
                        if (jsonNote.has("data")) {
                            dataPLN = jsonNote.getJSONObject("data");
                            spiOBJ = jsonNote;
                        } else {
                            dataPLN = jsonNote;
                            spiOBJ = jsonNote;
                        }

                        if (dataPLN != null && spiOBJ.has("sts") && (spiOBJ.getInt("sts") == 500)
                                && spiOBJ.has("voc") && (spiOBJ.getString("voc").equals("PAYPLN20"))) {

                            isPascaPLN = true;
                            nomorPelangganPLN = dataPLN.getString("id_pelanggan");
                            namaPelangganPLN = dataPLN.getString("nama_pelanggan");

                            tipeDayaPLN = dataPLN.getString("tipe");
                            blTHPLN = dataPLN.getString("blth");
                            standMeterPLN = dataPLN.getString("stan_meter");

                            rpTagPLN = "Rp " + MethodUtil.toCurrencyFormat(dataPLN.getString("rptag"));
                            noRefPLN = dataPLN.getString("refno");
                            adminSelada = "Rp " + MethodUtil.toCurrencyFormat(transaction.service.markup);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (Integer.parseInt(transaction.service.category_id) == Constant.PULSA_PASCABAYAR) {
                String note = transaction.note != null ? transaction.note : "";
                JSONObject jsonNote = null;
                try {
                    jsonNote = new JSONObject(note);

                    if (jsonNote != null) {
                        JSONObject dataPulsaPascabayar = null;
                        JSONObject spiOBJ = null;
                        if (jsonNote.has("data")) {
                            dataPulsaPascabayar = jsonNote.getJSONObject("data");
                            spiOBJ = jsonNote;
                        } else {
                            dataPulsaPascabayar = jsonNote;
                            spiOBJ = jsonNote;
                        }

                        if (dataPulsaPascabayar != null && spiOBJ.has("sts") && (spiOBJ.getInt("sts") == 500)
                                && spiOBJ.has("voc")) {

                            isPulsaPascabayar = true;
                            nomorPelanggan = dataPulsaPascabayar.getString("nopel");
                            namaPelanggan = dataPulsaPascabayar.getString("nama");

                            tagihan = "Rp " + MethodUtil.toCurrencyFormat(dataPulsaPascabayar.getString("tagihan"));
                            admin = "Rp " + MethodUtil.toCurrencyFormat(dataPulsaPascabayar.getString("admin"));
                            total = "Rp " + MethodUtil.toCurrencyFormat(dataPulsaPascabayar.getString("total"));
                            noreff = dataPulsaPascabayar.getString("refno");
                            adminSelada = "Rp " + MethodUtil.toCurrencyFormat(transaction.service.markup);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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
                    closePrinter();
                    Toast.makeText(context, "The printer is short of paper", Toast.LENGTH_SHORT).show();
                } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                    str += context.getString(R.string.statusNor) + "\n";
                    handler.post(myRunnable);
                    String finalDate = date;
                    String finalTime = time;
                    String finalStatusString = statusString;

                    //PLN
                    boolean finalIsPLNToken = isPLNToken;
                    String finalNomorPelangganPLN = nomorPelangganPLN;
                    String finalNamaPelangganPLN = namaPelangganPLN;
                    String finalTokenPLN = tokenPLN;
                    String finalTipeDayaPLN = tipeDayaPLN;
                    String finalKwhPLN = kwhPLN;
                    int finalAmountPLN = amountPLN;
                    String finalBlTHPLN = blTHPLN;
                    String finalStandMeterPLN = standMeterPLN;
                    String finalRpTagPLN = rpTagPLN;
                    String finalNoRefPLN = noRefPLN;
                    String finalAdminSelada = adminSelada;
                    boolean finalIsPascaPLN = isPascaPLN;
                    boolean finalIsNoHeader = isNoHeader;

                    //PULSA PASCABAYAR
                    boolean finalIsPulsaPasca = isPulsaPascabayar;
                    String finalNopel = nomorPelanggan;
                    String finalNama = namaPelanggan;
                    String finalTagihan = tagihan;
                    String finalAdmin = admin;
                    String finalTotal = total;
                    String finalReffno = noreff;

                    //ADD BIAYA ADMIN, NOMINAL, TOTAL
                    long grandTotal = Integer.parseInt(transaction.price);
                    long hargaAwal = Integer.parseInt(transaction.vendor_price);
                    long biayaAdmin = grandTotal - hargaAwal;

                    int finalHargaAwal = (int) hargaAwal;
                    int finalBiayaAdmin = (int) biayaAdmin;
                    int finalGrandTotal = (int) grandTotal;

                    long nominal, system_markup, biaya_admin;

                    if (Integer.parseInt(transaction.service.category_id) == Constant.LISTRIK_PASCABAYAR
                            || Integer.parseInt(transaction.service.category_id) == Constant.PULSA_PASCABAYAR
                            || Integer.parseInt(transaction.service.category_id) == Constant.TELKOM
                            || Integer.parseInt(transaction.service.category_id) == Constant.BPJS_KESEHATAN
                            || Integer.parseInt(transaction.service.category_id) == Constant.PDAM) {
                        nominal = Integer.parseInt(transaction.vendor_price);
                        system_markup = Integer.parseInt(transaction.service.system_markup);
                        biaya_admin = Integer.parseInt(transaction.service.markup);
                    } else {
                        nominal = Integer.parseInt(transaction.service.biller_price);
                        system_markup = Integer.parseInt(transaction.service.system_markup);
                        biaya_admin = Integer.parseInt(transaction.service.markup);
                    }

                    long amount;
                    int grand_total;
                    AtomicInteger amounts = new AtomicInteger();
                    AtomicInteger finalbiaya_admin = new AtomicInteger((int) biaya_admin);

                    if (isAgenStruk) {
                        amount = nominal + system_markup;
                        grand_total = (int) (biaya_admin + amount);

                        // handle if total == fee (case pln pasca)
                        if (grand_total == biaya_admin) {
                            amount = Integer.parseInt(transaction.service.biller_price) + system_markup;
                            grand_total = (int) (biaya_admin + amount);
                        }

                        amounts.set((int) (nominal + system_markup));
                    } else {
                        amount = nominal;
                        grand_total = (int) (amount);

                        // handle if total == fee (case pln pasca)
                        if (grand_total == 0) {
                            amount = Integer.parseInt(transaction.service.biller_price);
                            grand_total = (int) (amount);
                        }

                        amounts.set((int) (nominal));
                    }


                    AtomicInteger finalAmounts = amounts;
                    AtomicInteger finalGrand_total = new AtomicInteger(grand_total);
                    String finalTagihan1 = tagihan;
                    Thread thread = new Thread(() -> {
                        // TODO Auto-generated method stub
                        try {

                            QRCodeWriter writer = new QRCodeWriter();
                            Bitmap bmp = null;

                            Hashtable hints = new Hashtable();
                            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                            printerDevice.printlnText(format, "\n");

//                            try {
//                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.selada_final_logo);
//                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//
//                                if (PreferenceManager.getBitmapHeader() != null){
//                                    Bitmap bitmap1 = PreferenceManager.getBitmapHeader();
//                                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
//                                    bitmap1.compress(Bitmap.CompressFormat.PNG, 90, stream1);
//                                    printerDevice.printBitmap(format, bitmap1);
//                                    printerDevice.printText("\n");
//                                }
//                            } catch (Exception e){
//                                e.printStackTrace();
//                            }

                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");

                            try {
                                if (!finalIsNoHeader) {
                                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_bjb);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                                    printerDevice.printBitmap(format, bitmap);
                                    printerDevice.printText("\n");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String phone = "-";

                            try {
                                phone = transaction.merchant.phone != null ? transaction.merchant.phone : "-";
                                phone = phone.equalsIgnoreCase("null") ? "-" : phone;
                            } catch (Exception e) {
                            }

                            printerDevice.printText(format, loginStockLocation.name != null ?
                                    loginStockLocation.name.toUpperCase() + "\n" +
                                            transaction.merchant.name.toUpperCase() + "\n" +
                                            transaction.merchant.address + "\n" + phone + "\n\n"
                                    :
                                    " " + "\n" +
                                            transaction.merchant.name.toUpperCase() + "\n" +
                                            transaction.merchant.address + "\n" + phone + "\n\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, finalDate + "                " + finalTime + "\n");
                            printerDevice.printText(format, getPrintLabelValue("TRXCODE", transaction.code.toUpperCase(), false, true));
                            printerDevice.printText(format, getPrintLabelValue("STATUS", finalStatusString.toUpperCase(), false, true));
                            printerDevice.printText(format, getPrintLabelValue("TRACE NO", stan, false, true));

                            printerDevice.printText(format, "--------------------------------\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "small");
                            printerDevice.printText(format, "Transaksi:\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, transaction.service.product.provider.category.name.toUpperCase() + " " + (finalIsPascaPLN ? "" : transaction.service.product.name.toUpperCase()) + "\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "small");
                            printerDevice.printText(format, "Nomor:\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, transaction.merchant_no.toUpperCase() + "\n");

                            if (finalIsPascaPLN || finalIsPLNToken || finalIsPulsaPasca) {
                                //skip
                            } else {
                                if (transaction.note != null) {
                                    String note = transaction.note != null ? transaction.note : "";
                                    JSONObject jsonNote = null;
                                    try {
                                        jsonNote = new JSONObject(note);

                                        if (jsonNote != null) {
                                            JSONObject data = null;
                                            JSONObject spiOBJ = null;
                                            if (jsonNote.has("data")) {
                                                data = jsonNote.getJSONObject("data");
                                                spiOBJ = jsonNote;
                                            } else {
                                                data = jsonNote;
                                                spiOBJ = jsonNote;
                                            }

                                            if (data != null && spiOBJ.has("sts") && (spiOBJ.getInt("sts") == 500)
                                                    && spiOBJ.has("voc")) {
                                                try {
                                                    String objName = "";
                                                    String objValue = "";
                                                    for (int i = data.names().length() - 1; i >= 0; i--) {
                                                        try {
                                                            objName = data.names().getString(i);
                                                            objValue = data.getString(data.names().getString(i));
                                                            if (objName.equalsIgnoreCase("admin")) {
                                                                finalbiaya_admin.addAndGet(Integer.valueOf(objValue));
                                                                finalAmounts.addAndGet(-Integer.valueOf(objValue));
                                                                if (!isAgenStruk) {
                                                                    finalGrand_total.addAndGet(-Integer.valueOf(objValue));
                                                                }
                                                            } else if (!objName.equalsIgnoreCase("tgltrx") && !objName.equalsIgnoreCase("nopel") && !objName.equalsIgnoreCase("total")
                                                                    && !objName.equalsIgnoreCase("voc") && !objName.equalsIgnoreCase("sts") && !objName.equalsIgnoreCase("hrg")
                                                                    && !objName.equalsIgnoreCase("cmd") && !objName.equalsIgnoreCase("nop")) {

                                                                if (objName.contains("tagihan")) {
                                                                    if (isAgenStruk) {
                                                                        format.clear();
                                                                        format.setParameter("align", "left");
                                                                        format.setParameter("bold", "false");
                                                                        format.setParameter("size", "small");
                                                                        printerDevice.printText(format, objName + ":\n");

                                                                        format.clear();
                                                                        format.setParameter("align", "left");
                                                                        format.setParameter("bold", "false");
                                                                        format.setParameter("size", "medium");
                                                                        printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(objValue)) + "\n");
                                                                    }
                                                                } else {
                                                                    format.clear();
                                                                    format.setParameter("align", "left");
                                                                    format.setParameter("bold", "false");
                                                                    format.setParameter("size", "small");
                                                                    printerDevice.printText(format, objName + ":\n");

                                                                    format.clear();
                                                                    format.setParameter("align", "left");
                                                                    format.setParameter("bold", "false");
                                                                    format.setParameter("size", "medium");
                                                                    printerDevice.printText(format, objValue + "\n");
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Harga:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalAmounts.get())) + "\n");

                                if (isAgenStruk) {
                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "small");
                                    printerDevice.printText(format, "Biaya Layanan:\n");

                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "medium");

                                    printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalbiaya_admin.get())) + "\n");
                                }
                            }

//                            format.clear();
//                            format.setParameter("align", "left");
//                            format.setParameter("bold", "false");
//                            format.setParameter("size", "small");
//                            printerDevice.printText(format, "Biaya Layanan:\n");
//
//                            format.clear();
//                            format.setParameter("align", "left");
//                            format.setParameter("bold", "false");
//                            format.setParameter("size", "medium");
//
//                            printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(3000)) + "\n");

                            if (finalIsPLNToken) {

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nomor Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNomorPelangganPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nama Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNamaPelangganPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Tipe/Daya:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalTipeDayaPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nominal:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalAmounts.get())) + "\n");

                                if (isAgenStruk) {
                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "small");
                                    printerDevice.printText(format, "BIAYA ADMIN:\n");

                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "medium");

                                    printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalbiaya_admin.get())) + "\n");
                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "kWh:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalKwhPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Stroom/Token:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalTokenPLN.toUpperCase() + "\n");
                            }

                            if (finalIsPascaPLN) {

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nomor Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNomorPelangganPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nama Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNamaPelangganPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Tarif/Daya:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalTipeDayaPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "BL/TH:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalBlTHPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "STAND METER:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalStandMeterPLN.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "RP TAG PLN:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalRpTagPLN + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "NO REF:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNoRefPLN.toUpperCase() + "\n");

                                if (isAgenStruk) {
                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "small");
                                    printerDevice.printText(format, "BIAYA ADMIN:\n");

                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "medium");

                                    printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalbiaya_admin.get())) + "\n");
                                }
                            }

                            if (finalIsPulsaPasca) {
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nomor Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNopel.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Nama Pelanggan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalNama.toUpperCase() + "\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Tagihan:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalTagihan.toUpperCase() + "\n");

                                if (isAgenStruk) {
                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "small");
                                    printerDevice.printText(format, "Biaya Admin:\n");

                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "medium");

                                    printerDevice.printText(format, "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalbiaya_admin.get())) + "\n");
                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "No Ref:\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("bold", "false");
                                format.setParameter("size", "medium");

                                printerDevice.printText(format, finalReffno + "\n");

                                if (isAgenStruk) {
                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "small");
                                    printerDevice.printText(format, "Biaya Layanan:\n");

                                    format.clear();
                                    format.setParameter("align", "left");
                                    format.setParameter("bold", "false");
                                    format.setParameter("size", "medium");

                                    printerDevice.printText(format, finalAdminSelada + "\n");
                                }
                            }

                            printerDevice.printText(format, "--------------------------------\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, getPrintLabelValue("Total Bayar", "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(finalGrand_total.get())), false, true));

                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Simpan struk ini sebagai alat\nbukti transaksi yang sah\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Terimakasih sudah bertransaksi");
                            printerDevice.printlnText(format, "\n");

                            if (isAgenStruk) {
                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "small");
                                printerDevice.printlnText(format, "--AGENT COPY--");
                                printerDevice.printlnText(format, "\n");
                                printerDevice.printlnText(format, "\n");
                            }


                            closePrinter();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                } else {
                    closePrinter();
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

    private String getPrintLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine) {
        if (value != null) {
            int lineCharCount = 32;
            int labelCharCount = label.length();
            int valueCharCount = value.length();

            if (labelCharCount + valueCharCount > (lineCharCount - 2)) {
                usingNextLine = true;
            }

            String output = "";
            if (!usingNextLine) {
                output += label;
                for (int i = labelCharCount; i < lineCharCount - valueCharCount; i++) {
                    output += " ";
                }
                output += value;
            } else {
                output = label;
                for (int i = labelCharCount; i < lineCharCount; i++) {
                    output += " ";
                }
                output += "\n";
                for (int i = 0; i < valueCharCount; i++) {
                    output += " ";
                }
                output += value;
            }
            if (endWithNewLine) {
                output += "\n";
            }
            return output;
        }
        return "";

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

    public void sendReversalAdviceSale(String stan, String dte) {

//        dialog = ProgressDialog.show(context, "Reversal", "Sedang Mengirim Reversal", true);
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences preferences = context.getSharedPreferences(CommonConfig.SETTINGS_FILE, Context.MODE_PRIVATE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(dte);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        dte = sdf.format(newDate);
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
            final String msgId = telephonyManager.getDeviceId() + dte;
            msg.put("msg_id", msgId);
            msg.put("msg_ui", telephonyManager.getDeviceId());
            // Reversal Sale
            msg.put("msg_si", "R82561");
            msg.put("msg_dt", stan);

            final JSONObject msgRoot = new JSONObject();
            msgRoot.put("msg", msg);
            String hostname = preferences.getString("hostname", CommonConfig.HTTP_REST_URL);
            String postpath = preferences.getString("postpath", CommonConfig.POST_PATH);
            String httpPost = CommonConfig.HTTP_PROTOCOL + "://" + hostname + "/" + postpath;

            StringRequest jor = new StringRequest(Request.Method.POST,
                    httpPost,
                    response -> {
                        try {
                            Log.d("TERIMA", response);
                            JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[{\"visible\":true,\"comp_values\":{\"comp_value\":[{\"print\":\"Reversal Berhasil Dikirim\",\n" +
                                    "\"value\":\"Reversal Berhasil Dikirim\"}]},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
                                    "\"type\":\"3\",\"title\":\"Reversal\"}}");

                            Utils.saveReversalLog("R82561", msgId, context);
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
