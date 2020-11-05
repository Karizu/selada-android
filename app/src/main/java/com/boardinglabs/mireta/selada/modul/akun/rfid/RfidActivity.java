package com.boardinglabs.mireta.selada.modul.akun.rfid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RfidActivity extends AppCompatActivity {

    public final String TAG = "BRIZZI";
    public static final String cardType = "BRIZZI CARD (FLY)";
    private Context context;
    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams") TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;
    private String UID = "";

    @BindView(R.id.tvUID)
    TextView tvUID;

    @SuppressLint("HandlerLeak")
    public Handler handlerTest = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            tvUID.setText(UID);

            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);
        ButterKnife.bind(this);
        context = this;

        li = LayoutInflater.from(context);
        promptsView = (TapCard)li.inflate(R.layout.tap_card, null);

        alertDialogBuilder = new AlertDialog.Builder(context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertTaps = alertDialogBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(alertTaps.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
                promptsView.init(handlerTest);
                promptsView.searchBegin();
                promptsView.setOkListener(v -> {
                    alertTaps.dismiss();
                    promptsView.searchEnd();
                    ((Activity) context).finish();
                });

                alertTaps.show();
                alertTaps.getWindow().setAttributes(lp);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
