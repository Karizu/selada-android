package com.boardinglabs.mireta.selada.modul.selada.launcher.history.topup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPembayaranActivity extends AppCompatActivity {

    private Context context;
    private String date, dateStart, id;
    private int uAmount;

    @BindView(R.id.hometoolbar_title)
    TextView hometoolbar_title;
    @BindView(R.id.hometoolbar_logo)
    ImageView hometoolbar_logo;
    @BindView(R.id.layoutToolbarName)
    LinearLayout layoutToolbarName;
    @BindView(R.id.tvToTime)
    TextView tvToTime;
    @BindView(R.id.tvRangeTime)
    TextView tvRangeTime;
    @BindView(R.id.tvTotalBayar)
    TextView tvTotalBayar;

    @OnClick(R.id.btnConfirm)
    void onClickbtnConfirm(){
        Intent intent = new Intent(context, DetailHistoryTopupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("topup_id", id);
        startActivity(intent);
    }

    private final Handler handler = new Handler();

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack(){
        Intent intent = new Intent(context, SeladaLaucherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pembayaran);
        ButterKnife.bind(this);
        context = this;

        hometoolbar_logo.setVisibility(View.GONE);
        layoutToolbarName.setVisibility(View.VISIBLE);
        hometoolbar_title.setText("Detail Pembayaran");

        try {
            Intent intent = getIntent();
            id = intent.getStringExtra("topup_id");
            uAmount = intent.getIntExtra("uAmount", 0);
            date = intent.getStringExtra("date");
            dateStart = intent.getStringExtra("dateStart");
        } catch (Exception e){
            e.printStackTrace();
        }

        Date d = null, d2 = null;
        long diffHours = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            d = sdf.parse(date);
            d2 = sdf.parse(dateStart);
            long diff = d.getTime() - d2.getTime();
            diffHours = diff / (60 * 60 * 1000) % 24;
            Log.e("test",diffHours + " hours, "+diff);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        sdf.applyPattern("HH:mm aa");

        tvToTime.setText(sdf.format(d));
        tvTotalBayar.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(uAmount));
        tvRangeTime.setText("Selesaikan pembayaran Anda dalam "+diffHours+" jam");

    }

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                getDetailTopup();
            }
        }, 30000);
    }

    private void getDetailTopup(){
        Api.apiInterface().getDetailTopup(id, "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Topup>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topup>> call, Response<ApiResponse<Topup>> response) {
                try {
                    Log.d("MASUKK", "Detail Topup");
                    Topup historyTopup = response.body().getData();
                    int status = Integer.parseInt(historyTopup.getStatus());
                    String id = historyTopup.getId();
                    if (status == 1){
                        Intent intent = new Intent(context, DetailHistoryTopupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("topup_id", id);
                        startActivity(intent);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topup>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doTheAutoRefresh();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, SeladaLaucherActivity.class);
        startActivity(intent);
    }
}
