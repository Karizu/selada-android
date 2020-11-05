package com.boardinglabs.mireta.selada.modul.lakupandai;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeLakuPandaiActivity extends AppCompatActivity {

    private Context context;

    @OnClick(R.id.menuCekSaldo)
    void menuCekSaldo(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuCekMutasi)
    void menuCekMutasi(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuSetorPinjaman)
    void menuSetorPinjaman(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuSetorTabungan)
    void menuSetorTabungan(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuTarikTunai)
    void menuTarikTunai(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuVoidTarikTunai)
    void menuVoidTarikTunai(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuTransferSesama)
    void menuTransferSesama(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuTransferAntarbank)
    void menuTransferAntarbank(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuReprint)
    void menuReprint(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuReportTransaksi)
    void menuReportTransaksi(){
//        Intent intent = new Intent(this, PpobPascabayarActivity.class);
//        startActivity(intent);
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_laku_pandai);
        ButterKnife.bind(this);
        context = this;

    }
}
