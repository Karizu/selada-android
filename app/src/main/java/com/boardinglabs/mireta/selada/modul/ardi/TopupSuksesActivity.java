package com.boardinglabs.mireta.selada.modul.ardi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopupSuksesActivity extends BaseActivity {

    private String order_no, total, whatToDo, member_name, member_lulusan, member_angkatan;
    private long nomBayar, mTotal;
    private String mNomBayar;
    private String totals;
    private String order_date;
    private String str;
    private String nominal;
    private String saldo;
    private PrinterDevice printerDevice;
    private Format format;

    @BindView(R.id.btnSelesai)
    LinearLayout btnSelesai;
    @BindView(R.id.btnPrintStruk)
    LinearLayout btnPrintStruk;
    @BindView(R.id.tvKembalian)
    TextView tvKembalian;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_topup_sukses;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pembayaran Sukses");

        Intent intent = getIntent();
        member_name = intent.getStringExtra("member_name");
        member_lulusan = intent.getStringExtra("member_lulusan");
        member_angkatan = intent.getStringExtra("member_angkatan");
        nominal = intent.getStringExtra("nomBayar");
        order_no = intent.getStringExtra("order_no");
        order_date = intent.getStringExtra("date");
        saldo = intent.getStringExtra("saldo");
        int mSaldo = Integer.parseInt(saldo);
        tvKembalian.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mSaldo));

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");

    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        Intent intent = new Intent(TopupSuksesActivity.this, HomeArdiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @OnClick(R.id.btnPrintStruk)
    void onClickBtnPrintStruk() {
        printStruk();
    }

    @OnClick(R.id.btnSelesai)
    void onClickBtnSelesai() {
        Intent intent = new Intent(TopupSuksesActivity.this, HomeArdiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TopupSuksesActivity.this, HomeArdiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

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
        }
    }

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
                } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                    str += context.getString(R.string.statusNor) + "\n";
                    handler.post(myRunnable);
                    Thread thread = new Thread(() -> {
                        // TODO Auto-generated method stub
                        try {

                            format.setParameter("align", "center");
                            format.setParameter("bold", "true");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Merchant BL" + "\n" +
                                    "Jl. Batik Saketi No. 7" + "\n" +
                                    "Bandung" + "\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printText(format, "Detail Transaksi\n" +
                                    order_date + "\n" +
                                    "Order No: " + order_no + "\n");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printlnText(format, member_name);
                            printerDevice.printlnText(format, member_lulusan);
                            printerDevice.printlnText(format, member_angkatan);
                            printerDevice.printText(format, "--------------------------------\n");


                            String qty = "1 x ";
                            int nomBayar = Integer.parseInt(nominal);
                            String price = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(nomBayar);
                            String grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(nomBayar);

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Topup ARDI" + "\n" +
                                    qty + price + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, grandPrice + "\n");
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, "--------------------------------\n");

//                            grandPrice = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mTotal);

                            printerDevice.printText(format, "TOTAL : " + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, grandPrice + "\n");

                            try {
                                int mSisaSaldo = Integer.parseInt(saldo);
                                printerDevice.printText(format, "--------------------------------\n");

                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "Sisa Saldo :" + "\n");
                                format.clear();
                                format.setParameter("align", "right");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mSisaSaldo) + "\n");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            printerDevice.printText(format, "--------------------------------\n\n");
                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Terimakasih sudah melakukan \ntopup");
                            printerDevice.printlnText(format, member_name);
                            printerDevice.printlnText(format, "\n");
                            printerDevice.printlnText(format, "\n");

                            closePrinter();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    thread.start();
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
}
