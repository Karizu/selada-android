package com.boardinglabs.mireta.selada.modul.biller.selada.ppobpascabayar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PpobPascabayarActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.hometoolbar_title)
    TextView hometoolbar_title;
    @BindView(R.id.hometoolbar_logo)
    ImageView hometoolbar_logo;
    @BindView(R.id.layoutToolbarName)
    LinearLayout layoutToolbarName;

    @OnClick(R.id.menuPulsaRegular)
    void menuPulsaRegular(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuTelkomSpeedy)
    void menuTelkomSpeedy(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuAsuransi)
    void menuAsuransi(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuISP)
    void menuISP(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuKartuKredit)
    void menuKartuKredit(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuMultifinance)
    void menuMultifinance(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuPulsaPascabayar)
    void menuPulsaPascabayar(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.menuTvBerlangganan)
    void menuTvBerlangganan(){
        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack() {
        onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppob_pascabayar);
        ButterKnife.bind(this);
        context = this;

        hometoolbar_logo.setVisibility(View.GONE);
        layoutToolbarName.setVisibility(View.VISIBLE);
        hometoolbar_title.setText("PPOB Pascabayar");
    }
}
