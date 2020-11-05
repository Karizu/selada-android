package com.boardinglabs.mireta.selada.modul.ardi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaldoActivity extends BaseActivity {

    private String order_no, total, whatToDo,sisaSaldo, member_name, member_lulusan, member_angkatan;
    private long nomBayar, mTotal;
    private String mNomBayar, totals, order_date;

    @BindView(R.id.tvSaldo)
    TextView tvSaldo;
    @BindView(R.id.memberName)
    TextView memberName;
    @BindView(R.id.memberLulusan)
    TextView memberLulusan;
    @BindView(R.id.memberAngkatan)
    TextView memberAngkatan;

    @OnClick(R.id.btnSelesai)
    void onClickSelesai(){
        Intent intent = new Intent(SaldoActivity.this, HomeArdiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_saldo;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("SALDO");

        Intent intent = getIntent();
        sisaSaldo = intent.getStringExtra("sisaSaldo");
        member_name = intent.getStringExtra("member_name");
        member_lulusan = intent.getStringExtra("member_lulusan");
        member_angkatan = intent.getStringExtra("member_angkatan");
        int mSisaSaldo = Integer.parseInt(sisaSaldo);

        tvSaldo.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mSisaSaldo));
        memberName.setText("Nama: "+member_name);
        memberLulusan.setText("Lulusan: "+member_lulusan);
        memberAngkatan.setText("Angkatan "+member_angkatan);
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
