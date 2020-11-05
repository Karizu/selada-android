package com.boardinglabs.mireta.selada.modul.akun.pengaturan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.akun.ubahpassword.UbahPaswordActivity;
import com.boardinglabs.mireta.selada.modul.auth.login.LoginActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.HomeBillerActivity;
import com.boardinglabs.mireta.selada.modul.master.profil.toko.ProfilTokoActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PengaturanAkunActivity extends BaseActivity {

    private Dialog dialog;
    private String TID, MID;
    @BindView(R.id.scanWifiButton)
    Button scanWifiButton;
    @BindView(R.id.wifiLayout)
    RelativeLayout wifiLayout;
    @BindView(R.id.wifiSwitch)
    Switch wifiSwitch;
    @BindView(R.id.tvWifiSSID)
    android.widget.TextView txtConnectedWifi;

//    scanWifiButton = (Button) findViewById(R.id.scanWifiButton);
//    wifiLayout = (RelativeLayout) findViewById(R.id.wifiLayout);
//    wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
//    txtConnectedWifi = (android.widget.TextView) findViewById(R.id.tvWifiSSID);
    @BindView(R.id.tvTID)
    TextView tvTID;

    @BindView(R.id.tvMID)
    TextView tvMID;

    @BindView(R.id.tvSN)
    TextView tvSN;

    @BindView(R.id.tvIMEI)
    TextView tvIMEI;

    private WifiManager mWifiManager;
    private BroadcastReceiver wifiScanReceiver;
    private BroadcastReceiver wifiReceiver;
    private String wifiSSID;
    private WifiConfiguration conf;
    private boolean isWantToConnectWEP;
    private boolean isWantToConnectWPA;
    private boolean isWantToScanWiFi;
    private boolean showingWiFiListDialog;
    private List<ScanResult> scanResults;
    private WifiAdapter wifiArrayAdapter;
    private static String INTENT_PROFIL = "intentProfil";

    @OnClick(R.id.btnKeluar)
    void onClickbtnKeluar() {
        goToLoginPage();
    }

    @OnClick(R.id.btnUbahAkun)
    void onClickbtnUbahAkun() {
        startActivity(new Intent(PengaturanAkunActivity.this, UbahPaswordActivity.class));
    }

    @OnClick(R.id.btnProfilToko)
    void onClickbtnProfilToko() {
        Intent intent = new Intent(PengaturanAkunActivity.this, ProfilTokoActivity.class);
        intent.putExtra(INTENT_PROFIL, INTENT_PROFIL);
        startActivity(intent);
    }

    @OnClick(R.id.btnClearCache)
    void onCLickClearCache() {
        showDialogs(R.layout.layout_clear_cache);
        Button btnClearCache = dialog.findViewById(R.id.btnClearData);
        btnClearCache.setOnClickListener(v -> {
            dialog.dismiss();
            PreferenceManager.logOut();
            Intent intent = new Intent(PengaturanAkunActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btnClearScreenCache)
    void onCLickClearScreenCache() {
        showDialogs(R.layout.layout_clear_cache);
        Button btnClearCache = dialog.findViewById(R.id.btnClearData);
        TextView content = dialog.findViewById(R.id.tvContent);
        TextView title = dialog.findViewById(R.id.tvTitle);
        content.setText("Clear screen cache akan me-reset screen PPOB, apakah anda yakin?");
        title.setText("Clear Screen Cache");
        btnClearCache.setText("Clear");
        btnClearCache.setOnClickListener(v -> {
            dialog.dismiss();
            PreferenceManager.clearMenu();
            Intent intent = new Intent(PengaturanAkunActivity.this, HomeBillerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @OnClick(R.id.btnTestConnection)
    void onClickTestConnection() {
        try {
            if (isNetworkAvailable()){
                Toast.makeText(PengaturanAkunActivity.this, "Connection Ok", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PengaturanAkunActivity.this, "Jaringan tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnSetPassVoid)
    void onClickSetPass() {
        showDialogs(R.layout.layout_set_pass_void);
        EditText etPasswordVoid = dialog.findViewById(R.id.etPasswordVoid);
        EditText etPasswordVoidConfirm = dialog.findViewById(R.id.etPasswordVoidConfirm);
        etPasswordVoid.setText(PreferenceManager.getPassVoid());
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        Button btnUbah = dialog.findViewById(R.id.btnUbah);
        if (etPasswordVoid.getText().toString().equals("")) {
            btnSimpan.setVisibility(View.VISIBLE);
            btnUbah.setVisibility(View.GONE);
        } else {
            btnSimpan.setVisibility(View.GONE);
            btnUbah.setVisibility(View.VISIBLE);
            etPasswordVoidConfirm.setVisibility(View.GONE);
        }
        btnSimpan.setOnClickListener(v -> {
            if (etPasswordVoid.getText().toString().equals("") || etPasswordVoidConfirm.getText().toString().equals("")) {
                Toast.makeText(context, "Silahkan lengkapi isian", Toast.LENGTH_SHORT).show();
            } else {
                if (etPasswordVoidConfirm.getText().toString().equals(etPasswordVoid.getText().toString())) {
                    PreferenceManager.setPassVoid(etPasswordVoid.getText().toString());
                    Toast.makeText(context, "Berhasil set password", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    etPasswordVoidConfirm.setError("Password tidak sesuai");
                }
            }
        });

        btnUbah.setOnClickListener(v -> {
            dialog.dismiss();
            showDialogs(R.layout.layout_ubah_pass);
            EditText etPasswordLama = dialog.findViewById(R.id.etPasswordLama);
            EditText etPasswordBaru = dialog.findViewById(R.id.etPasswordBaru);
            EditText etPasswordBaruConfirm = dialog.findViewById(R.id.etPasswordBaruConfirm);
            Button btnSimpanPassword = dialog.findViewById(R.id.btnSimpanPassword);
            btnSimpanPassword.setOnClickListener(v1 -> {
                if (etPasswordLama.getText().toString().equals("") ||
                        etPasswordBaru.getText().toString().equals("") ||
                        etPasswordBaruConfirm.getText().toString().equals("")) {
                    Toast.makeText(context, "Silahkan lengkapi isian", Toast.LENGTH_SHORT).show();
                } else {
                    if (etPasswordLama.getText().toString().equals(PreferenceManager.getPassVoid()) && etPasswordBaruConfirm.getText().toString().equals(etPasswordBaru.getText().toString())) {
                        PreferenceManager.setPassVoid(etPasswordBaru.getText().toString());
                        Toast.makeText(context, "Berhasil mengubah password", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (!etPasswordLama.getText().toString().equals(PreferenceManager.getPassVoid())) {
                        etPasswordLama.setError("Password tidak sesuai");
                    } else if (!etPasswordBaruConfirm.getText().toString().equals(etPasswordBaru.getText().toString())) {
                        etPasswordBaruConfirm.setError("Password tidak sesuai");
                    } else {
                        Toast.makeText(context, "Silahkan input password dengan benar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pengaturan_akun;
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Pengaturan");
        String serialNum = Build.SERIAL;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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

        Intent intent = getIntent();
        if (intent.getStringExtra("TID") != null) {
            TID = intent.getStringExtra("TID");
            MID = intent.getStringExtra("MID");
            PreferenceManager.setTID(TID);
            PreferenceManager.setMID(MID);

            tvTID.setText(TID);
            tvMID.setText(MID);
            tvSN.setText(serialNum);
            tvIMEI.setText(Objects.requireNonNull(telephonyManager).getDeviceId());

        } else {
            if (PreferenceManager.getTID() != null) {
                tvTID.setText(PreferenceManager.getTID());
                tvMID.setText(PreferenceManager.getMID());
                tvSN.setText(serialNum);
                tvIMEI.setText(Objects.requireNonNull(telephonyManager).getDeviceId());
            } else {
//                HashMap<String, String> data = new HashMap<String, String>();
//                data.put("menu", "setting");
//                Utils.openApp(this, "id.co.tornado.billiton", data);
                tvTID.setText("-");
                tvMID.setText("-");
                tvSN.setText(serialNum);
                tvIMEI.setText(Objects.requireNonNull(telephonyManager).getDeviceId());
            }
        }

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mWifiManager.setWifiEnabled(isChecked);
                updateWifiStatus(isChecked);
            }
        });

        wifiReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context c, Intent intent){
                if(mWifiManager != null) {
                    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (intent.getAction())) {
                        NetworkInfo netInfo = intent.getParcelableExtra (WifiManager.EXTRA_NETWORK_INFO);
                        if (ConnectivityManager.TYPE_WIFI == netInfo.getType ()) {
                            updateWifiStatus(wifiSwitch.isChecked());
                        }
                    }
                }
            }
        };
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        wifiScanReceiver = new BroadcastReceiver(){
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context c, Intent intent){
                if(mWifiManager != null && isWantToScanWiFi) {
                    if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals (intent.getAction())) {
                        scanResults = mWifiManager.getScanResults();
                        if (!showingWiFiListDialog){
                            showWifiListDialog();
                        }
                        else{
                            wifiArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };

        wifiSwitch.setChecked(getWifiAdapterStat());
        updateWifiStatus(wifiSwitch.isChecked());



        scanWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiStartScan();
            }
        });

    }

    @Override
    protected void onCreateAtChild() {
    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private Boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private void showDialogs(int layout) {
        dialog = new Dialog(Objects.requireNonNull(PengaturanAkunActivity.this));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(wifiReceiver);
        }catch (Exception e){
        }

        try{
            unregisterReceiver(wifiScanReceiver);
        }catch (Exception e){
        }
    }

    private void updateWifiStatus(boolean isChecked) {
        if (isChecked){
            wifiLayout.setVisibility(View.VISIBLE);
            wifiSSID = "";
            if (getWifiStat()){
                WifiInfo wifiInfo;

                wifiInfo = mWifiManager.getConnectionInfo();
                wifiSSID = wifiInfo.getSSID();
            }

            if (wifiSSID.trim().equals("")){
                txtConnectedWifi.setText("Not Connected");
            }
            else{
                txtConnectedWifi.setText(wifiSSID);
            }
        }
        else{
            wifiLayout.setVisibility(View.GONE);
        }
    }

    private void connectToWifi(WifiConfiguration wifiConfig){
        showingWiFiListDialog = false;
        mWifiManager.setWifiEnabled(true);
        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
//        txtConnectedWifi.setText(wifiConfig.SSID);
        updateWifiStatus(wifiSwitch.isChecked());
    }

    private void showWifiLoginDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_login_wifi);
        dialog.setTitle("Connect to Wi-Fi");

        // get the Refferences of views
        final EditText editTextUserName = (EditText) dialog.findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword = (EditText) dialog.findViewById(R.id.editTextPasswordToLogin);
        editTextUserName.setVisibility(View.GONE);

        android.widget.Button btnSignIn = (android.widget.Button) dialog.findViewById(R.id.buttonSignIn);
        btnSignIn.setText("Connect");

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String networkPass = editTextPassword.getText().toString();
                if (isWantToConnectWPA){
                    conf.preSharedKey = "\""+ networkPass +"\"";
                }
                else if (isWantToConnectWEP){
                    conf.wepKeys[0] = "\"" + networkPass + "\"";
                }
                connectToWifi (conf);
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void wifiStartScan(){
        isWantToScanWiFi = true;
        registerReceiver(wifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    private void showWifiListDialog() {
        Collections.sort(scanResults, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level > lhs.level ? 1 : rhs.level < lhs.level ? -1 : 0;
            }
        });
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                this);
        wifiArrayAdapter = new WifiAdapter(
                this,
                android.R.layout.select_dialog_item, scanResults);

        builderSingle.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showingWiFiListDialog = false;
                        wifiStopScan();
                    }
                });
        builderSingle.setAdapter(wifiArrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String capabilities = wifiArrayAdapter.getItem(which).capabilities;
                        String strName = wifiArrayAdapter.getItem(which).SSID;

                        conf = new WifiConfiguration();
                        conf.SSID = "\"" + strName + "\"";
                        dialog.dismiss();
                        wifiStopScan();
                        showingWiFiListDialog = false;

                        if (capabilities.toUpperCase().contains("WEP")) {
                            // WEP Network
                            conf.wepTxKeyIndex = 0;
                            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            isWantToConnectWEP = true;
                            isWantToConnectWPA = false;
                            showWifiLoginDialog();
                        } else if (capabilities.toUpperCase().contains("WPA")
                                || capabilities.toUpperCase().contains("WPA2")) {
                            // WPA or WPA2 Network
                            isWantToConnectWEP = false;
                            isWantToConnectWPA = true;
                            showWifiLoginDialog();
                        } else {
                            // Open Network
                            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            connectToWifi (conf);
                        }
//                        Toast.makeText(getApplicationContext(), "Selected " + strName, Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builderSingle.create();
        dialog.show();
        showingWiFiListDialog = true;
        isWantToScanWiFi = false;
    }

    private void wifiStopScan(){
        unregisterReceiver(wifiScanReceiver);
    }


    private boolean getWifiAdapterStat(){
        if (mWifiManager.isWifiEnabled()) { // Wi-Fi adapter is ON
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    private boolean getWifiStat(){
        if (mWifiManager.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    private class WifiAdapter extends ArrayAdapter<ScanResult> {

        public WifiAdapter(Context context, int resource, List<ScanResult> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.wifi_item, parent, false);
            }
            ScanResult result = getItem(position);
            android.widget.TextView tvWifiName =  ((android.widget.TextView) convertView.findViewById(R.id.wifi_name));
            tvWifiName.setText(formatSSDI(result));
            ((ImageView) convertView.findViewById(R.id.wifi_img)).setImageLevel(getNormalizedLevel(result));
            return convertView;
        }

        private int getNormalizedLevel(ScanResult r) {
            int level = WifiManager.calculateSignalLevel(r.level,
                    5);
            Log.e(getClass().getSimpleName(), "level " + level);
            return level;
        }

        private String formatSSDI(ScanResult r) {
            if (r == null || r.SSID == null || "".equalsIgnoreCase(r.SSID.trim())) {
                return "no data";
            }
            return r.SSID.replace("\"", "");
        }
    }
}
