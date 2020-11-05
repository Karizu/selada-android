package com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.history.DetailTransactionActivity;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembayaranSuksesActivity extends BaseActivity {

    private String order_no, total, whatToDo;
    private long nomBayar, mTotal, mKembalian;
    public int saldo = 0;
    private String mNomBayar, totals, order_date, order_time;

    @BindView(R.id.btnSelesai)
    LinearLayout btnSelesai;
    @BindView(R.id.btnPrintStruk)
    LinearLayout btnPrintStruk;
    @BindView(R.id.tvKembalian)
    TextView tvKembalian;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pembayaran_sukses;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pembayaran Sukses");

        Intent intent = getIntent();
        order_no = intent.getStringExtra("order_no");
        order_date = intent.getStringExtra("order_date");
        total = intent.getStringExtra("total");
        mKembalian = intent.getLongExtra("kembalian", 0);
        mNomBayar = intent.getStringExtra("nomBayar");
        totals = intent.getStringExtra("mTotal");
        order_time = intent.getStringExtra("order_time");
        whatToDo = null;
        try {
            whatToDo = intent.getStringExtra("whatToDo");
        } catch (Exception e){
            e.printStackTrace();
        }

        int noBayar = Integer.parseInt(mNomBayar);
        int nTotal = Integer.parseInt(totals);
        nomBayar = noBayar;
        mTotal = nTotal;
        tvKembalian.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(mKembalian)));
    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @OnClick(R.id.btnPrintStruk)
    void onClickBtnPrintStruk(){
        Intent intent = new Intent(PembayaranSuksesActivity.this, DetailTransactionActivity.class);
        intent.putExtra("order_no", order_no);
        intent.putExtra("total", total);
        intent.putExtra("order_date", order_date);
        intent.putExtra("order_time", order_time);
        intent.putExtra("whatToDo", Constant.DO_PRINT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.btnSelesai)
    void onClickBtnSelesai(){
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PembayaranSuksesActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}