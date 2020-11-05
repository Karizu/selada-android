package com.boardinglabs.mireta.selada.component.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    private String fileName;

    public DownloadTask(Context context, ProgressDialog mProgressDialog, String fileName) {
        this.context = context;
        this.mProgressDialog = mProgressDialog;
        this.fileName = fileName;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream(); //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Download/" + fileName+".apk");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null) {
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            Log.d("DOWNLOAD ERROR", result);
        } else {

            try{
                File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName+".apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider",file);
                intent.setDataAndType(data, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);

            }
            catch(Exception e){
                e.printStackTrace();

                try {
                    if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName+".apk");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName+".apk");
                        Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        intent.setDataAndType(data, "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(context, "Gagal update aplikasi, silahkan hubungi Customer Service", Toast.LENGTH_LONG).show();
                }


            }
        }

    }
}
