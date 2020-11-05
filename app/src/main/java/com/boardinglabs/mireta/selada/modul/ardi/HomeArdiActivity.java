package com.boardinglabs.mireta.selada.modul.ardi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Users;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.akun.rfid.TapCard;
import com.boardinglabs.mireta.selada.modul.ardi.freemeal.FreeMeal;
import com.boardinglabs.mireta.selada.modul.ardi.historytopup.HistoryTopupActivity;
import com.boardinglabs.mireta.selada.modul.ardi.pengaturan.PengaturanActivity;
import com.boardinglabs.mireta.selada.modul.ardi.registermember.RegisterMember;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeArdiActivity extends BaseActivity {

    private Context context;
    private Dialog dialog;
    private int flag;
    private long nomBayar;
    final int REQUEST_CODE = 564;
    final int REQUEST_SCANNER_CEK = 565;
    final int REQUEST_SCANNER = 999;
    private String UID = "";
    private String spinnerBoothId, spinnerUserId;
    private List<String> booths_id, user_id, booths_name, user_name;

    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams")
    TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;

    @SuppressLint("HandlerLeak")
    public Handler handlerTopup = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "handleTopup");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handlerCekSaldo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handlerRegisterMember = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "register");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handlerCheckFreeMeal = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);
            checkMember(UID, "handleFreeMeal");
            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    private void checkMember(String UID, String flag) {
        Loading.show(context);
        Api.apiInterface().checkMember(UID, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<CheckMemberResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckMemberResponse>> call, Response<ApiResponse<CheckMemberResponse>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()) {
                        CheckMemberResponse memberResponse = response.body().getData();
                        String member_id = memberResponse.getId();
                        String member_name = memberResponse.getFullname();
                        String member_lulusan = memberResponse.getOpt1();
                        String member_angkatan = memberResponse.getOpt2();
                        if (flag.equals("handleTopup")) {
                            doTopup(member_id, member_name, member_lulusan, member_angkatan);
                        } else if (flag.equals("handleFreeMeal")) {
                            Intent intent = new Intent(HomeArdiActivity.this, FreeMeal.class);
                            intent.putExtra("member_id", member_id);
                            intent.putExtra("member_name", member_name);
                            intent.putExtra("member_lulusan", member_lulusan);
                            intent.putExtra("member_angkatan", member_angkatan);
                            startActivity(intent);
                        } else if (flag.equals("register")) {
                            Toast.makeText(context, "Kartu anda telah terdaftar", Toast.LENGTH_SHORT).show();
                        } else {
                            cekSaldo(member_id, member_name, member_lulusan, member_angkatan);
                        }
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

                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        if (flag.equals("register")) {
                            if (jObjError.getString("error").startsWith("Member not")) {
                                Intent intent = new Intent(HomeArdiActivity.this, RegisterMember.class);
                                intent.putExtra("UID", UID);
                                startActivity(intent);
                            } else {
                                try {
                                    Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckMemberResponse>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.freeMeal)
    void onClickSelesai() {
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
            promptsView.init(handlerCheckFreeMeal);
            promptsView.searchBegin();
            promptsView.setOkListener(v1 -> {
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

    @OnClick(R.id.layoutTrx)
    void onClickLayoutTrx() {
        settingDialog();
    }

    @OnClick(R.id.setting)
    void onClickSetting() {
        startActivity(new Intent(HomeArdiActivity.this, PengaturanActivity.class));
    }

    @OnClick(R.id.cekSaldo)
    void onClickCekSaldo() {
//        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(HomeArdiActivity.this), android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(Objects.requireNonNull(HomeArdiActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
//        } else {
//            Intent intent = new Intent(HomeArdiActivity.this, Scanner.class);
//            startActivityForResult(intent, REQUEST_SCANNER_CEK);
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
            promptsView.init(handlerCekSaldo);
            promptsView.searchBegin();
            promptsView.setOkListener(v1 -> {
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

    @OnClick(R.id.historyTopup)
    void onClickhistoryTopup() {
        if (PreferenceManager.getBoothId().equals("")) {
            Toast.makeText(context, "Silahkan pilih booth terlebih dahulu pada menu pengaturan", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(HomeArdiActivity.this, HistoryTopupActivity.class));
        }
    }

    @OnClick(R.id.registerMember)
    void onClickRegisterMember() {
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
            promptsView.init(handlerRegisterMember);
            promptsView.searchBegin();
            promptsView.setOkListener(v1 -> {
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

    private void cekSaldo(String memberId, String member_name, String member_lulusan, String member_angkatan) {
        Loading.show(context);
        Api.apiInterface().cekSaldo(memberId, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Members>>() {
            @Override
            public void onResponse(Call<ApiResponse<Members>> call, Response<ApiResponse<Members>> response) {
                Loading.hide(context);
                try {
                    if (response.isSuccessful()){
                        Members members = response.body().getData();
                        int saldo = Integer.parseInt(members.getBalance());
                        Intent intent = new Intent(HomeArdiActivity.this, SaldoActivity.class);
                        intent.putExtra("sisaSaldo", saldo + "");
                        intent.putExtra("member_name", member_name);
                        intent.putExtra("member_lulusan", member_lulusan);
                        intent.putExtra("member_angkatan", member_angkatan);
                        startActivity(intent);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                            if (response.message().equals("Unauthorized")) {
                                setDialog(R.layout.dialog_session_expired);
                                ImageView btnClose = dialog.findViewById(R.id.btnClose);
                                btnClose.setOnClickListener(v -> dialog.dismiss());
                                Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                                btnRelogin.setOnClickListener(v -> {
                                    goToLoginPage();
                                });
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                        }

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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_ardi;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        context = HomeArdiActivity.this;

        booths_name = new ArrayList<>();
        booths_id = new ArrayList<>();
        user_name = new ArrayList<>();
        user_id = new ArrayList<>();
    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {

    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    @SuppressLint("SetTextI18n")
    private void settingDialog() {
        setDialog(R.layout.layout_topup_sheet);
        EditText etNominal = (EditText) dialog.findViewById(R.id.etNominalBayar);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerPay);
        Spinner spinnerUser = (Spinner) dialog.findViewById(R.id.spinnerUser);
        Button lanjut = (Button) dialog.findViewById(R.id.btnLanjut);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layoutJumlahBayar);
        CheckedTextView checked1 = (CheckedTextView) dialog.findViewById(R.id.checked1);
        CheckedTextView checked2 = (CheckedTextView) dialog.findViewById(R.id.checked2);
        CheckedTextView checked3 = (CheckedTextView) dialog.findViewById(R.id.checked3);
        CheckedTextView checked4 = (CheckedTextView) dialog.findViewById(R.id.checked4);
        CheckedTextView checked5 = (CheckedTextView) dialog.findViewById(R.id.checked5);
        CheckedTextView checked6 = (CheckedTextView) dialog.findViewById(R.id.checked6);
        CheckedTextView checked7 = (CheckedTextView) dialog.findViewById(R.id.checked7);
        CheckedTextView checked8 = (CheckedTextView) dialog.findViewById(R.id.checked8);
        CheckedTextView checked9 = (CheckedTextView) dialog.findViewById(R.id.checked9);

        checked1.setVisibility(View.GONE);
        checked7.setVisibility(View.GONE);

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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
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
            checked8.setChecked(false);
            checked9.setChecked(false);
            flag = 7;
        });

        checked8.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            checked7.setChecked(false);
            checked8.setChecked(true);
            checked2.setChecked(false);
            checked5.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked3.setChecked(false);
            checked6.setChecked(false);
            flag = 8;
            checked9.setChecked(false);
        });

        checked9.setOnClickListener(v -> {
            checked1.setChecked(false);
            checked4.setChecked(false);
            checked7.setChecked(false);
            checked8.setChecked(false);
            checked2.setChecked(false);
            checked5.setChecked(false);
            etNominal.setVisibility(View.GONE);
            checked3.setChecked(false);
            checked6.setChecked(false);
            flag = 9;
            checked9.setChecked(true);
        });

        lanjut.setOnClickListener(v -> {
            dialog.dismiss();
            nomBayar = 0;
            switch (flag) {
                case 1:
                    nomBayar = 0;
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
                case 8:
                    nomBayar = 500000;
                    break;
                case 9:
                    nomBayar = 1000000;
                    break;
            }

            if (nomBayar != 0) {
                setDialog(R.layout.layout_inquiry_topup);
                TextView tvNominalTopup = dialog.findViewById(R.id.tvNominalToptup);
                tvNominalTopup.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(nomBayar));
                ImageView btnClose = dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(v1 -> dialog.dismiss());
                Button btnProsesTopup = dialog.findViewById(R.id.btnProsesTopup);
                btnProsesTopup.setOnClickListener(v1 -> {
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
                        promptsView.init(handlerTopup);
                        promptsView.searchBegin();
                        promptsView.setOkListener(v2 -> {
                            alertTaps.dismiss();
                            promptsView.searchEnd();
                            ((Activity) context).finish();
                        });

                        alertTaps.show();
                        alertTaps.getWindow().setAttributes(lp);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
//                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(HomeArdiActivity.this), android.Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Objects.requireNonNull(HomeArdiActivity.this), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
//                } else {
//                    Intent intent = new Intent(HomeArdiActivity.this, Scanner.class);
//                    startActivityForResult(intent, REQUEST_SCANNER);
//                }
            } else {
                Toast.makeText(HomeArdiActivity.this, "Silahkan masukan nominal Topup", Toast.LENGTH_SHORT).show();
            }
        });

//        Loading.show(HomeArdiActivity.this);
//        Api.apiInterface().getListBooth("Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<Booths>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<List<Booths>>> call, Response<ApiResponse<List<Booths>>> response) {
//                Loading.hide(HomeArdiActivity.this);
//                try {
//                    List<Booths> res = response.body().getData();
//                    for (int i = 0; i < res.size(); i++) {
//                        Booths booths = res.get(i);
//                        booths_name.add(booths.getName());
//                        booths_id.add(booths.getId());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<Booths>>> call, Throwable t) {
//                Loading.hide(HomeArdiActivity.this);
//                t.printStackTrace();
//            }
//        });
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_text, booths_name);
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
//
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinnerBoothId = booths_id.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
        Loading.show(HomeArdiActivity.this);
        Api.apiInterface().getListUsers("Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<List<Users>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Users>>> call, Response<ApiResponse<List<Users>>> response) {
                Loading.hide(HomeArdiActivity.this);
                try {
                    List<Users> res = response.body().getData();
                    for (int i = 0; i < res.size(); i++) {
                        Users users = res.get(i);
                        user_name.add(users.getFullname());
                        user_id.add(users.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Users>>> call, Throwable t) {
                Loading.hide(HomeArdiActivity.this);
                t.printStackTrace();
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.layout_spinner_text, user_name);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(R.layout.layout_spinner_dropdown);

        // attaching data adapter to spinner
        spinnerUser.setAdapter(dataAdapter2);

        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerUserId = user_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_SCANNER && resultCode == Activity.RESULT_OK) {
//            String resultData = data.getStringExtra("scan_data");
//            Log.d("Data Scan", resultData);
////            onClickBayar();
//            doTopup(resultData);
//        } else if (requestCode == REQUEST_SCANNER_CEK && resultCode == Activity.RESULT_OK){
//            String resultData = data.getStringExtra("scan_data");
//            cekSaldo(member_id, member_name, member_lulusan, resultData);
//        }
    }

    private void doTopup(String resultData, String member_name, String member_lulusan, String member_angkatan) {

        if (PreferenceManager.getBoothId().equals("")) {
            Toast.makeText(context, "Silahkan pilih booth terlebih dahulu pada menu pengaturan", Toast.LENGTH_SHORT).show();
//            booths_id.get(0) != null ? booths_id.get(0) : "0"
        } else if (PreferenceManager.getMasterKey().equals("")) {
            Toast.makeText(context, "Silahkan isi Master Key terlebih dahulu", Toast.LENGTH_SHORT).show();
//            "5419720191027151637867954"
        } else {
            Loading.show(HomeArdiActivity.this);

            final int min = 10000;
            final int max = 99999;
            final int random = new Random().nextInt((max - min) + 1) + min;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");
            String format = s.format(new Date());
            Log.d("NUM RANDOM", random + "");
            format = "M" + loginBusiness.name.substring(0, 1).toUpperCase() + format + random;

            @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = f.format(new Date());
            RequestBody requestBody = null;

            try {
                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("booth_id", PreferenceManager.getBoothId())
                        .addFormDataPart("booth_secret", PreferenceManager.getMasterKey())
                        .addFormDataPart("member_id", resultData)
                        .addFormDataPart("user_id", PreferenceManager.getUserIdArdi())
                        .addFormDataPart("amount", nomBayar + "")
                        .addFormDataPart("date", date)
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String finalFormat = format;
            Api.apiInterface().doTopup(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse<Topup>>() {
                @Override
                public void onResponse(Call<ApiResponse<Topup>> call, Response<ApiResponse<Topup>> response) {
                    Loading.hide(HomeArdiActivity.this);
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        Topup topup = response.body().getData();
                        Toast.makeText(HomeArdiActivity.this, "Topup sejumlah Rp " + nomBayar + " berhasil dilakukan", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomeArdiActivity.this, TopupSuksesActivity.class);
                        intent.putExtra("nomBayar", nomBayar + "");
                        intent.putExtra("saldo", topup.getMember().getBalance() + "");
                        intent.putExtra("date", date);
                        intent.putExtra("order_no", finalFormat);
                        intent.putExtra("member_name", member_name);
                        intent.putExtra("member_lulusan", member_lulusan);
                        intent.putExtra("member_angkatan", member_angkatan);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        dialog.dismiss();
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("error"), Toast.LENGTH_SHORT).show();
                            if (response.message().equals("Unauthorized")) {
                                setDialog(R.layout.dialog_session_expired);
                                ImageView btnClose = dialog.findViewById(R.id.btnClose);
                                btnClose.setOnClickListener(v -> dialog.dismiss());
                                Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                                btnRelogin.setOnClickListener(v -> {
                                    goToLoginPage();
                                });
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                            if (response.message().equals("Unauthorized")) {
                                setDialog(R.layout.dialog_session_expired);
                                ImageView btnClose = dialog.findViewById(R.id.btnClose);
                                btnClose.setOnClickListener(v -> dialog.dismiss());
                                Button btnRelogin = dialog.findViewById(R.id.btnRelogin);
                                btnRelogin.setOnClickListener(v -> {
                                    goToLoginPage();
                                });
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Topup>> call, Throwable t) {
                    Loading.hide(HomeArdiActivity.this);
                    t.printStackTrace();
                }
            });
        }
    }

    private void setDialog(int layout) {

        dialog = new Dialog(Objects.requireNonNull(HomeArdiActivity.this));
        //set content
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
}
