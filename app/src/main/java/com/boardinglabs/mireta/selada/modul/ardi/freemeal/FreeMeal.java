package com.boardinglabs.mireta.selada.modul.ardi.freemeal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.ardi.HomeArdiActivity;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeMeal extends BaseActivity {

    private String order_no, total, whatToDo, sisaSaldo, member_name, member_lulusan, member_angkatan;
    private long nomBayar, mTotal;
    private String mNomBayar, totals, order_date, order_time;
    private Context context;
    private String member_id;

    @BindView(R.id.btnPrintStruk)
    LinearLayout btnPrintStruk;
    @BindView(R.id.imgSukses)
    ImageView imgSukses;
    @BindView(R.id.imgGagal)
    ImageView imgGagal;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.tvSaldo)
    TextView tvSaldo;
    @BindView(R.id.memberName)
    TextView memberName;
    @BindView(R.id.memberLulusan)
    TextView memberLulusan;
    @BindView(R.id.memberAngkatan)
    TextView memberAngkatan;

    private PrinterDevice printerDevice;
    private String str;
    private Format format;

    @OnClick(R.id.btnSelesai)
    void onClickSelesai() {
        Intent intent = new Intent(FreeMeal.this, HomeArdiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btnPrintStruk)
    void onClickBtnPrintStruk() {
        printStruk();
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
                            printerDevice.printText(format, order_date + "                " + order_time + "\n\n");
                            printerDevice.printText(format, "Gratis Sarapan\n");
                            printerDevice.printText(format, "--------------------------------\n");
                            printerDevice.printlnText(format, member_name);
                            printerDevice.printlnText(format, member_lulusan);
                            printerDevice.printlnText(format, member_angkatan);
                            printerDevice.printText(format, "--------------------------------\n");

                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Breakfast \n" +
                                    "1 x Rp. 0" + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Rp. 0" + "\n");
                            format.clear();
                            format.setParameter("align", "left");
                            format.setParameter("size", "medium");

                            printerDevice.printText(format, "--------------------------------\n");

                            printerDevice.printText(format, "TOTAL :" + "\n");
                            format.clear();
                            format.setParameter("align", "right");
                            format.setParameter("size", "medium");
                            printerDevice.printText(format, "Rp. 0" + "\n");

                            printerDevice.printText(format, "--------------------------------\n\n");

                            format.clear();
                            format.setParameter("align", "center");
                            format.setParameter("size", "medium");
                            printerDevice.printlnText(format, "Selamat menikmati sarapan");

                            try {
                                printerDevice.printlnText(format, member_name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_free_meal;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        setToolbarTitle("BREAKFAST");
        ButterKnife.bind(this);
        context = this;

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");

        Intent intent = getIntent();
        member_id = intent.getStringExtra("member_id");
        member_name = intent.getStringExtra("member_name");
        member_lulusan = intent.getStringExtra("member_lulusan");
        member_angkatan = intent.getStringExtra("member_angkatan");

        memberName.setText("Nama: " + member_name);
        memberLulusan.setText("Lulusan: " + member_lulusan);
        memberAngkatan.setText("Angkatan " + member_angkatan);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");
        order_time = s.format(new Date());
        order_date = f.format(new Date());

        doCheckFreeMeal();
    }

    private void doCheckFreeMeal() {
        Loading.show(context);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("member_id", member_id)
                .build();

        Api.apiInterface().doCheckFreeMeal(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()) {
                    imgSukses.setVisibility(View.VISIBLE);
                    imgGagal.setVisibility(View.GONE);
                    tvMessage.setText("Silahkan ambil sarapan anda");
                    btnPrintStruk.setVisibility(View.VISIBLE);
                } else {
                    imgSukses.setVisibility(View.GONE);
                    imgGagal.setVisibility(View.VISIBLE);
                    tvMessage.setText("Anda sudah mengambil sarapan");
                    btnPrintStruk.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
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
}
