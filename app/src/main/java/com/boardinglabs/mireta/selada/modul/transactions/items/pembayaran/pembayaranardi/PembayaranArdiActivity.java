package com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.pembayaranardi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.fontview.RobotoLightTextView;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.ApiLocal;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Trx.TransactionArdi;
import com.boardinglabs.mireta.selada.component.network.entities.Item;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionToCashier;
import com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.akun.rfid.TapCard;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.ItemsPresenter;
import com.boardinglabs.mireta.selada.modul.transactions.items.ItemsView;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.Items;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.PembayaranAdapter;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.PembayaranSuksesActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PembayaranArdiActivity extends BaseActivity implements ItemsView, CommonInterface {

    private ItemsPresenter mPresenter;
    private List<TransactionPost.Items> transactionItems;
    private List<TransactionPost.Stock> details;
    private TransactionToCashier transactionToCashier;
    private List<TransactionToCashier.Items> itemsList;
    private TransactionPost transactionPost;
    private List<Items> orederditems;
    private List<Items> mItems;
    private long mTotal;
    private Dialog dialog;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER = 999;
    private long totalPrice;
    private int flag;
    private long nomBayar;
    private long sisaSaldo;
    private Context context;
    private String UID = "";
    private String member_name;

    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams")
    TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;

    @BindView(R.id.tvAmount)
    RobotoLightTextView tvAmount;
    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvKembalian)
    TextView tvKembalian;
    @BindView(R.id.tvNameTenant)
    TextView tvNameTenant;
    @BindView(R.id.tvBusinessAddress)
    TextView tvBusinessAddress;
    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.btnBayar)
    Button btnBayar;

    @SuppressLint("HandlerLeak")
    public Handler handlerPembayaranArdi = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID);
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    private String formatOrderNo;
    private String member_lulusan, member_angkatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ItemsPresenter(this, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pembayaran_ardi;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("PEMBAYARAN");
        context = this;

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        mTotal = Long.parseLong(intent.getStringExtra("total"));
        tvTotalAmount.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(mTotal)));

        tvNameTenant.setText(loginBusiness.name);
        tvBusinessAddress.setText(loginBusiness.address);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s1 = new SimpleDateFormat("HH:mm | yyyy-MM-dd");
        String format1 = s1.format(new Date());
        tvOrderDate.setVisibility(View.VISIBLE);
        tvOrderDate.setText(format1);

        orederditems = new ArrayList<>();
        mItems = new ArrayList<>();

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {

                Items myObject = new Items();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderQty = String.valueOf(jsonObject.get("order_qty"));
                myObject.setId(String.valueOf(jsonObject.get("id")));
                myObject.setName(String.valueOf(jsonObject.getJSONObject("item").get("name")));
                myObject.setOrder_qty(Long.parseLong(orderQty));
                myObject.setPrice(String.valueOf(jsonObject.getJSONObject("item").get("price")));

                orederditems.add(myObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PembayaranAdapter adapter = new PembayaranAdapter(orederditems, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        updateTotalBottom();

    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new ItemsPresenter(this, this);
    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCANNER && resultCode == Activity.RESULT_OK) {
            String resultData = data.getStringExtra("scan_data");
            Log.d("Data Scan", resultData);
            updateTotalBottom();
            createTransactionArdi(resultData);
        }
    }

    @OnClick(R.id.btnBayar)
    void onClickButton() {
//        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(PembayaranArdiActivity.this), android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(Objects.requireNonNull(PembayaranArdiActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
//        } else {
//            Intent intent = new Intent(PembayaranArdiActivity.this, Scanner.class);
//            startActivityForResult(intent, REQUEST_SCANNER);
//        }
        li = LayoutInflater.from(context);
        promptsView = (TapCard) li.inflate(R.layout.tap_card, null);

        alertDialogBuilder = new AlertDialog.Builder(context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertTaps = alertDialogBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(alertTaps.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
            promptsView.init(handlerPembayaranArdi);
            promptsView.searchBegin();
            promptsView.setOkListener(v -> {
                alertTaps.dismiss();
                promptsView.searchEnd();
                ((Activity) context).finish();
            });

            alertTaps.show();
            alertTaps.getWindow().setAttributes(lp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void settingDialog() {
        setDialog(R.layout.layout_top_sheet);
        EditText etNominal = (EditText) dialog.findViewById(R.id.etNominalBayar);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerPay);
        Button lanjut = (Button) dialog.findViewById(R.id.btnLanjut);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layoutJumlahBayar);
        CheckedTextView checked1 = (CheckedTextView) dialog.findViewById(R.id.checked1);
        CheckedTextView checked2 = (CheckedTextView) dialog.findViewById(R.id.checked2);
        CheckedTextView checked3 = (CheckedTextView) dialog.findViewById(R.id.checked3);
        CheckedTextView checked4 = (CheckedTextView) dialog.findViewById(R.id.checked4);
        CheckedTextView checked5 = (CheckedTextView) dialog.findViewById(R.id.checked5);
        CheckedTextView checked6 = (CheckedTextView) dialog.findViewById(R.id.checked6);
        CheckedTextView checked7 = (CheckedTextView) dialog.findViewById(R.id.checked7);

        checked1.setOnClickListener(v -> {
            checked1.setChecked(true);
            checked4.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(false);
            checked5.setChecked(false);
            flag = 1;
            checked3.setChecked(false);
            checked6.setChecked(false);
            checked7.setChecked(false);
        });

        checked2.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(true);
            checked5.setChecked(false);
            flag = 2;
            checked3.setChecked(false);
            checked6.setChecked(false);
            checked7.setChecked(false);
        });

        checked3.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(false);
            checked5.setChecked(false);
            flag = 3;
            checked3.setChecked(true);
            checked6.setChecked(false);
            checked7.setChecked(false);
        });

        checked4.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(true);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(false);
            checked5.setChecked(false);
            flag = 4;
            checked3.setChecked(false);
            checked6.setChecked(false);
            checked7.setChecked(false);
        });

        checked5.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(false);
            checked5.setChecked(true);
            flag = 5;
            checked3.setChecked(false);
            checked6.setChecked(false);
            checked7.setChecked(false);
        });

        checked6.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked2.setChecked(false);
            checked5.setChecked(false);
            flag = 6;
            checked3.setChecked(false);
            checked6.setChecked(true);
            checked7.setChecked(false);
        });

        checked7.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            checked7.setChecked(true);
            checked2.setChecked(false);
            checked5.setChecked(false);
            etNominal.setVisibility(View.VISIBLE);
            checked3.setChecked(false);
            checked6.setChecked(false);
            flag = 7;
        });

        lanjut.setOnClickListener(v -> {
            nomBayar = 0;
            switch (flag) {
                case 1:
                    nomBayar = mTotal;
                    break;
                case 2:
                    nomBayar = 5000;
                    break;
                case 3:
                    nomBayar = 10000;
                    break;
                case 4:
                    nomBayar = 20000;
                    break;
                case 5:
                    nomBayar = 50000;
                    break;
                case 6:
                    nomBayar = 100000;
                    break;
                case 7:
                    break;
            }

            if (nomBayar != 0) {
                tvKembalian.setVisibility(View.VISIBLE);
                try {
                    long total = mTotal;
                    total = (nomBayar - total);
                    if (total >= 0) {
                        dialog.dismiss();
                        btnBayar.setEnabled(true);
                        tvKembalian.setText("Kembalian : Rp " + MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
                    } else {
                        btnBayar.setEnabled(false);
                        Toast.makeText(context, "Nominal yang dibayarkan kurang dari total harga", Toast.LENGTH_LONG).show();
                        tvKembalian.setText("Kembalian : Rp " + MethodUtil.toCurrencyFormat("0") + "");
                    }
                } catch (Exception e) {
                    tvKembalian.setText(" ");
                }
            } else {
                Toast.makeText(context, "Silahkan masukan nominal pembayaran", Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner Drop down elements
        List<String> method = new ArrayList<String>();
        method.add("Tunai");
        method.add("Debit");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_text, method);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getItemAtPosition(position).toString().equals("Tunai")) {
                    layout.setVisibility(View.VISIBLE);
                } else {
                    layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    long total = mTotal;
                    total = (Integer.valueOf(s.toString()) - total);
                    if (total >= 0) {
                        btnBayar.setEnabled(true);
                        tvKembalian.setText("Kembalian : Rp " + MethodUtil.toCurrencyFormat(Long.toString(total)) + "");
                    } else {
                        btnBayar.setEnabled(false);
                        Toast.makeText(context, "Masukan nominal yang dibayarkan", Toast.LENGTH_LONG).show();
                        tvKembalian.setText("Kembalian : Rp " + MethodUtil.toCurrencyFormat("0") + "");
                    }
                } catch (Exception e) {
                    tvKembalian.setText(" ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateTotalBottom() {
        long totalQty = 0;
        totalPrice = 0;
        transactionItems = new ArrayList<>();
        details = new ArrayList<>();
        itemsList = new ArrayList<>();

        for (int i = 0; i < orederditems.size(); i++) {
            String uniqueId = UUID.randomUUID().toString();
            Log.d("Tag ID", orederditems.get(i).getId());
            transactionItems.add(new TransactionPost.Items(orederditems.get(i).getId(), orederditems.get(i).getOrder_qty(), 0));
            itemsList.add(new TransactionToCashier.Items(uniqueId, orederditems.get(i).getName(), orederditems.get(i).getOrder_qty(), orederditems.get(i).getPrice(), "0"));
            details.add(new TransactionPost.Stock(orederditems.get(i).getId(), orederditems.get(i).getOrder_qty(), orederditems.get(i).getPrice(), 0));
            totalQty += orederditems.get(i).getOrder_qty();
            long total_price = (long) (Integer.valueOf(orederditems.get(i).getPrice()) * orederditems.get(i).getOrder_qty());
            totalPrice += total_price;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");
        String format = s.format(new Date());

        final int min = 10000;
        final int max = 99999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        Log.d("NUM RANDOM", random + "");

        format = "M" + loginBusiness.name.substring(0, 1).toUpperCase() + format + random;
        formatOrderNo = format;

        totalPrice = mTotal;
        transactionPost = new TransactionPost(loginStockLocation.id, format, totalQty, totalPrice + "", 1, 2, 2, details);

        Gson gson = new Gson();
        String json = gson.toJson(transactionPost);
        System.out.println(json);
    }

    private void onClickBayar(String member_id, long sisaSaldo) {
        Loading.show(PembayaranArdiActivity.this);
        ApiLocal.apiInterface().createTransactions(transactionPost, "Bearer " + PreferenceManager.getSessionTokenMireta()).enqueue(new Callback<ApiResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionResponse>> call, Response<ApiResponse<TransactionResponse>> response) {
                Loading.hide(PembayaranArdiActivity.this);
                try {
                    if (response.isSuccessful()) {
                        TransactionResponse apiResponse = response.body().getData();
                        Date d = null;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try {
                            d = sdf.parse(apiResponse.getCreatedAt());
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }

                        sdf.applyPattern("dd MMM yyyy");
                        String orderDate = sdf.format(d);
                        sdf.applyPattern("HH:mm");
                        String orderTime = sdf.format(d);

                        String order_no = apiResponse.getId() + "";
                        String total = totalPrice + "";
                        Log.d("DATA TRX", order_no + " " + total);
                        Intent intent = new Intent(PembayaranArdiActivity.this, PembayaranSuksesActivity.class);
                        intent.putExtra("order_no", order_no);
                        intent.putExtra("order_date", orderDate);
                        intent.putExtra("order_time", orderTime);
                        intent.putExtra("member_name", member_name);
                        intent.putExtra("member_lulusan", member_lulusan);
                        intent.putExtra("member_angkatan", member_angkatan);
                        intent.putExtra("total", total);
                        intent.putExtra("sisaSaldo", sisaSaldo + "");
                        intent.putExtra("nomBayar", nomBayar + "");
                        intent.putExtra("mTotal", mTotal + "");
                        intent.putExtra("whatToDo", Constant.DO_PRINT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
//                        Toast.makeText(context, Objects.requireNonNull(response.body()).getMessage()!=null ?
//                                response.body().getMessage() : response.message(), Toast.LENGTH_SHORT).show();
                        sendReversal(member_id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionResponse>> call, Throwable t) {
                Log.d("TAG onFailure POS", t.getMessage());
                Loading.hide(PembayaranArdiActivity.this);
            }
        });
    }

    private void checkMember(String UID){
        Loading.show(context);
        Api.apiInterface().checkMember(UID, "Bearer "+PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<CheckMemberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckMemberResponse>> call, Response<ApiResponse<CheckMemberResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        CheckMemberResponse memberResponse = Objects.requireNonNull(response.body()).getData();
                        String member_id = memberResponse.getId();
                        member_name = memberResponse.getFullname();
                        member_lulusan = memberResponse.getOpt1();
                        member_angkatan = memberResponse.getOpt2();
                        createTransactionArdi(member_id);
                    } else {
                        if (response.message().equals("Unauthorized")) {
                            setDialog(R.layout.dialog_session_expired);
                            ImageView btnClose = dialog.findViewById(R.id.btnClose);
                            btnClose.setOnClickListener(v -> dialog.dismiss());
                            Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                            btnRelogin.setOnClickListener(v -> {
                                goToLoginPage();
                            });
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                                if (jObjError.getString("error").startsWith("Balance not")) {
                                    Toast.makeText(context, "Saldo anda kurang, silahkan lakukan topup terlebih dahulu", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckMemberResponse>> call, Throwable t) {

            }
        });
    }

    private void sendReversal(String member_id) {
        Loading.show(context);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("booth_id", PreferenceManager.getBoothId())
                .addFormDataPart("member_id", member_id)
                .addFormDataPart("user_id", PreferenceManager.getUserIdArdi())
                .addFormDataPart("amount", mTotal + "")
                .addFormDataPart("date", date)
                .build();

        Api.apiInterface().sendReversal(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Loading.hide(context);
                if (response.isSuccessful()) {
                    Toast.makeText(context, " Transaksi gagal, mengirim reversal", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Gagal mengirim reversal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    private void cekSaldo(String member_id) {
        Loading.show(context);
        Api.apiInterface().cekSaldo(member_id, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
            @Override
            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
                Loading.hide(context);
                try {
                    Members members = response.body().getData();
                    int saldo = Integer.parseInt(members.getBalance());
                    sisaSaldo = saldo;
                    if (saldo >= mTotal) {
                        createTransactionArdi(member_id);
                    } else {
                        Toast.makeText(context, "Saldo tidak mencukupi, silahkan isi ulang saldo anda", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Members>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    public void createTransactionArdi(String member_id) {
        Loading.show(context);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String date = f.format(new Date());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("business_id", loginBusiness.id)
                .addFormDataPart("member_id", member_id)
                .addFormDataPart("business_name", loginBusiness.name)
                .addFormDataPart("transaction_code", formatOrderNo)
                .addFormDataPart("amount", mTotal + "")
                .addFormDataPart("date", date)
                .build();
        Api.apiInterface().postTransactionArdi(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<TransactionArdi>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionArdi>> call, Response<ApiResponse<TransactionArdi>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        TransactionArdi transactionArdi = response.body().getData();
                        sisaSaldo = transactionArdi.getMember().getBalance();
                        Log.d("CREATE TRX ARDI", "MASUK");
                        onClickBayar(member_id, sisaSaldo);
                    } else {
                        if (response.message().equals("Unauthorized")) {
                            setDialog(R.layout.dialog_session_expired);
                            ImageView btnClose = dialog.findViewById(R.id.btnClose);
                            btnClose.setOnClickListener(v -> dialog.dismiss());
                            Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                            btnRelogin.setOnClickListener(v -> {
                                goToLoginPage();
                            });
                        }

                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            if (jObjError.getString("error").startsWith("Balance not")) {
                                Toast.makeText(context, "Saldo anda kurang, silahkan lakukan topup terlebih dahulu", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionArdi>> call, Throwable t) {
                Loading.hide(context);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    @Override
    public void onSuccessGetItems(List<Item> transactionItems) {

    }

    @Override
    public void onSuccessCreateTransaction(ResponseBody responseBody) {
        Log.d("TAG SUKSES", String.valueOf(responseBody));
        Intent intent = new Intent(PembayaranArdiActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setDialog(int layout) {

        dialog = new Dialog(Objects.requireNonNull(PembayaranArdiActivity.this));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
}
