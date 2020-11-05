package com.boardinglabs.mireta.selada.modul.old.splash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.entities.AppVersion;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.DataBaseHelper;
import com.boardinglabs.mireta.selada.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.selada.modul.ardi.HomeArdiActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview.TransactionReviewActivity;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.boardinglabs.mireta.selada.BuildConfig;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;
import com.boardinglabs.mireta.selada.modul.auth.login.LoginActivity;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dhimas on 11/23/17.
 */

public class SplashActivity extends AppCompatActivity {
    private String currentVersion;
    private ImageView splashOne;
    private String TID, MID, MerchantName, MerchantAddress;
    private String serviceId;
    private String mid = "";
    private String mobileNumber = "";
    private String nominal = "";
    private String amount = "";
    private String stan = "";
    private String json;
    private Boolean isSeladaPos = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        splashOne = (ImageView) findViewById(R.id.img_splash_1);

        try {
            isSeladaPos = PreferenceManager.getIsSeladaPos() != null ? PreferenceManager.getIsSeladaPos() : false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataBaseHelper helperDb = new DataBaseHelper(this.getApplicationContext());
        try {
            helperDb.createDataBase();
        } catch (IOException e) {
            Log.e("DB", "Cannot access database : " + e.getMessage());
//            e.printStackTrace();
        } finally {
            helperDb.close();
            helperDb = null;
        }

        Intent intent = getIntent();
        Log.d("SPLASH", "GET INTENT");
        if (intent.getExtras() != null) {
            Log.d("SPLASH", "GET EXTRA");

            try {
                json = intent.getExtras().getString("json");
                stan = intent.getExtras().getString("stan");
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Log.d("SPLASH BEFORE: SVCID", intent.getExtras().getString("serviceId"));
//            Log.d("SPLASH BEFORE: mid", intent.getExtras().getString("mid"));
//            Log.d("SPLASH BEFORE: MN", intent.getExtras().getString("mobileNumber"));
//            Log.d("SPLASH BEFORE: nominal", intent.getExtras().getString("nominal"));
//            Log.d("SPLASH BEFORE: amount", intent.getExtras().getString("amount"));
//            Log.d("SPLASH BEFORE: stan", intent.getExtras().getString("stan"));
            if (intent.getExtras().getString("serviceId") != null) {
                Log.d("SPLASH", "FOUND SERVICE ID");
                serviceId = intent.getExtras().getString("serviceId");
                mid = intent.getExtras().getString("mid");
                mobileNumber = intent.getExtras().getString("mobileNumber");
                nominal = intent.getExtras().getString("nominal");
                amount = intent.getExtras().getString("amount");
                stan = intent.getExtras().getString("stan");
            } else if (intent.getExtras().getString("TID") != null) {
                try {
                    TID = intent.getExtras().getString("TID");
                    MID = intent.getExtras().getString("MID");
                    MerchantName = intent.getExtras().getString("MerchantName");
                    MerchantAddress = intent.getExtras().getString("MerchantAddress");
                } catch (Exception e) {
                }
            }
        }

//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();w
//
//        //To displaying token on logcat
//        Log.d("kunamTOKEN: ", refreshedToken);
//        Log.d("kunamTOKEN2: ", String.valueOf(FirebaseInstanceId.getInstance().getToken()));

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String newToken = instanceIdResult.getToken();
//                Log.d("kunamTOKEN: ", newToken);
//            }
//        });

        String uniqueID = UUID.randomUUID().toString();
        if (TextUtils.isEmpty(PreferenceManager.getImei())) {
//            PreferenceManager.setImei(uniqueID);
//            if(FirebaseInstanceId.getInstance().getToken().length() > 0){
//                PreferenceManager.setImei(refreshedToken);
//            }else{
            PreferenceManager.setImei(uniqueID);
//            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("promo");
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new GetVersionCode().execute();
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            String newVersion;
            try {

                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

                return newVersion;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null) {
                String newVersion = onlineVersion;
                if (newVersion != null && !newVersion.isEmpty()) {
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("production")
                            || BuildConfig.FLAVOR.equalsIgnoreCase("postrelease")
                            || BuildConfig.FLAVOR.equalsIgnoreCase("productionpampasy")) {
                        if (!newVersion.equalsIgnoreCase(currentVersion)) {
                            DialogPlus dialog = DialogPlus.newDialog(SplashActivity.this)
                                    .setContentHolder(new ViewHolder(R.layout.content_dialog))
                                    .setCancelable(false)
                                    .setGravity(Gravity.BOTTOM)
                                    .setExpanded(false)
                                    .setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(DialogPlus dialog, View view) {
                                            switch (view.getId()) {
                                                case R.id.token_pln:
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                    finish();
                                                    break;
                                                case R.id.tagihan_pln:
                                                    break;
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setOverlayBackgroundResource(R.color.starDust_opacity_90)
                                    .create();
                            View view = dialog.getHolderView();
                            TextView title = (TextView) view.findViewById(R.id.title);
                            Button first = (Button) view.findViewById(R.id.tagihan_pln);
                            Button second = (Button) view.findViewById(R.id.token_pln);

                            title.setText("Versi terbaru sudah tersedia, silahkan lakukan pembaharuan");
                            first.setVisibility(View.GONE);
                            second.setText("Ya");
                            dialog.show();
                        } else {
                            downTimer();
                        }
                    } else {
                        downTimer();
                    }

                } else {
                    downTimer();
                }
            } else {
                downTimer();
            }

        }
    }

    private void downTimer() {
        long futureMillis = TimeUnit.SECONDS.toMillis(2);
        new CountDownTimer(futureMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                if (seconds == 0) {
                    cancel();
                    onFinish();
                }
            }

            @Override
            public void onFinish() {
                if (PreferenceManager.isLogin()) {
                    gotoHomePage();
                } else {
                    if (PreferenceManager.isArdi()) {
                        gotoHomePageArdi();
                    } else {
                        gotoLoginPage();
                    }
                }
            }
        }.start();
    }

    private void gotoLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoHomePageArdi() {
        Intent intent = new Intent(this, HomeArdiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoHomePage() {
        Log.d("SPLASH", "GET TOHOME PAGE");
        if (TID != null) {
            Log.d("SPLASH", "TID FOUND");
            Intent intent = new Intent(this, SeladaLaucherActivity.class);
            intent.putExtra("TID", TID);
            intent.putExtra("MID", MID);
            intent.putExtra("MerchantName", MerchantName);
            intent.putExtra("MerchantAddress", MerchantAddress);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (json != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("json", json);
            intent.putExtra("stan", stan);
            startActivity(intent);
            finish();
        } else {
            Log.d("SPLASH", "TID NOT FOUND");
            if (serviceId != null) {
                Log.d("SPLASH", "SVCID FOUND");
                Intent intent = new Intent(this, SeladaLaucherActivity.class);
                intent.putExtra("serviceId", serviceId);
                intent.putExtra("mid", mid);
                intent.putExtra("mobileNumber", mobileNumber);
                intent.putExtra("nominal", nominal);
                intent.putExtra("amount", amount);
                intent.putExtra("stan", stan);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, SeladaLaucherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }
    }

}
