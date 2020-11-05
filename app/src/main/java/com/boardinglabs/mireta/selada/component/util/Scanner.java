package com.boardinglabs.mireta.selada.component.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by mrizkidharmawan on 03/09/2019.
 * Organization: BLTeam
 */

public class Scanner extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private static final int REQUEST_CODE = 1996;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(scannerView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        fetchData(result.getText());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    private void fetchData(String data) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("scan_data", data);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
