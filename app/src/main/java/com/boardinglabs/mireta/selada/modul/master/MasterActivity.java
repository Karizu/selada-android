package com.boardinglabs.mireta.selada.modul.master;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.modul.history.HistoryActivity;
import com.boardinglabs.mireta.selada.modul.master.brand.BrandActivity;
import com.boardinglabs.mireta.selada.modul.master.categories.CategoryActivity;
import com.boardinglabs.mireta.selada.modul.master.katalog.KatalogActivity;
import com.boardinglabs.mireta.selada.modul.master.laporan.LaporanActivity;
import com.boardinglabs.mireta.selada.modul.master.profil.toko.ProfilTokoActivity;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.TambahBarangActivity;
import com.jakewharton.rxbinding.view.RxView;

import rx.functions.Action1;

public class MasterActivity extends BaseActivity {
    private Button kategoriButton;
    private Button brandButton;
    private Button inventoriButton;
    private Button laporanButton;
    private Button profilTokoButton;
    private Button historyTrxButton;
    private Button printerTest;
    private Button tambah_barang_button;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_master;
    }

    @Override
    protected void setContentViewOnChild() {
        setToolbarTitle("Data Master");

        kategoriButton = (Button) findViewById(R.id.kategori_button);
        brandButton = (Button) findViewById(R.id.brand_button);
        inventoriButton = (Button) findViewById(R.id.inventory_button);
        laporanButton = (Button) findViewById(R.id.laporan_button);
        profilTokoButton = (Button) findViewById(R.id.profil_toko_button);
        historyTrxButton = (Button) findViewById(R.id.trx_button);
        printerTest = (Button) findViewById(R.id.test_printer);
        tambah_barang_button = (Button) findViewById(R.id.tambah_barang_button);

        //hide menu
        brandButton.setVisibility(View.GONE);
        printerTest.setVisibility(View.GONE);

        RxView.clicks(kategoriButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, CategoryActivity.class));
            }
        });

        RxView.clicks(tambah_barang_button).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, TambahBarangActivity.class));
            }
        });

        RxView.clicks(brandButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, BrandActivity.class));
            }
        });
        RxView.clicks(inventoriButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, KatalogActivity.class));
            }
        });
        RxView.clicks(laporanButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, LaporanActivity.class));
            }
        });
        RxView.clicks(profilTokoButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(MasterActivity.this, ProfilTokoActivity.class));
            }
        });
        RxView.clicks(historyTrxButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(MasterActivity.this, HistoryActivity.class);
                intent.putExtra("tag", "1");
                startActivity(intent);
            }
        });
//        RxView.clicks(printerTest).subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                Intent intent = new Intent(MasterActivity.this, PrinterActivity.class);
//                startActivity(intent);
//            }
//        });
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
