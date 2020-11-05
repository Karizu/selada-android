package com.boardinglabs.mireta.selada.modul.biller.selada.purchase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.component.adapter.RecyProductAdapter;
import com.boardinglabs.mireta.selada.component.network.ApiSelada;
import com.boardinglabs.mireta.selada.component.network.gson.GSPICmd;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.SeladaAmountHelper;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.DetailTransactionHistoryActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.MiretaPOSApplication;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.adapter.RecyPurchaseQuotaAdapter;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.gson.GServices;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.biller.selada.transactionreview.TransactionReviewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dhimas on 10/2/17.
 */

public class PurchaseActivity extends BaseActivity implements CommonInterface, PurchaseView, RecyPurchaseQuotaAdapter.Action {
    public static final String DATE_TRANSACTION = "dateTransaction";
    public static final String ORDER_ID = "orderId";
    public static final String NOTE = "note";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String MARGIN = "margin";
    public static final String TOTAL_TRANSACTION = "totalTransaction";
    public static final String DATA = "data";
    public static final String SERVICE_PROVIDER = "serviceProvider";
    public static final String INQPLNPRE = "INQPLNPRE";
    public static final String INQPLNPSC = "INQPLNPSC";
    public static final String INQBPJSKS = "INQBPJSKS";
    public static String INQPDAM;
    public static String INQTELKOM;
    public static String INQPLSPSC = "";
    public static String INQPLSPASCA;
    public static String INQFN;
    public static String INQTVKABEL;
    public static String INQDEFAULT;
    public static String VOC;


    private static String telkomselRegex = "^(0|62)8(1[123]|52|53|21|22|23)[0-9]{5,9}$";
    private static String simpatiRegex = "^(0|62)8(1[123]|2[12])[0-9]{5,9}$";
    private static String asRegex = "^(0|62)8(52|53|23)[0-9]{5,9}$";
    private static String triRegex = "^(0|62)8(96|97|98|99|95)[0-9]{5,9}$";
    private static String smartfrenRegex = "^(0|62)8(81|82|83|84|85|86|87|88|89)[0-9]{5,9}$";
    private static String axisRegex = "^(0|62)8(38|31|32|33)[0-9]{5,9}$";
    private static String indosatRegex = "^(0815|62815|0816|62816|\\+62858|0858|62858||0814|62814)[0-9]{5,9}$";
    private static String im3Regex = "^(0855|62855|0856|62856|0857|62857)[0-9]{5,9}$";
    private static String xlRegex = "^(0817|62817|0818|62818|0819|62819|0859|62859|0878|62878|0877|62877)[0-9]{5,9}$";

    private String providerId, productCode, inqCmd;
    private RecyclerView listPurchase;
    private RecyPurchaseQuotaAdapter quotaAdapter;
    private RecyProductAdapter productAdapter;
    private PurchasePresenter mPresenter;
    private int pos;
    private String posName;
    private LinearLayout containerList;
    private Button nextBtn;
    private TextView infoPurchase;
    private TextView pricePurchase;
    private EditText no;
    private RelativeLayout pickService, pickProduct, pickProvider;
    private RelativeLayout inputAmount;
    private EditText amountPurchase;
    private ImageView iconPurchase;
    private List<GSeladaService> listServices;
    private TextView jenisNomor;
    private boolean isTokenPln;
    private int positionService;
    private boolean isPicked;
    private boolean isUsingInquiry = true;
    private int drawableOperator = 0;
    private Spinner spinnerProduct, spinnerProvider;

    public static final String TYPE_PLN = "typePln";
    public static final int TOKEN_PLN = 0;
    public static final int BAYAR_PLN = 1;
    private List<String> listProductName, listProductCode, listProviderId, listInqCmd;
    private boolean isFromDefault = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.purchase_layout;
    }

    @Override
    protected void setContentViewOnChild() {
        no = (EditText) findViewById(R.id.no);
        pricePurchase = (TextView) findViewById(R.id.price_purchase);
        infoPurchase = (TextView) findViewById(R.id.info_purchase);
        spinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);
        spinnerProvider = (Spinner) findViewById(R.id.spinnerProvider);
        nextBtn = (Button) findViewById(R.id.next_btn);
        containerList = (LinearLayout) findViewById(R.id.container_list_service);
        pickService = (RelativeLayout) findViewById(R.id.pick_service);
        pickProduct = (RelativeLayout) findViewById(R.id.pick_product);
        pickProvider = (RelativeLayout) findViewById(R.id.pick_provider);
        iconPurchase = (ImageView) findViewById(R.id.icon_purchase);
        inputAmount = (RelativeLayout) findViewById(R.id.input_amount);
        amountPurchase = (EditText) findViewById(R.id.amount_purchase);
        listPurchase = (RecyclerView) findViewById(R.id.purchase_list);
        jenisNomor = (TextView) findViewById(R.id.jenis_nomor);
        listPurchase.setLayoutManager(new LinearLayoutManager(this));
        launchKey();
        initEvent();
        pos = getIntent().getIntExtra(Constant.POSITION, 0);
        posName = getIntent().getStringExtra(Constant.POSITION_NAME);
        setSeladaToolbarTitle(posName);
        setListProduct(pos);
    }

    private void initEvent() {
        RxView.clicks(pickService).subscribe(aVoid -> {
            if (pos == Constant.VOUCHER_GAME) {
                if (providerId != null) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan pilih game terlebih dahulu", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.EWALLET) {
                if (providerId != null) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan pilih penyedia jasa terlebih dahulu", R.drawable.ic_error_login);
                }
            } else {
                if (no.getText().toString().length() > 0) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            }

        });

        RxView.clicks(nextBtn).subscribe(aVoid -> {
            if (pos == Constant.LISTRIK) {
                if (no.getText().toString().length() > 1) {
                    if (listServices != null && listServices.size() > 0 && isPicked && positionService >= 0) {
                        mPresenter.inquiryCommand(INQPLNPRE, no.getText().toString(), "", Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
//                            mPresenter.setInquiry(listServices.get(positionService).id, no.getText().toString(), isUsingInquiry, amountPurchase.getText().toString());
                    } else {
                        MethodUtil.showCustomToast(PurchaseActivity.this, "Mohon lengkapi seluruh data", R.drawable.ic_error_login);
                    }
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.LISTRIK_PASCABAYAR) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.PDAM) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.BPJS_KESEHATAN) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.TELKOM) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.PULSA_PASCABAYAR) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.MULTIFINANCE) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.TV_KABEL) {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else if (pos == Constant.VOUCHER_GAME || pos == Constant.EWALLET || pos == Constant.PULSA_HANDPHONE || pos == Constant.INTERNET_DATA){
                if (no.getText().toString().length() > 1) {
                    if (listServices != null && listServices.size() > 0 && isPicked && positionService >= 0) {
//                                mPresenter.setInquiry(listServices.get(positionService).id, no.getText().toString(), isUsingInquiry, amountPurchase.getText().toString());

                        GSeladaService pickedService = listServices.get(positionService);
                        String info = pickedService.product.provider.category.name + " " + pickedService.product.name;

                        long nominal = Integer.parseInt(pickedService.biller_price);
                        long system_markup = Integer.parseInt(pickedService.system_markup);
                        int amount = (int) (nominal + system_markup);

                        gotoTransactionReview(no.getText().toString(), no.getText().toString(), info, pickedService.id, pos, SeladaAmountHelper.convertServiceAmount(pickedService), String.valueOf(amount), pickedService.markup, "", drawableOperator);

                    } else {
                        MethodUtil.showCustomToast(PurchaseActivity.this, "Mohon lengkapi seluruh data", R.drawable.ic_error_login);
                    }
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            } else {
                if (no.getText().toString().length() > 1) {
                    processRequest();
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Input tidak boleh kosong", R.drawable.ic_error_login);
                }
            }
        });

        no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    if (pos == Constant.PULSA_HANDPHONE || pos == Constant.PULSA_PASCABAYAR) {
                        String nomor = charSequence.toString();
                        nomor = charSequence.charAt(0) != '0' ? "0" + nomor : nomor;
                        Pattern p = Pattern.compile(telkomselRegex);
                        Matcher m = p.matcher(nomor);
                        if (nomor.matches(xlRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_xl));
                            drawableOperator = R.drawable.icon_xl;
                            providerId = Constant.xlProviderId;
                            INQPLSPSC = "";
                        } else if (nomor.matches(indosatRegex) || nomor.matches(im3Regex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_indosat));
                            drawableOperator = R.drawable.icon_indosat;
                            providerId = Constant.indosatProviderId;
                            INQPLSPSC = "";
                        } else if (nomor.matches(telkomselRegex) || nomor.matches(simpatiRegex) || nomor.matches(asRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_telkomsel));
                            drawableOperator = R.drawable.icon_telkomsel;
                            providerId = Constant.telkomselProviderId;
                            INQPLSPSC = INQPLSPASCA;
                        } else if (nomor.matches(triRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_tri));
                            drawableOperator = R.drawable.icon_tri;
                            providerId = Constant.threeProviderId;
                            INQPLSPSC = "";
                        } else if (nomor.matches(smartfrenRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.smartfren));
                            drawableOperator = R.drawable.smartfren;
                            providerId = Constant.smartfrenProviderId;
                            INQPLSPSC = "";
                        } else if (nomor.matches(axisRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.axis));
                            drawableOperator = R.drawable.axis;
                            providerId = Constant.axisProviderId;
                            INQPLSPSC = "";
                        } else {
                            try {
                                iconPurchase.setImageDrawable(null);
                                providerId = "0";
                                drawableOperator = 0;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (pos == Constant.INTERNET_DATA) {
                        String nomor = charSequence.toString();
                        nomor = charSequence.charAt(0) != '0' ? "0" + nomor : nomor;
                        Pattern p = Pattern.compile(telkomselRegex);
                        Matcher m = p.matcher(nomor);
                        if (nomor.matches(xlRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_xl));
                            drawableOperator = R.drawable.icon_xl;
                            providerId = Constant.xlDataProviderId;
                        } else if (nomor.matches(indosatRegex) || nomor.matches(im3Regex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_indosat));
                            drawableOperator = R.drawable.icon_indosat;
                            providerId = Constant.indosatDataProviderId;
                        } else if (nomor.matches(telkomselRegex) || nomor.matches(simpatiRegex) || nomor.matches(asRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_telkomsel));
                            drawableOperator = R.drawable.icon_telkomsel;
                            providerId = Constant.telkomselDataProviderId;
                        } else if (nomor.matches(triRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.icon_tri));
                            drawableOperator = R.drawable.icon_tri;
                            providerId = Constant.threeDataProviderId;
                        } else if (nomor.matches(smartfrenRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.smartfren));
                            drawableOperator = R.drawable.smartfren;
                            providerId = Constant.smartfrenDataProviderId;
                        } else if (nomor.matches(axisRegex)) {
                            iconPurchase.setImageDrawable(ContextCompat.getDrawable(PurchaseActivity.this, R.drawable.axis));
                            drawableOperator = R.drawable.axis;
                            providerId = Constant.axisDataProviderId;
                        } else {
                            iconPurchase.setImageDrawable(null);
                            providerId = "0";
                        }
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 16) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Maximal input 16 karakter", R.drawable.ic_error_login);
                    editable.delete(editable.length() - 1, editable.length());
                }
            }
        });
    }

    private void gotoTransactionReview(String nop, String transactionInfoMore, String transactionInfo, String transactionId, int service, String totalTransaction, String amountTransaction, String marginTransaction, String urlImage, int drawableLogo) {
        Intent intent = new Intent(this, TransactionReviewActivity.class);
        intent.putExtra(TransactionReviewActivity.NOP, nop);
        intent.putExtra(TransactionReviewActivity.SERVICE_ID, transactionId);
        intent.putExtra(TransactionReviewActivity.TOTAL_TRANSACTION, totalTransaction);
        intent.putExtra(TransactionReviewActivity.MARGIN_TRANSACTION, marginTransaction);
        intent.putExtra(TransactionReviewActivity.AMOUNT_TRANSACTION, amountTransaction);
        intent.putExtra(TransactionReviewActivity.ORDER_ID, transactionId);
        intent.putExtra(TransactionReviewActivity.TRANSACTION_INFO, transactionInfo);
        intent.putExtra(TransactionReviewActivity.TRANSACTION_INFO_MORE, transactionInfoMore);
        intent.putExtra(TransactionReviewActivity.TRANSACTION_INFO_MORE, transactionInfoMore);
        intent.putExtra(TransactionReviewActivity.REQUEST_FROM, service);
        intent.putExtra(TransactionReviewActivity.IMAGE_URL, urlImage);
        intent.putExtra(TransactionReviewActivity.IMAGE_DRAWABLE, drawableLogo);
        intent.putExtra(TransactionReviewActivity.IS_FROM_PURCHASE, true);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onSuccessSPICmdInquiry(GSPICmd spiCMD) {
        if (spiCMD.msg != null) {
            if (spiCMD.msg.rc != null) {
                if (spiCMD.sts == 500 && spiCMD.msg.rc.equals("0000")) {
//            0014
                    if (spiCMD.cmd.equals(INQPLNPRE)) {
                        GSeladaService pickedService = listServices.get(positionService);
                        String info = pickedService.product.provider.category.name + " " + pickedService.product.name;

                        String custInfo = no.getText().toString() + "\n" + spiCMD.msg.idpel + "\n" + spiCMD.msg.nama;

                        long nominal = Integer.parseInt(pickedService.biller_price);
                        long system_markup = Integer.parseInt(pickedService.system_markup);

                        int amount = (int) (nominal + system_markup);

                        gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, SeladaAmountHelper.convertServiceAmount(pickedService), String.valueOf(amount), pickedService.markup, "", R.drawable.pln);
                    } else if (spiCMD.cmd.equals(INQPLNPSC)) {
                        GSeladaService pickedService = listServices.get(positionService);
                        String info = pickedService.product.provider.category.name;

                        String custInfo = spiCMD.msg.idpel
                                + "\n" + spiCMD.msg.nama + "\n" + "Jml Bulan Byr: " + spiCMD.msg.jml_bln_byr + "\n"
                                + "Jml Tunggakan Bln: " + spiCMD.msg.jml_tunggakan_bln + "\n"
                                + "Bulan-Tahun: " + spiCMD.msg.bl_thn + "\n"
                                + "Stand-Meter: " + spiCMD.msg.stand_meter + "\n"
                                + "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.rp_tag)) + "\n"
                                + "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n"
                                + "Denda: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.denda)) + "\n"
                                + "Total Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total_bayar));

                        String markup = spiCMD.msg.admin;
                        int totalTagihan = spiCMD.msg.total_bayar;
                        int totalBayar = Integer.valueOf(markup) + totalTagihan;

                        long nominal = totalBayar;
                        long system_markup = Integer.parseInt(pickedService.system_markup);

                        int amount = (int) (nominal + system_markup);

                        gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(totalBayar), String.valueOf(amount), pickedService.markup, "", R.drawable.pln);
                    }

                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQPDAM)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;
                    String note = new Gson().toJson(spiCMD.msg);
                    StringBuilder custInfo = new StringBuilder();
                    JSONObject jsonNote;
                    try {
                        jsonNote = new JSONObject(note);
                        JSONObject data;
                        if (jsonNote.has("msg")) {
                            data = jsonNote.getJSONObject("msg");
                        } else {
                            data = jsonNote;
                        }

                        try {
                            String objName = "";
                            String objValue = "";
                            String dataInfo = "";
                            for (int i = data.names().length() - 1; i >= 0; i--) {
                                try {
                                    objName = data.names().getString(i);
                                    objValue = data.getString(data.names().getString(i));

                                    if (objName.contains("tagihan") || objName.contains("admin") || objName.contains("total")) {
                                        objValue = "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(objValue));
                                    }

                                    dataInfo = objName + ": " + objValue + "\n";
                                    custInfo.append(dataInfo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d("custInfo", String.valueOf(custInfo));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);
                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), String.valueOf(custInfo), info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.pdam);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQFN)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;
                    String denda = "-";
                    try {
                        denda = "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.denda));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String custInfo = "";
                    try {
                        custInfo += spiCMD.msg.nopel + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += spiCMD.msg.nama + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Periode: " + MethodUtil.strToDateFormat(spiCMD.msg.periode) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.tagihan)) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Denda: " + denda + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Total Bayar: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total));
                    } catch (Exception e) {
                    }

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);

                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.multi_finance);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQTVKABEL)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;
                    String denda = "-";
                    try {
                        denda = "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.denda));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String custInfo = "";
                    try {
                        custInfo += spiCMD.msg.nopel + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += spiCMD.msg.nama + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Periode: " + MethodUtil.strToDateFormat(spiCMD.msg.periode) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.tagihan)) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Denda: " + denda + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n";
                    } catch (Exception e) {
                    }
                    try {
                        custInfo += "Total Bayar: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total));
                    } catch (Exception e) {
                    }

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);

                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.multi_finance);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQBPJSKS)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;

                    String custInfo = ""
                            + "Nomor VA: " + spiCMD.msg.nova + "\n"
                            + "Periode: " + "Hingga Bulan ke " + spiCMD.msg.jbln + "\n"
                            + "Nama Peserta: " + spiCMD.msg.nama + "\n"
                            + "Jumlah Peserta: " + spiCMD.msg.jpst + "\n"
                            + "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.premi)) + "\n"
                            + "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n"
                            + "Total Bayar: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total));

                    String markup = spiCMD.msg.admin;
                    int totalTagihan = Integer.parseInt(spiCMD.msg.premi);
                    int totalBayar = Integer.valueOf(markup) + totalTagihan;

                    long nominal = totalBayar;
                    long system_markup = Integer.parseInt(pickedService.system_markup);

                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(totalBayar), String.valueOf(amount), pickedService.markup, "", R.drawable.icon_bpjs_kesehatan);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQTELKOM)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;
                    String[] periodes;
                    String periode = "-";
                    if (spiCMD.msg.periode != null) {
                        if (spiCMD.msg.periode.contains(",")) {
                            periodes = spiCMD.msg.periode.split(",");
                            periode = MethodUtil.strToDateFormat(periodes[0]) + ", " + MethodUtil.strToDateFormat(periodes[1]);
                        } else {
                            periode = MethodUtil.strToDateFormat(spiCMD.msg.periode);
                        }
                    }


                    String custInfo = ""
                            + "Nomor Telepon: " + spiCMD.msg.nopel + "\n"
                            + "Nama: " + spiCMD.msg.nama + "\n"
                            + "Periode: " + periode + "\n"
                            + "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.tagihan)) + "\n"
                            + "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n"
                            + "Total Bayar: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total));

//                    String markup = pickedService.markup;
//                    int totalTagihan = Integer.parseInt(spiCMD.msg.total);
//                    int totalBayar = Integer.valueOf(markup) + totalTagihan;

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);
                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.speedy);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(INQPLSPASCA)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;

                    String custInfo = ""
                            + "Nomor Telepon: " + spiCMD.msg.nopel + "\n"
                            + "Nama: " + spiCMD.msg.nama + "\n"
                            + "Tagihan: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.tagihan)) + "\n"
                            + "Biaya Admin: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.admin)) + "\n"
                            + "Total Bayar: " + "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(spiCMD.msg.total));

//                    String markup = pickedService.markup;
//                    int totalTagihan = Integer.parseInt(spiCMD.msg.total);
//                    int totalBayar = Integer.valueOf(markup) + totalTagihan;

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);
                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), custInfo, info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.pulsa_pascabayar);
                } else if (spiCMD.msg.ket != null) {
                    MethodUtil.showCustomToast(PurchaseActivity.this, spiCMD.msg.ket, R.drawable.ic_error_login);
                } else {
                    MethodUtil.showCustomToast(PurchaseActivity.this, "Silahkan coba lagi nanti", R.drawable.ic_error_login);
                }
            } else if (spiCMD.cmd.equals(VOC)) {
                if (spiCMD.sts == 500) {
                    GSeladaService pickedService = listServices.get(positionService);
                    String info = pickedService.product.provider.category.name;
                    String note = new Gson().toJson(spiCMD.msg);
                    StringBuilder custInfo = new StringBuilder();
                    JSONObject jsonNote;
                    try {
                        jsonNote = new JSONObject(note);
                        JSONObject data;
                        if (jsonNote.has("msg")) {
                            data = jsonNote.getJSONObject("msg");
                        } else {
                            data = jsonNote;
                        }

                        try {
                            String objName = "";
                            String objValue = "";
                            String dataInfo = "";
                            for (int i = data.names().length() - 1; i >= 0; i--) {
                                try {
                                    objName = data.names().getString(i);
                                    objValue = data.getString(data.names().getString(i));

                                    if (objName.contains("tagihan") || objName.contains("admin") || objName.contains("total")) {
                                        objValue = "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(objValue));
                                    }

                                    dataInfo = Utils.formatString(objName) + ": " + objValue + "\n";
                                    custInfo.append(dataInfo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d("custInfo", String.valueOf(custInfo));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long nominal = Integer.parseInt(pickedService.biller_price);
                    long system_markup = Integer.parseInt(pickedService.system_markup);
                    int amount = (int) (nominal + system_markup);

                    gotoTransactionReview(no.getText().toString(), String.valueOf(custInfo), info, pickedService.id, pos, String.valueOf(amount), spiCMD.msg.total, pickedService.markup, "", R.drawable.pdam);
                }
            }
        }
    }

    @Override
    public void onSuccessSPICmdInquiryResponse(ResponseBody spiCMD) {
        JSONObject jsonNote;
        JSONObject jsonData = null, data = null;
        String totalSpi = null;
        int status = 0;
        try {
            jsonNote = new JSONObject(spiCMD.string());
            if (jsonNote.has("data")) {
                jsonData = jsonNote.getJSONObject("data");
                status = Integer.parseInt(jsonData.getString("sts"));
            } else {
                jsonData = jsonNote;
            }

            if (jsonData.has("msg")) {
                data = jsonData.getJSONObject("msg");
            } else {
                data = jsonData;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (status == 500) {
            GSeladaService pickedService = listServices.get(positionService);
            String info = pickedService.product.provider.category.name;
            StringBuilder custInfo = new StringBuilder();
            try {
                if (jsonData.has("msg")) {
                    data = jsonData.getJSONObject("msg");
                } else {
                    data = jsonData;
                }
                try {
                    String objName;
                    String objValue;
                    String dataInfo;
                    for (int i = data.names().length() - 1; i >= 0; i--) {
                        try {
                            objName = data.names().getString(i);
                            objValue = data.getString(data.names().getString(i));

                            if (objName.contains("tagihan") || objName.contains("admin") || objName.contains("total")) {
                                if (objName.contains("total")) {
                                    totalSpi = objValue;
                                }
                                objValue = "Rp " + MethodUtil.toCurrencyFormat(String.valueOf(objValue));
                            }

                            dataInfo = Utils.formatString(objName) + ": " + objValue + "\n";
                            custInfo.append(dataInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("custInfo", String.valueOf(custInfo));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            long nominal = Integer.parseInt(pickedService.biller_price);
            long system_markup = Integer.parseInt(pickedService.system_markup);
            int amount = (int) (nominal + system_markup);

            gotoTransactionReview(no.getText().toString(), String.valueOf(custInfo), info, pickedService.id, pos, String.valueOf(amount), totalSpi, pickedService.markup, "", Utils.icons().get(posName) != null ? Utils.icons().get(posName) : Utils.icons().get("Default"));
        } else {
            String ket = "Silahkan coba lagi nanti";
            try {
                ket = data.getString("ket");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MethodUtil.showCustomToast(PurchaseActivity.this, ket, R.drawable.ic_error_login);
        }
    }

    @Override
    public void onSuccessSPICmdInquiryBPJS(GSPICmd spiCMD) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String orderId = data.getExtras().getString(ORDER_ID);
            Intent intent = new Intent(this, DetailTransactionHistoryActivity.class);
            intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_ID, orderId);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new PurchasePresenterImpl(this, this);
    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (containerList.isShown()) {
            containerList.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private void processRequest() {
        switch (pos) {
            case Constant.PULSA_HANDPHONE:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.INTERNET_DATA:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.LISTRIK:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.LISTRIK_PASCABAYAR:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.PASCABAYAR:
                mPresenter.getServices(Constant.SERVICE_PAYMENT, "", no.getText().toString(), Constant.SERVICE_PULSA, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.PDAM:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_PDAM, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.BPJS_KESEHATAN:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_BPJS_KESEHATAN, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.TELKOM:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_TELKOM, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.VOUCHER_GAME:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.EWALLET:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.PULSA_PASCABAYAR:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_PASCABAYAR, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.MULTIFINANCE:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_MULTIFINANCE, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            case Constant.TV_KABEL:
                mPresenter.getSeladaServices(String.valueOf(pos), Constant.PROVIDER_TV_KABEL, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
            default:
                mPresenter.getSeladaServices(String.valueOf(pos), providerId, Utils.encodeToString(rToken), Utils.encodeToString(keyGen), merchant.getTerminal().getSerialNumber());
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setListProduct(int position) {
        MiretaPOSApplication app = (MiretaPOSApplication) getApplication();
        FirebaseAnalytics mFirebaseAnalytics = app.getFirebaseAnalytics();
        Bundle params = new Bundle();
        pos = position;
        switch (position) {
            case Constant.INTERNET_DATA:
                setSeladaToolbarTitle("Paket Data");
                jenisNomor.setText("Nomor");
                pricePurchase.setText("Pilih Paket");
                infoPurchase.setText("Pilih Paket");
                no.setHint("08XXXXXXXXXX");
                isUsingInquiry = false;
                quotaAdapter = new RecyPurchaseQuotaAdapter(false);
                quotaAdapter.setListener(this);
                listPurchase.setAdapter(quotaAdapter);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Data");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Data");
                nextBtn.setText("BELI");
                break;
            case Constant.LISTRIK:
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pln));
                setSeladaToolbarTitle("TOKEN LISTRIK");
                nextBtn.setText("ISI TOKEN");
                quotaAdapter = new RecyPurchaseQuotaAdapter(false);
                quotaAdapter.setListener(this);
                listPurchase.setAdapter(quotaAdapter);
                isTokenPln = true;
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Listrik");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Listrik");
                break;
            case Constant.LISTRIK_PASCABAYAR:
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pln));
                setSeladaToolbarTitle("BAYAR LISTRIK");
                pickService.setVisibility(View.GONE);
                nextBtn.setText("CEK TAGIHAN");
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Listrik");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Listrik");
                break;
            case Constant.PULSA_HANDPHONE:
                setSeladaToolbarTitle("PULSA Reguler");
                jenisNomor.setText("Nomor");
                no.setHint("08XXXXXXXXXX");
                nextBtn.setText("BELI");
                isUsingInquiry = false;
                quotaAdapter = new RecyPurchaseQuotaAdapter(false);
                quotaAdapter.setListener(this);
                listPurchase.setAdapter(quotaAdapter);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase pulsa");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase pulsa");
                break;
            case Constant.BPJS_KESEHATAN:
                setSeladaToolbarTitle("BAYAR BPJS");
                no.setHint("1234567890");
                jenisNomor.setText("Nomor VA");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_bpjs_kesehatan));
                nextBtn.setText("CEK TAGIHAN");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.BPJS_KESEHATAN);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase bpjsks");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase bpjsks");
                break;
            case Constant.TELKOM:
                setSeladaToolbarTitle("BAYAR TELKOM");
                no.setHint("1234567890");
                jenisNomor.setText("Nomor Telepon");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.speedy));
                nextBtn.setText("CEK TAGIHAN");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.TELKOM);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase telkom");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase telkom");
                break;
            case Constant.VOUCHER_GAME:
                setSeladaToolbarTitle("Voucher Game");
                pickProvider.setVisibility(View.VISIBLE);
                jenisNomor.setText("User ID");
                no.setHint("88XXXXXXX");
                pricePurchase.setText("Pilih Nominal Topup");
                infoPurchase.setText("Pilih Nominal Topup");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pulsa_pascabayar));
                isUsingInquiry = false;
                quotaAdapter = new RecyPurchaseQuotaAdapter(false);
                quotaAdapter.setListener(this);
                listPurchase.setAdapter(quotaAdapter);
                proccessSpinnerData(Constant.VOUCHER_GAME);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase voucher game");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase voucher game");
                nextBtn.setText("BELI");
                break;
            case Constant.EWALLET:
                setSeladaToolbarTitle("Multipayment");
                pickProvider.setVisibility(View.VISIBLE);
                jenisNomor.setText("Masukkan Nomor HP");
                pricePurchase.setText("Pilih Nominal Topup");
                infoPurchase.setText("Pilih Nominal Topup");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pulsa));
                no.setHint("08XXXXXXXX");
                isUsingInquiry = false;
                quotaAdapter = new RecyPurchaseQuotaAdapter(false);
                quotaAdapter.setListener(this);
                listPurchase.setAdapter(quotaAdapter);
                proccessSpinnerData(Constant.EWALLET);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase ewallet");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase ewallet");
                nextBtn.setText("BELI");
                break;
            case Constant.PULSA_PASCABAYAR:
                setSeladaToolbarTitle("Pulsa Pascabayar");
                jenisNomor.setText("Masukkan Nomor HP");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_pulsa));
                no.setHint("08XXXXXXXX");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.PULSA_PASCABAYAR);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Pascabayar");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Pascabayar");
                nextBtn.setText("BELI");
                break;
            case Constant.MULTIFINANCE:
                setSeladaToolbarTitle("BAYAR MULTIFINANCE");
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.multi_finance));
                nextBtn.setText("CEK TAGIHAN");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.MULTIFINANCE);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Multifinance");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Multifinance");
                break;
            case Constant.TV_KABEL:
                setSeladaToolbarTitle("BAYAR TV KABEL");
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tv_berlangganan));
                nextBtn.setText("CEK TAGIHAN");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.TV_KABEL);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Tv Kabel");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Tv Kabel");
                break;
            case Constant.PDAM:
                setSeladaToolbarTitle("BAYAR PDAM");
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pdam));
                nextBtn.setText("CEK TAGIHAN");
                pickProduct.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(Constant.PDAM);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase Pdam");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase Pdam");
                break;
            default:
                setSeladaToolbarTitle(posName);
                no.setHint("1234567890");
                iconPurchase.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_transaksi_baru));
                nextBtn.setText("CEK TAGIHAN");
                pickProvider.setVisibility(View.VISIBLE);
                pickService.setVisibility(View.GONE);
                proccessSpinnerData(pos);
                params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "purchase " + posName);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                mFirebaseAnalytics.setUserProperty("Page", "purchase " + posName);
                break;
        }


    }


    @Override
    public void showProgressLoading() {
        String textLoading = "Mohon tunggu beberapa saat, no tagihan anda sedang dalam proses pengecekan";
        progressBar.show(this, textLoading, false, null);
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
        if (pos == Constant.INTERNET_DATA || pos == Constant.PULSA_HANDPHONE
                || pos == Constant.LISTRIK) {
            isPicked = false;
            infoPurchase.setText("Pilih Nominal");
            pricePurchase.setVisibility(View.GONE);
            listServices = null;
        }
    }

    @Override
    public void onSuccessGetSeladaService(List<GSeladaService> listServices) {
        this.listServices = listServices;
        isPicked = false;
        switch (pos) {
            case Constant.PULSA_HANDPHONE:
                quotaAdapter.setData(listServices);
                pricePurchase.setVisibility(View.GONE);
                break;
            case Constant.INTERNET_DATA:
                quotaAdapter.setData(listServices);
                pricePurchase.setVisibility(View.GONE);
                break;
            case Constant.LISTRIK:
                quotaAdapter.setData(listServices);
                pricePurchase.setVisibility(View.GONE);
                break;
            case Constant.LISTRIK_PASCABAYAR:
                isPicked = true;
                positionService = 0;
                mPresenter.inquiryCommand(INQPLNPSC, no.getText().toString(), "", Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                return;
            case Constant.PASCABAYAR:
                isPicked = true;
                positionService = 0;
                break;
            case Constant.PDAM:
                isPicked = true;
                positionService = getPositionService();
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            case Constant.BPJS_KESEHATAN:
                isPicked = true;
                positionService = 0;
                mPresenter.inquiryCommandBPJS(INQBPJSKS, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            case Constant.TELKOM:
                isPicked = true;
                positionService = 0;
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            case Constant.VOUCHER_GAME:
                quotaAdapter.setData(listServices);
                pricePurchase.setVisibility(View.GONE);
                break;
            case Constant.EWALLET:
                quotaAdapter.setData(listServices);
                pricePurchase.setVisibility(View.GONE);
                break;
            case Constant.PULSA_PASCABAYAR:
                isPicked = true;
                positionService = getPositionService();
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), "", Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            case Constant.MULTIFINANCE:
                isPicked = true;
                positionService = getPositionService();
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            case Constant.TV_KABEL:
                isPicked = true;
                positionService = getPositionService();
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
            default:
                isPicked = true;
                positionService = getPositionService();
                mPresenter.inquiryCommandResponse(VOC, no.getText().toString(), productCode, Utils.encodeToString(rToken), Utils.encodeToString(keyGen));
                break;
        }

        if (listServices != null && listServices.size() > 0) {
            if (containerList.isShown()) {
                containerList.setVisibility(View.GONE);
                //nextBtn.setVisibility(View.VISIBLE);
            } else {
                containerList.setVisibility(View.VISIBLE);
                //nextBtn.setVisibility(View.GONE);
            }
            if (pos == Constant.LISTRIK) {
                if (!isTokenPln) {
                    containerList.setVisibility(View.GONE);
                }
            }
        } else {
            MethodUtil.showCustomToast(PurchaseActivity.this, "Gagal mendapatkan data service", R.drawable.ic_error_login);
        }
    }

    private int getPositionService() {
        if (listServices.size() > 0) {
            for (int i = 0; i < listServices.size(); i++) {
                GSeladaService pickedService = listServices.get(i);
                if (pickedService.product.code.equals(productCode)) {
                    Log.d("positionService", String.valueOf(i));
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public void onSuccessGetService(List<GServices> listServices) {

    }

    @Override
    public void onSuccessInquiry(GTransaction transaction) {

    }

    @Override
    public void onSuccessGetListProductSelada(List<GSeladaProduct> seladaProducts) {
        if (seladaProducts.size() > 0) {
            for (int i = 0; i < seladaProducts.size(); i++) {
                GSeladaProduct seladaProduct = seladaProducts.get(i);
                listProductName.add(seladaProduct.getName());
                listProductCode.add(seladaProduct.getCode());
                listInqCmd.add(seladaProduct.getBiller_inq_cmd() != null ? seladaProduct.getBiller_inq_cmd() : "");
            }

            if (isFromDefault) {
                GSeladaProduct seladaProduct = seladaProducts.get(positionService);
                productCode = seladaProduct.getCode();
                VOC = seladaProduct.getBiller_inq_cmd() != null ? seladaProduct.getBiller_inq_cmd() : "";
            } else {
                setSpinnerProductData();
            }
        }
    }

    @Override
    public void onSuccessGetListProviderSelada(List<GSeladaProvider> seladaProviders) {
        for (int i = 0; i < seladaProviders.size(); i++) {
            GSeladaProvider seladaProvider = seladaProviders.get(i);
            listProductName.add(seladaProvider.name);
            listProviderId.add(seladaProvider.id);
        }
        setSpinnerProviderData();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onListClick(int position) {
        containerList.setVisibility(View.GONE);
        //nextBtn.setVisibility(View.VISIBLE);
        GSeladaService services = listServices.get(position);
        positionService = position;
        isPicked = true;
        int price = Integer.parseInt(services.biller_price);
        int markup = Integer.parseInt(services.system_markup);
        String grand_price = String.valueOf(price + markup);
        String amount = SeladaAmountHelper.convertServiceAmount(services);
        switch (pos) {
            case Constant.PULSA_HANDPHONE:
                pricePurchase.setText("Rp " + MethodUtil.toCurrencyFormat(grand_price));
                pricePurchase.setVisibility(View.VISIBLE);
                infoPurchase.setText(services.product.name);
                break;
            case Constant.INTERNET_DATA:
                pricePurchase.setText("Rp " + MethodUtil.toCurrencyFormat(grand_price));
                infoPurchase.setText(services.product.name);
                pricePurchase.setVisibility(View.VISIBLE);
                break;
            case Constant.LISTRIK:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.LISTRIK_PASCABAYAR:
                if (isTokenPln) {
                    infoPurchase.setText(services.product.name);
                } else {

                }
                break;
            case Constant.PASCABAYAR:
                break;
            case Constant.PDAM:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.MULTIFINANCE:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.TV_KABEL:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.BPJS_KESEHATAN:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.TELKOM:
                infoPurchase.setText(services.product.name);
                break;
            case Constant.VOUCHER_GAME:
                pricePurchase.setVisibility(View.VISIBLE);
                pricePurchase.setText("Rp " + MethodUtil.toCurrencyFormat(grand_price));
                infoPurchase.setText(services.product.name);
                break;
            case Constant.EWALLET:
                infoPurchase.setText(services.product.name);
                pricePurchase.setText("Rp " + MethodUtil.toCurrencyFormat(grand_price));
                pricePurchase.setVisibility(View.VISIBLE);
                break;
            default:
                infoPurchase.setText(services.product.name);
                break;
        }
    }

    private void proccessSpinnerData(int services) {
        listProductName = new ArrayList<>();
        listProductCode = new ArrayList<>();
        listProviderId = new ArrayList<>();
        listInqCmd = new ArrayList<>();

        switch (services) {
            case Constant.PDAM:
                listProductName.add("Pilih PDAM");
                listProductCode.add("proCode");
                listInqCmd.add("inqCmd");
                mPresenter.getListProductSelada(Constant.PROVIDER_PDAM);
                break;
            case Constant.MULTIFINANCE:
                listProductName.add("Pilih Pembayaran");
                listProductCode.add("proCode");
                listInqCmd.add("inqCmd");
                mPresenter.getListProductSelada(Constant.PROVIDER_MULTIFINANCE);
                break;
            case Constant.TV_KABEL:
                listProductName.add("Pilih Provider TV");
                listProductCode.add("proCode");
                listInqCmd.add("inqCmd");
                mPresenter.getListProductSelada(Constant.PROVIDER_TV_KABEL);
                break;
            case Constant.BPJS_KESEHATAN:
                listProductName.add("Bayar Untuk");
                listProductCode.add("0 Bulan");
                try {
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    String[] months = new DateFormatSymbols().getMonths();
                    for (int i = 0; i < months.length; i++) {
                        String month = months[i];
                        listProductName.add(i + 1 + " Bulan");
                        listProductCode.add(i + 1 + "");
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseActivity.this, R.layout.layout_spinner_text, listProductName) {
                        @Override
                        public boolean isEnabled(int position) {
                            return position != 0;
                        }
                    };
                    dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
                    spinnerProduct.setAdapter(dataAdapter);
                    spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if (position != 0) {
                                productCode = listProductCode.get(position);
                            } else {
                                productCode = null;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Constant.VOUCHER_GAME:
                listProductName.add("Pilih Game");
                listProviderId.add("provId");
                mPresenter.getListProviderSelada(String.valueOf(pos));
                break;
            case Constant.EWALLET:
                listProductName.add("Pilih Penyedia Jasa");
                listProviderId.add("provId");
                mPresenter.getListProviderSelada(String.valueOf(pos));
                break;
            case Constant.PULSA_PASCABAYAR:
                listProductName.add("Pilih Provider");
                listProductCode.add("proCode");
                listInqCmd.add("inqCmd");
                listProviderId.add("provId");
                mPresenter.getListProductSelada(Constant.PROVIDER_PASCABAYAR);
                break;
            case Constant.TELKOM:
                listProductName.add("Pilih Provider");
                listProductCode.add("proCode");
                listInqCmd.add("inqCmd");
                mPresenter.getListProductSelada(Constant.PROVIDER_TELKOM);
                break;
            default:
                listProductName.add("Pilih Provider");
                listProviderId.add("proCode");
                isFromDefault = true;
                mPresenter.getListProviderSelada(String.valueOf(pos));
        }
    }

    private void setSpinnerProductData() {
        // Creating adapter for spinner
        Log.d("isSpinnerProductData", "ON");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseActivity.this, R.layout.layout_spinner_text, listProductName) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };

        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        spinnerProduct.setAdapter(dataAdapter);
        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    productCode = listProductCode.get(position);
                    VOC = listInqCmd.get(position);
                } else {
                    productCode = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerProviderData() {
        // Creating adapter for spinner
        Log.d("onSpinnerData", "ON");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseActivity.this, R.layout.layout_spinner_text, listProductName) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };

        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        spinnerProvider.setAdapter(dataAdapter);
        spinnerProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    providerId = listProviderId.get(position);
                    if (pos == Constant.VOUCHER_GAME) {
                        if (providerId.equals(Constant.mobileLegendId)) {
                            jenisNomor.setText("User & Server ID");
                            no.setHint("521212902009");
                        } else {
                            jenisNomor.setText("User ID");
                            no.setHint("88XXXXXXXX");
                        }
                    } else {
                        if (isFromDefault) {
                            mPresenter.getListProductSelada(providerId);
                        }
                    }
                } else {
                    providerId = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
