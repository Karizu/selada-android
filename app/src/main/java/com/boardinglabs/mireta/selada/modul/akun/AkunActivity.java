package com.boardinglabs.mireta.selada.modul.akun;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.selada.modul.akun.rfid.RfidActivity;
import com.boardinglabs.mireta.selada.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.selada.modul.master.categories.CategoryActivity;
import com.boardinglabs.mireta.selada.modul.master.katalog.KatalogActivity;
import com.boardinglabs.mireta.selada.modul.master.profil.toko.ProfilTokoActivity;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.TambahBarangActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.voids.VoidActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AkunActivity extends BaseActivity implements KeyEvent.Callback {

    private String codeHolder = "";
    private Dialog dialog;
    private Context context;

    @BindView(R.id.btnVoidArdi)
    CardView btnVoidArdi;

    @OnClick(R.id.btnPengaturanAkun)
    void onClickbtnUbahAkun() {
        startActivity(new Intent(AkunActivity.this, PengaturanAkunActivity.class));
    }

    @OnClick(R.id.btnProfilToko)
    void onClickbtnProfilToko() {
        startActivity(new Intent(AkunActivity.this, ProfilTokoActivity.class));
    }

    @OnClick(R.id.btnKatalog)
    void onClickbtnKatalog() {
        startActivity(new Intent(AkunActivity.this, KatalogActivity.class));
    }

    @OnClick(R.id.btnKategori)
    void onClickbtnKategori() {
        startActivity(new Intent(AkunActivity.this, CategoryActivity.class));
    }

    @OnClick(R.id.btnTambahBarang)
    void onClickbtnTambahBarang() {
        startActivity(new Intent(AkunActivity.this, TambahBarangActivity.class));
    }

    @OnClick(R.id.btnVoidArdi)
    void onClickbtnVoidArdi() {
        startActivity(new Intent(AkunActivity.this, VoidActivity.class));
    }

    @OnClick(R.id.btnTestRFID)
    void onClickTestRFID(){
        startActivity(new Intent(AkunActivity.this, RfidActivity.class));
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_akun;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pengaturan");
        context = this;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.i("KEY", "Code : " + keyCode + ", Event : " + event);
        if (keyCode == 232) {
            return false;
        }
        if (codeHolder.length() >= 6) {
            codeHolder = "";
        }
        if (keyCode == 56) {
            codeHolder = "";
        }
        if (keyCode == 67) {
            if (codeHolder.length() > 0) {
                codeHolder = codeHolder.substring(0, codeHolder.length() - 1);
            }
        }
        if (keyCode > 6 && keyCode < 17) {
            codeHolder += String.valueOf(keyCode - 7);
        }
        if (codeHolder.equals("147258")) {
            btnVoidArdi.setVisibility(View.VISIBLE);
        }
        if (codeHolder.equals("8888")) {
            btnVoidArdi.setVisibility(View.GONE);
        }
        if (codeHolder.equals("258369")) {
            startActivity(new Intent(AkunActivity.this, HomeArdiActivity.class));
        }
        if (keyCode == 7) {
            Log.d("CD", codeHolder);
        }
        return super.onKeyDown(keyCode, event);
    }
}
