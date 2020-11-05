package com.boardinglabs.mireta.selada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            setMobileDataEnabled(getApplicationContext(), true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class<?> conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
//        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        final Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
        for (final Method method : methods) {
            if (method.toGenericString().contains("set")) {

            }
        }
//        setMobileDataEnabledMethod.setAccessible(true);

//        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Context context = getApplicationContext();
                    boolean isEnabled = Settings.System.getInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 0) == 1;

                    Toast.makeText(context, isEnabled ? "true": "false", Toast.LENGTH_LONG);

                    Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

                    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    intent.putExtra("state", !isEnabled);
                    sendBroadcast(intent);

                    IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");

                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            
                        }
                    };

                    context.registerReceiver(receiver, intentFilter);
                } else {

                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
