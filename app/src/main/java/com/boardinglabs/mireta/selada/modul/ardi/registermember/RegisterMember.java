package com.boardinglabs.mireta.selada.modul.ardi.registermember;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.Api;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.Loading;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.akun.rfid.TapCard;
import com.boardinglabs.mireta.selada.modul.ardi.HomeArdiActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterMember extends AppCompatActivity {

    private Context context;
    private Dialog dialog;
    AlertDialog alertTaps;
    LayoutInflater li;
    @SuppressLint("InflateParams")
    TapCard promptsView;
    AlertDialog.Builder alertDialogBuilder;
    public String UID = "";
    private List<String> lulusanList;
    private String[] arraySpinner;

    @BindView(R.id.input_fullname)
    EditText input_fullname;
    @BindView(R.id.spinner_input_sma)
    Spinner spinner_input_sma;
    @BindView(R.id.input_angkatan)
    EditText input_angkatan;
    @BindView(R.id.input_sma_lainnya)
    EditText input_sma_lainnya;
    @BindView(R.id.input_no_kartu)
    EditText input_no_kartu;

    @SuppressLint("HandlerLeak")
    public Handler handlerRegisterMember = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            UID = bundle.getString("uid");
            Log.d("UID", UID);

            alertTaps.dismiss();
            promptsView.searchEnd();
        }
    };

    private Calendar calendar;
    private String year = "";
    private DatePickerDialog.OnDateSetListener date;
    private String sekolahLainnya = "";

    @OnClick(R.id.btn_register)
    void onClickRegister() {
        if (UID.equals("")) {
            Toast.makeText(context, "Terjadi kesalahan, kartu tidak terbaca", Toast.LENGTH_SHORT).show();
        } else if (input_fullname.getText().toString().equals("") || input_angkatan.getText().toString().equals("") ||
                spinner_input_sma.getSelectedItem().toString().equals("Pilih Lulusan") ||
                input_no_kartu.getText().toString().equals("")) {
            Toast.makeText(context, "Input data dengan benar", Toast.LENGTH_SHORT).show();
        } else {
            doRegister();
        }
    }

//    @OnClick(R.id.input_angkatan)
//    void onClickInputAngkatan(){
//        MonthYearPickerDialog pd = new MonthYearPickerDialog();
//        pd.setListener(date);
//        pd.show(getFragmentManager(), "MonthYearPickerDialog");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_member);
        ButterKnife.bind(this);
        context = this;

        calendar = Calendar.getInstance();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        };


        lulusanList = new ArrayList<>();
        arraySpinner = new String[] {
                "Pilih Sekolah", "SMAN 1 BANDUNG", "SMAN 2 BANDUNG", "SMAN 3 BANDUNG", "SMAN 4 BANDUNG", "SMAN 5 BANDUNG", "SMAN 6 BANDUNG", "LAINNYA"
        };

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterMember.this, R.layout.layout_spinner_text, arraySpinner){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        spinner_input_sma.setAdapter(dataAdapter);
        spinner_input_sma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    if (spinner_input_sma.getSelectedItem().toString().equals("LAINNYA")){
                        input_sma_lainnya.setVisibility(View.VISIBLE);
                    } else {
                        input_sma_lainnya.setVisibility(View.GONE);
                        sekolahLainnya = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
    }

    private void updateDate() {
        String format = "yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        year = sdf.format(calendar.getTime());
//        year = sdf.format(createDialogWithoutDateField().getDatePicker().getYear());
        input_angkatan.setText(year);
        Log.d("YEAR", year);
    }

    private void doRegister() {
        Loading.show(context);

        if (input_sma_lainnya.getVisibility() == View.VISIBLE && input_sma_lainnya.getText().toString().equals("")){
            sekolahLainnya = "null";
        } else if (input_sma_lainnya.getVisibility() == View.VISIBLE && !input_sma_lainnya.getText().toString().equals("")){
            sekolahLainnya = input_sma_lainnya.getText().toString();
        }

        if (input_angkatan.getText().toString().length() < 4){
            input_angkatan.setError("Angkatan harus 4 digit");
            Loading.hide(context);
        } else if (sekolahLainnya.equals("null")){
            input_sma_lainnya.setError("SMA harus diisi");
            Loading.hide(context);
        } else {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fullname", input_fullname.getText().toString())
                    .addFormDataPart("opt_1", sekolahLainnya.equals("") ? spinner_input_sma.getSelectedItem().toString() : sekolahLainnya)
                    .addFormDataPart("opt_2", input_angkatan.getText().toString())
                    .addFormDataPart("opt_3", input_no_kartu.getText().toString())
                    .addFormDataPart("card_number", UID)
                    .build();

            Api.apiInterface().doRegisterMember(requestBody, "Bearer " + PreferenceManager.getSessionTokenArdi()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Loading.hide(context);
                    if (response.isSuccessful()) {
                        showDialogLayout(R.layout.layout_sukses_register_member);
                        Button btnOK = dialog.findViewById(R.id.btnOK);
                        btnOK.setOnClickListener(v ->
                        {
                            Intent intent = new Intent(RegisterMember.this, HomeArdiActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                            Toast.makeText(context, jObjError.getString("error")!=null?jObjError.getString("error"):jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Loading.hide(context);
                    t.printStackTrace();
                }
            });
        }
    }

    private void showDialogLayout(int layout) {
        dialog = new Dialog(Objects.requireNonNull(RegisterMember.this));
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
