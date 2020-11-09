package com.boardinglabs.mireta.selada.modul.selada.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.boardinglabs.mireta.selada.BuildConfig;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.dialog.CustomProgressBar;
import com.boardinglabs.mireta.selada.component.network.ApiSelada;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.AppVersion;
import com.boardinglabs.mireta.selada.component.network.entities.Merchant;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.DownloadTask;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.akun.pengaturan.PengaturanAkunActivity;
import com.boardinglabs.mireta.selada.modul.auth.login.LoginActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.HomeBillerActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.DetailTransactionHistoryActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.TransactionHistoryActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.purchase.PurchaseActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview.TransactionReviewPresenter;
import com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview.TransactionReviewPresenterImpl;
import com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview.TransactionReviewView;
import com.boardinglabs.mireta.selada.modul.bjb.common.CommonConfig;
import com.boardinglabs.mireta.selada.modul.history.HistoryActivity;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.StokActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.ItemsActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.boardinglabs.mireta.selada.component.util.Utils.IV;
import static com.boardinglabs.mireta.selada.component.util.Utils.decodeToBytes;
import static com.boardinglabs.mireta.selada.component.util.Utils.decrypt;
import static com.boardinglabs.mireta.selada.component.util.Utils.encodeToString;
import static com.boardinglabs.mireta.selada.component.util.Utils.encrypt;
import static com.boardinglabs.mireta.selada.component.util.Utils.stringToBytes;

public class SeladaLaucherActivity extends AppCompatActivity implements CommonInterface, TransactionReviewView {

    public static CustomProgressBar progressBar = new CustomProgressBar();
    private Merchant merchant;
    private byte[] rToken, keyGen;

    @Override
    public void showProgressLoading() {
        progressBar.show(this, "Pembayaran anda sedang dalam proses", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getSeladaCoreInstance();
    }


    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);

        //Intent to Purchase
//        HashMap<String, String> data = new HashMap<String, String>();
//        data.put("menu", "REVERSALEFROMSELADA");
//        data.put("stan", stan);
//        Utils.openApp(this, "id.co.tornado.billiton", data);
//        if (msg.equalsIgnoreCase(Constant.EXPIRED_SESSION) || msg.equalsIgnoreCase(Constant.EXPIRED_ACCESS_TOKEN)) {
//            goToLoginPage1(this);
//        }
//        else{
//            onBackPressed();
//        }
    }

    @Override
    public void onSuccessGetBalance(String balance) {

    }

    @Override
    public void onSuccessRegisterPremium(MessageResponse mResponse) {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSuccessPayTransaction(GTransaction transaction) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PurchaseActivity.ORDER_ID, transaction.id);
        resultIntent.putExtra(PurchaseActivity.TOTAL_AMOUNT, transaction.default_price);
        resultIntent.putExtra(PurchaseActivity.NOTE, transaction.service.name + ", " + transaction.customer_no);
        resultIntent.putExtra(PurchaseActivity.DATA, transaction.data);
        resultIntent.putExtra(PurchaseActivity.DATE_TRANSACTION, transaction.created_at);
//        resultIntent.putExtra(PurchaseActivity.SERVICE_PROVIDER, positionRequest);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSuccessSeladaPayTransaction(GSeladaTransaction transaction) {
        Intent intent = new Intent(this, DetailTransactionHistoryActivity.class);
        intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_ID, String.valueOf(transaction.id));
        intent.putExtra("stan", stan);
        startActivity(intent);
    }

    @Override
    public void onSuccessCheckReferral(String refferalId) {

    }

    @Override
    public void chargeAmount(String totalAmount, String fee) {

    }

    @Override
    public void charge(QRTransactionResponse json) {

    }

    private Context context;
    private ProgressDialog mProgressDialog;
    final public static String INTENT_EXTRA_NOTIFICATION = "fromNotification";

    @BindView(R.id.textViewTID)
    TextView textViewTID;
    @BindView(R.id.textViewMID)
    TextView textViewMID;
    @BindView(R.id.textView4)
    TextView textViewMerchantName;
    @BindView(R.id.textViewMName)
    TextView textViewAlamat;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout pullRefresh;
    private Dialog dialog;
    private String TID, MID, MerchantName, MerchantAddress;

    private String serviceId;
    private String mid = "";
    private String mobileNumber = "";
    private String nominal = "";
    private String amount = "";
    private String stan = "";


    private TransactionReviewPresenter mPresenter;


    @OnClick(R.id.menuInfo)
    void onClickTopup() {
//        Intent intent = new Intent(this, TopupSeladaActivity.class);
//        startActivity(intent);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "MA00065");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @OnClick(R.id.menuTopupHistory)
    void onClickTopupHistory() {
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menuSetting)
    void onClickSettings() {
        Intent intent = new Intent(this, PengaturanAkunActivity.class);
        startActivity(intent);
    }

    //POS

    @OnClick(R.id.btnTransaksiBaru)
    void onClickTransaksiBaru() {
        Intent intent = new Intent(this, ItemsActivity.class);
        startActivity(intent);
//        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnInventory)
    void onClickInventory() {
        Intent intent = new Intent(this, StokActivity.class);
        startActivity(intent);
//        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnHistoryPos)
    void onClickHistoryPos() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
//        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnLainnyaPos)
    void onClickLainnyaPos() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
//        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    //PPOB

    @OnClick(R.id.btnPulsa)
    void onClickPulsa() {
        gotoPurchaseActivity(Constant.PULSA_HANDPHONE);
    }

    @OnClick(R.id.btnPaketData)
    void onClickPaketData() {
        gotoPurchaseActivity(Constant.INTERNET_DATA);
    }

    @OnClick(R.id.btnHistoryPpob)
    void onClickHistoryPpob() {
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnLainnyaPpob)
    void onClickLainnyaPpob() {
        Intent intent = new Intent(this, HomeBillerActivity.class);
        startActivity(intent);
    }

    //LAKUPANDAI

    @OnClick(R.id.btnTarikTunai)
    void onClickTarikTunai() {
//        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(this, HomeLakuPandaiActivity.class);
//        startActivity(intent);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "MA00015");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @OnClick(R.id.btnSetorTabungan)
    void onClickSetorTabungan() {
//        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "MA00040");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @OnClick(R.id.btnReportTransaksi)
    void onClickReportTransaksi() {
//        Toast.makeText(context, "Segera Hadir", Toast.LENGTH_SHORT).show();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "RMA0010");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @OnClick(R.id.btnLainnyaLakuPandai)
    void onClickLainnyaLakupandai() {
//        Intent intent = new Intent(this, HomeLakuPandaiActivity.class);
//        startActivity(intent);

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "S000025");
        Utils.openApp(this, "id.co.tornado.billiton", data);
    }

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selada_laucher);
        ButterKnife.bind(this);
        context = this;
        mPresenter = new TransactionReviewPresenterImpl(this, this);
        merchant = PreferenceManager.getMerchant();
        if (merchant != null){
            String serialNumber = "";

            if (merchant.getTerminal() != null && merchant.getTerminal().getSerialNumber() != null){
                serialNumber = merchant.getTerminal().getSerialNumber();
            }

            //check serial number
            if (!Build.SERIAL.equals(serialNumber)) {
                Log.d("HOME", "SN TIDAK SESUAI");
                dialog = MethodUtil.showDialog(this, R.layout.layout_confirm_update_setting, "Peringatan, anda tidak memiliki akses terhadap EDC ini");
                Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
                btnUpdate.setOnClickListener(v -> {
                    dialog.dismiss();
                    doLogout();
                });
            } else {
                if (PreferenceManager.getTID() != null) {
                    if (!PreferenceManager.getTID().equals(merchant.getTerminal().getTid())){
                        Log.d("HOME", "TID TIDAK SESUAI");
                        PreferenceManager.setTID(merchant.getTerminal().getTid());
                        PreferenceManager.setMID(merchant.getMid());
                        PreferenceManager.setMerchantName(merchant.getTerminal().getMerchantName());
                        PreferenceManager.setMerchantAddress(merchant.getAddress());

                        //DECRYPT HEULA TONG POHO
//                        PreferenceManager.setIP(merchant.getIp());

                        HashMap<String, String> data = new HashMap<String, String>();
                        data.put("menu", "setting");
                        data.put("tid", merchant.getTerminal().getTid());
                        data.put("mids", merchant.getMid());
                        data.put("mn", merchant.getTerminal().getMerchantName());
                        data.put("ma", merchant.getAddress());
                        data.put("ct", merchant.getCity());
                        data.put("sid", merchant.getScreenId());
//                        data.put("ip", PreferenceManager.getIP());

                        Utils.openApp(this, "id.co.tornado.billiton", data);
                    } else if (PreferenceManager.getIsSeladaPos()!=null?PreferenceManager.getIsSeladaPos():false){
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d("HOME", "TID BELOM DIISI");
                    PreferenceManager.setTID(merchant.getTerminal().getTid());
                    PreferenceManager.setMID(merchant.getMid());
                    PreferenceManager.setMerchantName(merchant.getTerminal().getMerchantName());
                    PreferenceManager.setMerchantAddress(merchant.getAddress());

                    //DECRYPT HEULA TONG POHO
//                    PreferenceManager.setIP(merchant.getIp());

                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("menu", "setting");
                    data.put("tid", merchant.getTerminal().getTid());
                    data.put("mids", merchant.getMid());
                    data.put("mn", merchant.getTerminal().getMerchantName());
                    data.put("ma", merchant.getAddress());
                    data.put("ct", merchant.getCity());
                    data.put("sid", merchant.getScreenId());
//                    data.put("ip", PreferenceManager.getIP());
                    Utils.openApp(this, "id.co.tornado.billiton", data);
                }

                textViewTID.setText(merchant.getTerminal().getTid());
                textViewMID.setText(merchant.getMid());
                textViewMerchantName.setText(merchant.getTerminal().getMerchantName());
                textViewAlamat.setText(merchant.getAddress());
            }
        }
        else{
            Log.d("HOME", "MERCHANT TIDAK DITEMUKAN");
            dialog = MethodUtil.showDialog( this, R.layout.layout_confirm_update_setting, "Peringatan, anda tidak terdaftar sebagai Agen");
            Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
            btnUpdate.setOnClickListener(v -> {
                dialog.dismiss();
                doLogout();
            });
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

//        try {
//            Intent intent = getIntent();
//            if (intent.getStringExtra("TID") != null) {
//                TID = intent.getStringExtra("TID");
//                MID = intent.getStringExtra("MID");
//                MerchantName = intent.getStringExtra("MerchantName");
//                MerchantAddress = intent.getStringExtra("MerchantAddress");
//                PreferenceManager.setTID(TID);
//                PreferenceManager.setMID(MID);
//                PreferenceManager.setMerchantName(MerchantName);
//                PreferenceManager.setMerchantAddress(MerchantAddress);
//
//                textViewTID.setText(TID);
//                textViewMID.setText(MID);
//                textViewMerchantName.setText(MerchantName);
//                textViewAlamat.setText(MerchantAddress);
//            } else {
//                if (PreferenceManager.getTID() != null) {
//                    textViewTID.setText(PreferenceManager.getTID());
//                    textViewMID.setText(PreferenceManager.getMID());
//                    textViewMerchantName.setText(PreferenceManager.getMerchantName());
//                    textViewAlamat.setText(PreferenceManager.getMerchantAddress());
//                } else {
//                    HashMap<String, String> data = new HashMap<String, String>();
//                    data.put("menu", "setting");
//                    Utils.openApp(this, "id.co.tornado.billiton", data);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        getDetailMember();
        pullRefresh.setEnabled(false);
        pullRefresh.setOnRefreshListener(() -> {

            pullRefresh.setRefreshing(false);
        });
        pullRefresh.setColorSchemeResources(R.color.primaryAccentBlue);
//        swipeRefresh.setOnRefreshListener(this::getDetailMember);

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading Software");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        try {
            Log.d("getIntent", getIntent().getIntExtra("update", 0)+"");
            if (getIntent().getIntExtra("update", 0) == 1){
                checkVersion(1);
            } else {
                checkVersion(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Log.d("HOME", "GET INTENT");

        if (intent.getExtras() != null) {
            Log.d("HOME", "GET EXTRA");
            if (intent.getExtras().getString("serviceId") != null) {
                Log.d("HOME", "FOUND SERVICE ID");

                launchKey();

                serviceId = intent.getExtras().getString("serviceId");
                mid = intent.getExtras().getString("mid");
                mobileNumber = intent.getExtras().getString("mobileNumber");
                nominal = intent.getExtras().getString("nominal");
                amount = intent.getExtras().getString("amount");
                stan = intent.getExtras().getString("stan");

                Log.d("PAY serviceId", serviceId);
                Log.d("PAY merchantId", mid);
                Log.d("PAY merchantNo", mobileNumber);
                Log.d("PAY totalPrice", nominal);
                Log.d("PAY billerPrice", amount);

                Log.d("HOME", "SVCID FOUND");
//                Intent newintent = new Intent(this, TransactionReviewActivity.class);
//                newintent.putExtra("serviceId", serviceId);
//                newintent.putExtra("mid", mid);
//                newintent.putExtra("mobileNumber", mobileNumber);
//                newintent.putExtra("nominal", nominal);
//                newintent.putExtra("amount", amount);
//                newintent.putExtra("stan", stan);
//                startActivity(newintent);

//                mPresenter.paySeladaTransaction(serviceId, mid, mobileNumber, nominal, amount, stan);
                Toast.makeText(SeladaLaucherActivity.this, "Transaksi anda sedang dalam proses", Toast.LENGTH_LONG).show();
                ApiSelada.apiInterface().createSeladaTrx(serviceId, mid, mobileNumber, nominal, amount, "", stan, "Bearer "+PreferenceManager.getSessionToken(), encodeToString(rToken), encodeToString(keyGen), Constant.KEY_VERSION).enqueue(new Callback<SeladaTransactionResponse>() {
                    @Override
                    public void onResponse(Call<SeladaTransactionResponse> call, Response<SeladaTransactionResponse> response) {
                        try {
//                            progressBar.getDialog().dismiss();
//                            Toast.makeText(SeladaLaucherActivity.this, "111111111111111", Toast.LENGTH_LONG).show();
                            if (response.isSuccessful()) {
//                                Toast.makeText(SeladaLaucherActivity.this, "2222222222222222", Toast.LENGTH_LONG).show();
                                SeladaTransactionResponse gSeladaTransaction = response.body();
//                                Toast.makeText(SeladaLaucherActivity.this, "333333333333333333", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(SeladaLaucherActivity.this, DetailTransactionHistoryActivity.class);
//                                Toast.makeText(SeladaLaucherActivity.this, "44444444444444444", Toast.LENGTH_LONG).show();
                                intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_ID, String.valueOf(gSeladaTransaction.data.id));
//                                Toast.makeText(SeladaLaucherActivity.this, "555555555555555555555", Toast.LENGTH_LONG).show();
                                intent.putExtra("stan", stan);
                                intent.putExtra("intent", "INTENT_FROM_PAY");
//                                Toast.makeText(SeladaLaucherActivity.this, "6666666666666666666666", Toast.LENGTH_LONG).show();
                                startActivity(intent);
//                                Toast.makeText(SeladaLaucherActivity.this, "77777777777777777777777", Toast.LENGTH_LONG).show();
                            } else {
//                                sendReversalAdviceSale(stan);
                                String resp = null;
                                try {
                                    resp= MethodUtil.getResponseError(Objects.requireNonNull(response.errorBody()).toString());
                                } catch (Exception e){e.printStackTrace();}
                                String msg = resp!=null?resp:"";
                                Toast.makeText(SeladaLaucherActivity.this, "Transaksi anda gagal di proses \n" + msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {

                            Toast.makeText(SeladaLaucherActivity.this, "Transaksi anda gagal di proses", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<SeladaTransactionResponse> call, Throwable t) {
                        Toast.makeText(SeladaLaucherActivity.this, "Transaksi anda gagal di proses, coba lagi nanti", Toast.LENGTH_LONG).show();
//                        progressBar.getDialog().dismiss();
//                        sendReversalAdviceSale(stan);
                        t.printStackTrace();
                    }
                });


//                        .enqueue(new Callback<ApiResponse<GSeladaTransaction>>() {
//                    @Override
//                    public void onResponse(Call<SeladaTransactionResponse> call, Response<SeladaTransactionResponse> response) {
//                        try {
//                            progressBar.getDialog().dismiss();
////                            if (response.isSuccessful()){
//                                GSeladaTransaction gSeladaTransaction = response.body().getData();
//                                Toast.makeText(SeladaLaucherActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//
//                                Intent intent = new Intent(SeladaLaucherActivity.this, DetailTransactionHistoryActivity.class);
//                                intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_ID, gSeladaTransaction.id);
//                                intent.putExtra("stan", stan);
//                                startActivity(intent);
////                            }
//                        } catch (Exception e){
//
//                            Toast.makeText(SeladaLaucherActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<GSeladaTransaction>> call, Throwable t) {
//                        progressBar.getDialog().dismiss();
//                        t.printStackTrace();
//                    }
//                });
            }
        }
    }

    private void launchKey(){
        if (merchant!=null){
            try {
                String serialNumber = merchant.getTerminal().getSerialNumber();
                String imei = merchant.getTerminal().getImei();
                String iccid = merchant.getTerminal().getIccid();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("ssmmHHyyyyMMdd");
                String timeDate = s.format(new Date());
                String primToken = imei+iccid+timeDate;
                String secToken = serialNumber+timeDate+"SL";

                try {
                    //KEY FOR DECRYPT RTOKEN
                    //KEY SEND TO SERVER
                    keyGen = encrypt(stringToBytes(secToken), stringToBytes(serialNumber), IV());
//                    Log.d("KEYGENSEC", encodeToString(keyGen));
                    String encKeyGen = encodeToString(keyGen);
                    byte[] decKeyGen = decodeToBytes(encKeyGen);
//                    Log.d("DECRYPT KEYGENSEC", decrypt(decKeyGen, stringToBytes(serialNumber), IV()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //RTOKEN ENCRYPTED
                    //SEND TO SERVER
                    rToken = encrypt(stringToBytes(primToken), stringToBytes(secToken), IV());
//                    Log.d("KEYGENPRIM", encodeToString(rToken));
//                Log.d("DECRYPT KEYGENPRIM", decrypt(rToken, stringToBytes(secToken), IV()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doLogout() {
            PreferenceManager.logOut();
            Intent intent = new Intent(SeladaLaucherActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
    }

//    public void getDetailMember(){
//        Loading.show(context);
//        Api.apiInterface().cekSaldo(PreferenceManager.getMemberId(),  "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
//                Loading.hide(context);
//                try {
//                    if (response.isSuccessful()){
//                        Members members = response.body().getData();
//                        textViewTID.setText(members.getFullname());
//                        long balance = Long.parseLong(members.getBalance());
//                        textViewMID.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(balance));
//
//                        PreferenceManager.setMerchantId(members.getMerchantId());
//                    } else {
//                        try {
//                            Toast.makeText(context,
//                                    MethodUtil.getResponseError(Objects.requireNonNull(response.errorBody()).string()),
//                                    Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Members>> call, Throwable t) {
//                Loading.hide(context);
//                t.printStackTrace();
//            }
//        });
//    }

    private void checkVersion(int update) {
        ApiSelada.apiInterface().checkVersion("Bearer " + PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<AppVersion>>() {
            @Override
            public void onResponse(Call<ApiResponse<AppVersion>> call, Response<ApiResponse<AppVersion>> response) {
                try {
                    AppVersion appVersion = response.body().getData();
                    if (!appVersion.getAndroid_version().equals(BuildConfig.VERSION_NAME) && !mProgressDialog.isShowing()) {
                        Log.d("MASUK LOG VERSION", appVersion.getAndroid_version());
//                        if (update == 1) {
                            showDialog();
                            Button buttonUpdate = dialog.findViewById(R.id.btnUpdate);
//                            Button buttonNanti = dialog.findViewById(R.id.btnNanti);

                            buttonUpdate.setOnClickListener(v -> {
                                final DownloadTask downloadTask = new DownloadTask(context, mProgressDialog, "bjb-app"){
                                    @Override
                                    protected void onPostExecute(String result) {
                                        super.onPostExecute(result);
                                        final DownloadTask downloadTask = new DownloadTask(context, mProgressDialog, "selada-app");
                                        downloadTask.execute(appVersion.getAndroid_link());

                                        mProgressDialog.setOnCancelListener(dialog -> {
                                            downloadTask.cancel(false); //cancel the task
                                        });

                                    }
                                };
                                downloadTask.execute(appVersion.getAndroid_bjb_link());

//                            downloadTask.execute("https://srv-file5.gofile.io/download/7J1fwQ/app-selada.apk");
                                mProgressDialog.setOnCancelListener(dialog -> {
                                    downloadTask.cancel(false); //cancel the task
                                });
                                dialog.dismiss();
                            });

//                            buttonNanti.setOnClickListener(v -> {
//                                dialog.dismiss();
//                            });
//                        } else {
//                            showNotification("Update Software", "Sorftware baru telah tersedia, silahkan lakukan update");
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AppVersion>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("2.2",
                    "Selada",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Selada Toko Segala Ada");
            Objects.requireNonNull(mNotificationManager).createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "2.2")
                .setSmallIcon(R.drawable.ic_selada_notif) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setOngoing(true)
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), SeladaLaucherActivity.class);
        intent.putExtra("update", 1);
        intent.putExtra(INTENT_EXTRA_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void showDialog() {
        dialog = new Dialog(Objects.requireNonNull(SeladaLaucherActivity.this));
        //set content
        dialog.setContentView(R.layout.layout_confirm_download_software);
        dialog.setTitle("Download Software");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.very_light_pink)));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void gotoPurchaseActivity(int position) {
        Intent intent = new Intent(context, PurchaseActivity.class);
        intent.putExtra(Constant.POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getDetailMember();
        try {
            Log.d("getIntent", getIntent().getIntExtra("update", 0)+"");
            if (getIntent().getIntExtra("update", 0) == 1){
                checkVersion(1);
            } else {
                checkVersion(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("TAG", "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);//needed so that getIntent() doesn't return null inside onResume()
    }

    public void sendReversalAdviceSale(String stan) {

//        dialog = ProgressDialog.show(context, "Reversal", "Sedang Mengirim Reversal", true);
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences preferences = context.getSharedPreferences(CommonConfig.SETTINGS_FILE, Context.MODE_PRIVATE);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final JSONObject msg = new JSONObject();
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            final String msgId = telephonyManager.getDeviceId() + sdf.format(new Date());
            msg.put("msg_id", msgId);
            msg.put("msg_ui", telephonyManager.getDeviceId());
            // Reversal Sale
            msg.put("msg_si", "R82561");
            msg.put("msg_dt", stan);

            final JSONObject msgRoot = new JSONObject();
            msgRoot.put("msg", msg);
            String hostname = preferences.getString("hostname", CommonConfig.HTTP_REST_URL);
            String postpath = preferences.getString("postpath", CommonConfig.POST_PATH);
            String httpPost = CommonConfig.HTTP_PROTOCOL + "://" + hostname + "/" + postpath;

            StringRequest jor = new StringRequest(Request.Method.POST,
                    httpPost,
                    response -> {
                        try {
                            Log.d("TERIMA", response);
                            JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[{\"visible\":true,\"comp_values\":{\"comp_value\":[{\"print\":\"Reversal Berhasil Dikirim\",\n" +
                                    "\"value\":\"Reversal Berhasil Dikirim\"}]},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
                                    "\"type\":\"3\",\"title\":\"Reversal\"}}");
                            Toast.makeText(context, "Reversal Berhasil Dikirim", Toast.LENGTH_SHORT).show();

//                                processResponse(rps, msgId);
//                                dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            try {
                                Toast.makeText(context, "Request Timeout",
                                        Toast.LENGTH_LONG).show();
                                JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[" +
                                        "{\"visible\":true,\"comp_values\":{\"comp_value\":[" +
                                        "{\"print\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\",\n" +
                                        "\"value\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\"}]" +
                                        "},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
                                        "\"type\":\"3\",\"title\":\"Gagal\"}}");

                                Toast.makeText(context, "Transaksi ditolak\nTidak dapat mengirim Reversal", Toast.LENGTH_SHORT).show();

    //                            processResponse(rps, msgId);
//                                dialog.dismiss();
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "text/plain; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {

                        return msgRoot == null ? null : msgRoot.toString().getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        Log.e("VOLLEY", "Unsupported Encoding while trying to get the bytes of " + msgRoot.toString() + "utf-8");
                        return null;
                    }
                }


            };
            jor.setRetryPolicy(new DefaultRetryPolicy(10000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue revrq = Volley.newRequestQueue(context);
            revrq.add(jor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
