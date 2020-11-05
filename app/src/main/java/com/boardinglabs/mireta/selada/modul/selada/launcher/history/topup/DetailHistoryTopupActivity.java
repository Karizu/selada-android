package com.boardinglabs.mireta.selada.modul.selada.launcher.history.topup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.selada.component.network.entities.Business;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.entities.Trx.Detail;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailHistoryTopupActivity extends AppCompatActivity {

    private Context context;
    private String id, date, name, time, nominal;
    private long amount;
    private int status;
    private PrinterDevice printerDevice;
    private Format format;
    private TextView txt;
    private String str;

    private User loginUser;
    private StockLocation loginStockLocation;
    private Business loginBusiness;

    @BindView(R.id.hometoolbar_title)
    TextView hometoolbar_title;
    @BindView(R.id.hometoolbar_logo)
    ImageView hometoolbar_logo;
    @BindView(R.id.layoutToolbarName)
    LinearLayout layoutToolbarName;

    @BindView(R.id.tvMerchantNames)
    TextView tvMerchantName;
    @BindView(R.id.tvTotalBayar)
    TextView tvTotalBayar;
    @BindView(R.id.tvStatusTransaksi)
    TextView tvStatusTransaksi;
    @BindView(R.id.tvTanggal)
    TextView tvTanggal;
    @BindView(R.id.tvWaktu)
    TextView tvWaktu;
    @BindView(R.id.btnPrintStruk)
    LinearLayout btnPrintStruk;

    @OnClick(R.id.btnPrintStruk)
    void onClickBtnPrintStruk() {
        printStruk();
    }

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack() {
        onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_topup);
        ButterKnife.bind(this);
        context = this;

        hometoolbar_logo.setVisibility(View.GONE);
        layoutToolbarName.setVisibility(View.VISIBLE);
        hometoolbar_title.setText("Detail Transaksi");

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");

        loginUser = PreferenceManager.getUser();
        loginBusiness = PreferenceManager.getBusiness();
        loginStockLocation = PreferenceManager.getStockLocation();

        Intent intent = getIntent();
        id = intent.getStringExtra("topup_id");
        getDetailTopup();

    }

    private void getDetailTopup() {
        Api.apiInterface().getDetailTopup(id, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Topup>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Topup>> call, Response<ApiResponse<Topup>> response) {
                try {
                    Log.d("MASUKK", "Detail Topup");
                    Topup historyTopup = response.body().getData();
                    name = historyTopup.getMember().getFullname();
                    nominal = historyTopup.getAmount();
                    amount = historyTopup.getAmount_unique();
                    status = Integer.parseInt(historyTopup.getStatus());
                    Date d = null, d2 = null;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    d = sdf.parse(historyTopup.getUpdatedAt());
                    sdf.applyPattern("dd MMM yyyy");
                    date = sdf.format(d);
                    d2 = sdf2.parse(historyTopup.getUpdatedAt());
                    sdf2.applyPattern("HH:mm aa");
                    time = sdf2.format(d2);

                    tvMerchantName.setText(name);
                    switch (status) {
                        case 0:
                            tvStatusTransaksi.setText("PENDING");
                            tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            btnPrintStruk.setVisibility(View.GONE);
                            break;
                        case 1:
                            tvStatusTransaksi.setText("BERHASIL");
                            tvStatusTransaksi.setTextColor(ContextCompat.getColor(context, R.color.Green));
                            btnPrintStruk.setVisibility(View.VISIBLE);
                            break;
                    }
                    tvTotalBayar.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(amount));
                    tvTanggal.setText(date);
                    tvWaktu.setText(time);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topup>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };


    public void printStruk() {
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

                            try {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_selada_header);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                                printerDevice.printBitmap(format, bitmap);
                                printerDevice.printText("\n");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            printerDevice.printText(format, "SELADA" + "\n" +
                                    "Jl. Batik Saketi No. 7" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printText(format, date + "             " + time + "\n");
                            format.setParameter("align", "left");
                            printerDevice.printText(format, getPrintLabelValue("TRXCODE", id, false, true));
                            printerDevice.printText(format, getPrintLabelValue("STATUS", "BERHASIL", false, true));
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "small");
                            printerDevice.printText(format, "Transaksi: \n");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "TOPUP ARDI \n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("bold", "false");
                            format.setParameter("size", "small");
                            printerDevice.printText(format, "Nominal:\n");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, MethodUtil.toCurrencyFormat(String.valueOf(nominal)) + "\n");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printText(format, getPrintLabelValue("TOTAL: ", "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(amount), false, true));
                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Terimakasih sudah melakukan \n topup");
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

    private String getPrintLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine) {
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
}
