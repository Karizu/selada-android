package com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionTopupResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.biller.selada.purchase.PurchaseActivity;
import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

/**
 * Created by Randy on 02/14/19.
 */

public class TransactionReviewActivity extends BaseActivity implements CommonInterface, TransactionReviewView {
    public static final String TRANSACTION_INFO = "transactionInfo";
    public static final String TRANSACTION_INFO_MORE = "transactionInfoMore";
    public static final String NOP = "nop";
    public static final String TOTAL_TRANSACTION = "totalTransaction";
    public static final String MARGIN_TRANSACTION = "marginTransaction";
    public static final String AMOUNT_TRANSACTION = "amountTransaction";
    public static final String ORDER_ID = "orderId";
    public static final String SERVICE_ID = "serviceId";
    public static final String REQUEST_FROM = "requestFrom";
    public static final String IMAGE_URL = "imgUrl";
    public static final String IMAGE_DRAWABLE = "imgDrawable";
    public static final String IS_FROM_QR = "isFromQr";
    public static final String IS_FROM_PURCHASE = "isFromPurchase";
    public static final String SYSTEM_MARKUP = "isSystemMarkup";
    public static final String PURCHASE_BJB = "MB82510";
    public static final String PURCHASE_SELADA = "MB82560";
    private static final int CHECK_PASSCODE = 1;
    private static final int CHECK_PASSCODE_QR = 2;
    private static final String TAG = "TRANSACTION_REVIEW_ACT";

    private static String PURCHASE = PURCHASE_SELADA;

    private Members member;
    private TextView transactionInfo;
    private TextView transactionInfoMore;
    private ImageView iconTransaction;
    private TextView orderIdText;
    private TextView totalTransaction;
    private TextView balance;
    private TextView errorBalance;
    private TextView fee;
    private TextView transaction;
    private TextView transactionAmount;
    private Button nextBtn;
    private String total;
    private int mTotals;
    private String margin;
    private String amount;
    private String serviceId;
    private TransactionReviewPresenter mPresenter;
    private int positionRequest;
    private LinearLayout topupBtn, bjbBtn;
    private EditText refferal;
    private LinearLayout ccBtn;
    private ImageView ccImg;
    private int selectedPayment = 0;
    private TextView saldoTxt;
    private ImageView transactionIcon;
    private TextView CCtext;
    private boolean isFromPurchase;
    private TransactionTopupResponse topupResponse;
    private String transactionId;
    private String orderId;
    private ImageView iconCheck;
    private ImageView iconCheckCC;
    private TextView orderIdTitle;
    private String mobileNumber;
    private int mBalance;
    private AlertDialog alert;
    private Context context;
    private android.widget.TextView iccUIDisplay;
    private Messenger syncMessenger = null;
    private boolean pinDialogCanceled = false;
    private android.widget.TextView pinpadWarningText;
    private String mid;
    private String mobileNum = "";
    private String nominal = "";
    private String amounts = "";
    private String stan = "";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.transaction_review_activity;
    }

    @Override
    protected void setContentViewOnChild() {
        setSeladaToolbarTitle("Detail Pembayaran");
        context = this;
        transactionInfo = findViewById(R.id.transaction_info);
        transactionInfoMore = findViewById(R.id.transaction_info_more);
        iconTransaction =  findViewById(R.id.icon_transaction);
        orderIdTitle =  findViewById(R.id.order_id_title);
        orderIdText =  findViewById(R.id.order_id_text);
        totalTransaction =  findViewById(R.id.total_transaction);
        balance =  findViewById(R.id.balance);
        errorBalance =  findViewById(R.id.error_balance);
        nextBtn =  findViewById(R.id.next_btn);
        topupBtn =  findViewById(R.id.topup_btn);
        bjbBtn =  findViewById(R.id.bjb_btn);
        refferal =  findViewById(R.id.refferal_code);
        fee =  findViewById(R.id.fee);
        transaction =  findViewById(R.id.transaction);
        transactionAmount =  findViewById(R.id.transaction_amount);
        ccBtn =  findViewById(R.id.cc_btn);
        ccImg =  findViewById(R.id.cc_image);
        transactionIcon =  findViewById(R.id.transaction_icon);
        saldoTxt =  findViewById(R.id.saldo_text);
        CCtext =  findViewById(R.id.CC_text);
        topupResponse = new TransactionTopupResponse();
        iconCheck =  findViewById(R.id.iconCheck);
        iconCheckCC =  findViewById(R.id.iconCheckCC);
        iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_merchant));
        initEvent();
    }

    private void initEvent() {
        String info = getIntent().getStringExtra(TRANSACTION_INFO);
        String moreInfo = getIntent().getStringExtra(TRANSACTION_INFO_MORE);
        mobileNumber = getIntent().getStringExtra(NOP);
        amount = getIntent().getStringExtra(AMOUNT_TRANSACTION);
        margin = getIntent().getStringExtra(MARGIN_TRANSACTION);
        total = getIntent().getStringExtra(TOTAL_TRANSACTION);
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        String id = getIntent().getStringExtra(ORDER_ID);
        positionRequest = getIntent().getIntExtra(REQUEST_FROM, 0);

        long margins = Integer.parseInt(margin);
        long amounts = Integer.parseInt(amount);

        mTotals = (int) (amounts + margins);

        transactionInfo.setText(info);
        transactionInfoMore.setText(moreInfo);
        orderIdText.setVisibility(View.GONE);
        orderIdTitle.setVisibility(View.GONE);
//        orderIdText.setText(id);
        refferal.setText(PreferenceManager.getRefferalId());
        transaction.setText(info);
        transactionAmount.setText(MethodUtil.toCurrencyFormat(amount));
        fee.setText(MethodUtil.toCurrencyFormat(margin));
        totalTransaction.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(mTotals)));
        String imgUrl = getIntent().getStringExtra(IMAGE_URL);
        int imgDraw = getIntent().getIntExtra(IMAGE_DRAWABLE, 0);
        isFromPurchase = getIntent().getBooleanExtra(IS_FROM_PURCHASE, false);
        if (isFromPurchase) {
            ccBtn.setVisibility(View.GONE);
        } else {
        }

        RxView.clicks(nextBtn).subscribe(aVoid -> {

            switch (positionRequest) {
                case 100:
                    if (TextUtils.isEmpty(refferal.getText().toString())) {
                        mPresenter.onRegisterPremium("");
                    } else {
                        mPresenter.checkRefferal(refferal.getText().toString());
                    }

                    break;
                default:
                    switch (selectedPayment) {
                        case 1:
                            if (errorBalance.getVisibility() == View.VISIBLE) {
                                MethodUtil.showCustomToast(TransactionReviewActivity.this, "Saldo anda tidak mencukupi", R.drawable.ic_error_login);
                            } else {
//
                                try {
                                    long mTotal = Long.parseLong(total);
//                                        if (mBalance < mTotal) {
//                                            MethodUtil.showCustomToast(TransactionReviewActivity.this, "Saldo anda tidak mencukupi", R.drawable.ic_error_login);
//                                        } else {

                                    //CALL MODULE CHIP
//                                    JSONObject currentScreen = JsonCompHandler
//                                            .readJsonFromIntent(PURCHASE, this);
//                                    Log.d("JSON", currentScreen.toString());
//                                    Integer type = currentScreen.getInt("type");
//                                    String ids = currentScreen.get("id").toString();

//                                    Intent intent = new Intent(TransactionReviewActivity.this, ActivityList.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("comp_act", PURCHASE);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);

//                                    mPresenter.paySeladaTransaction(serviceId, merchant.getId().toString(), mobileNumber, total, amount, "");

                                    Log.d("serviceId", serviceId);
                                    Log.d("mid", merchant.getId().toString());
                                    Log.d("mobileNumber", mobileNumber);
                                    Log.d("nominal", String.valueOf(mTotals));
                                    Log.d("amount", amount);

//                                    Intent to Purchase
                                    HashMap<String, String> data = new HashMap<String, String>();
                                    data.put("menu", PURCHASE);
                                    data.put("serviceId", serviceId);
                                    data.put("mid", merchant.getId().toString());
                                    data.put("mobileNumber", mobileNumber);
                                    data.put("nominal", String.valueOf(mTotals));
                                    data.put("amount", amount);
                                    data.put("margin", margin);
                                    Utils.openApp(this, "id.co.tornado.billiton", data);
//                                        }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        default:
                            MethodUtil.showCustomToast(TransactionReviewActivity.this, "Harap pilih metode pembayaran", R.drawable.ic_error_login);
                            break;
                    }
                    break;
            }

        });

        if (positionRequest != 100) {
            refferal.setVisibility(View.GONE);
        }

        RxView.clicks(topupBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                ccBtn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.border_round_white));
                topupBtn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.border_round_blue_contact));
                selectedPayment = 1;

                iconCheck.setVisibility(View.VISIBLE);
                iconCheckCC.setVisibility(View.GONE);

//                float scale = getResources().getDisplayMetrics().density;
//                int verticalPad = (int) (5*scale + 0.5f);
////                topupBtn.setPadding(verticalPad*2,verticalPad,verticalPad*2,verticalPad);
            }
        });

        RxView.clicks(bjbBtn).subscribe(aVoid -> {
            ccBtn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.border_round_white));
            bjbBtn.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.border_round_blue_contact));
            selectedPayment = 1;

            iconCheck.setVisibility(View.VISIBLE);
            iconCheckCC.setVisibility(View.GONE);

//                float scale = getResources().getDisplayMetrics().density;
//                int verticalPad = (int) (5*scale + 0.5f);
////                topupBtn.setPadding(verticalPad*2,verticalPad,verticalPad*2,verticalPad);
        });

        switch (positionRequest) {
            case Constant.EWALLET:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pulsa));
                break;
            case Constant.VOUCHER_GAME:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pulsa_pascabayar));
                break;
            case Constant.TELKOM:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.speedy));
                break;
            case Constant.PDAM:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pdam));
                break;
            case Constant.MULTIFINANCE:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.multi_finance));
                break;
            case Constant.BPJS_KESEHATAN:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_bpjs_kesehatan));
                break;
            case Constant.LISTRIK:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_token));
                break;
            case Constant.LISTRIK_PASCABAYAR:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_token));
                break;
            case Constant.PULSA_HANDPHONE:
                if (imgDraw != 0) {
                    iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, imgDraw));
                }
                break;
            case Constant.INTERNET_DATA:
                if (imgDraw != 0) {
                    iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, imgDraw));
                }
                break;
            case Constant.INTERNET:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_internet));
                break;
            case Constant.TV_INTERNET:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_tv_kabel));
                break;
            case Constant.PULSA_PASCABAYAR:
                iconTransaction.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pulsa_pascabayar));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new TransactionReviewPresenterImpl(this, this);
//        mPresenter.getBalance();
//        getDetailMember();

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

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

    public void getDetailMember() {
        progressBar.show(this, "Memuat data", false, null);
        Api.apiInterface().cekSaldo(PreferenceManager.getMemberId(), "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
                hideProgresLoading();
                try {
                    member = response.body().getData();
                    PreferenceManager.setMerchantId(member.getMerchantId());
                    balance.setText("Rp " + MethodUtil.toCurrencyFormat(member.getBalance()));
                    mBalance = Integer.parseInt(member.getBalance());
                    Log.d("BALANCE", mBalance + "");
                    int curBalance = Integer.parseInt(member.getBalance());
                    int totalAmount = TextUtils.isEmpty(total) ? 0 : Integer.parseInt(total);
                    if (curBalance < totalAmount) {
                        errorBalance.setVisibility(View.VISIBLE);
                    } else {
                        errorBalance.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Members>> call, Throwable t) {
                hideProgresLoading();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);

        //Intent to Purchase
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("menu", "REVERSALEFROMSELADA");
        data.put("stan", stan);
        Utils.openApp(this, "id.co.tornado.billiton", data);
//        if (msg.equalsIgnoreCase(Constant.EXPIRED_SESSION) || msg.equalsIgnoreCase(Constant.EXPIRED_ACCESS_TOKEN)) {
//            goToLoginPage1(this);
//        }
//        else{
//            onBackPressed();
//        }
    }

    @Override
    public void onSuccessGetBalance(String balance) {
        this.balance.setText("Rp " + MethodUtil.toCurrencyFormat(balance));
        int curBalance = Integer.parseInt(balance);
        int totalAmount = TextUtils.isEmpty(total) ? 0 : Integer.parseInt(total);
        if (curBalance < totalAmount) {
            errorBalance.setVisibility(View.VISIBLE);
        } else {
            errorBalance.setVisibility(View.INVISIBLE);
        }
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
        resultIntent.putExtra(PurchaseActivity.SERVICE_PROVIDER, positionRequest);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSuccessSeladaPayTransaction(GSeladaTransaction transaction) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PurchaseActivity.ORDER_ID, String.valueOf(transaction.id));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSuccessCheckReferral(String refferalId) {
        mPresenter.onRegisterPremium(refferalId);
    }

    @Override
    public void chargeAmount(String totalAmount, String fee) {
        this.fee.setText(MethodUtil.toCurrencyFormat(fee));
        totalTransaction.setText(MethodUtil.toCurrencyFormat(totalAmount));
    }

    @Override
    public void charge(QRTransactionResponse response) {
        if (response.success) {
            topupResponse.setInfo(response.transaction.notes);
            String[] dateTime = MethodUtil.formatDateAndTime(response.transaction.created_at);
            topupResponse.setDate(dateTime[0]);
            topupResponse.setTime(dateTime[1]);
            topupResponse.setTopupSaldo(response.transaction.amount_charged);
            topupResponse.setBankName("Saldo");
            topupResponse.setSuccess(true);
            topupResponse.setJenisTransaksi(2);
            topupResponse.setOrderId(response.transaction.id);
            topupResponse.setMerchant_name(response.transaction.merchant_name);
            topupResponse.isFromHome = true;
        } else {
            onFailureRequest("Terjadi kesalahan koneksi, tunggu beberapa saat lagi");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHECK_PASSCODE:
                    mPresenter.payTransaction(orderIdText.getText().toString());
                    break;
                case CHECK_PASSCODE_QR:
                    mPresenter.chargeTransaction(transactionId);
                    break;
            }
        }
    }


//    public void sendReversalAdviceSale(String stan) {
//        dettachPrint();
//        dialog = ProgressDialog.show(context, "Reversal", "Sedang Mengirim Reversal", true);
//        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        SharedPreferences preferences = context.getSharedPreferences(CommonConfig.SETTINGS_FILE, Context.MODE_PRIVATE);
//        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        final JSONObject msg = new JSONObject();
//        try {
//            final String msgId = telephonyManager.getDeviceId() + sdf.format(new Date());
//            msg.put("msg_id", msgId);
//            msg.put("msg_ui", telephonyManager.getDeviceId());
//            // Reversal Sale
//            msg.put("msg_si", "R82561");
//            msg.put("msg_dt", stan);
//
//            final JSONObject msgRoot = new JSONObject();
//            msgRoot.put("msg", msg);
//            String hostname = preferences.getString("hostname", CommonConfig.HTTP_REST_URL);
//            String postpath = preferences.getString("postpath", CommonConfig.POST_PATH);
//            String httpPost = CommonConfig.HTTP_PROTOCOL + "://" + hostname + "/" + postpath;
//
//            StringRequest jor = new StringRequest(Request.Method.POST,
//                    httpPost,
//                    new com.android.volley.Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                Log.d("TERIMA", response);
//                                JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[{\"visible\":true,\"comp_values\":{\"comp_value\":[{\"print\":\"Reversal Berhasil Dikirim\",\n" +
//                                        "\"value\":\"Reversal Berhasil Dikirim\"}]},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
//                                        "\"type\":\"3\",\"title\":\"Reversal\"}}");
//
//                                processResponse(rps, msgId);
//                                dialog.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }, new com.android.volley.Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                        try {
//                            Toast.makeText(context, "Request Timeout",
//                                    Toast.LENGTH_LONG).show();
//                            JSONObject rps = new JSONObject("{\"screen\":{\"ver\":\"1\",\"comps\":{\"comp\":[" +
//                                    "{\"visible\":true,\"comp_values\":{\"comp_value\":[" +
//                                    "{\"print\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\",\n" +
//                                    "\"value\":\"Transaksi ditolak oleh kartu\nTidak dapat mengirim Reversal\"}]" +
//                                    "},\"comp_lbl\":\" \",\"comp_type\":\"1\",\"comp_id\":\"P00001\",\"seq\":0}]},\"id\":\"000000F\",\n" +
//                                    "\"type\":\"3\",\"title\":\"Gagal\"}}");
//
//                            processResponse(rps, msgId);
//                            dialog.dismiss();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "text/plain; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//
//                        return msgRoot == null ? null : msgRoot.toString().getBytes("utf-8");
//                    } catch (UnsupportedEncodingException uee) {
//                        Log.e("VOLLEY", "Unsupported Encoding while trying to get the bytes of " + msgRoot.toString() + "utf-8");
//                        return null;
//                    }
//                }
//
//
//            };
//            jor.setRetryPolicy(new DefaultRetryPolicy(10000,
//                    0,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            RequestQueue revrq = Volley.newRequestQueue(context);
//            revrq.add(jor);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//

}
