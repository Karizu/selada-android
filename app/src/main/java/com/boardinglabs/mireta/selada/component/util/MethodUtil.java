package com.boardinglabs.mireta.selada.component.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BuildConfig;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.selada.modul.selada.launcher.SeladaLaucherActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Dhimas on 9/29/17.
 */

public class MethodUtil extends Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MethodUtil.context = getApplicationContext();
    }

    public static void showCustomToast(Activity activityContext, String message, int image) {
        if (!TextUtils.isEmpty(message) && message.contains("AKUPAY") && !PreferenceManager.getStatusAkupay()) {
            message = message.replace("AKUPAY", "DOOMO");
        }
        if (activityContext != null && !TextUtils.isEmpty(message)) {
            LayoutInflater inflater = activityContext.getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) activityContext.findViewById(R.id.toast_layout_root));
            RobotoBoldTextView messageTextView = (RobotoBoldTextView) layout.findViewById(R.id.textview_message);
            ImageView imageView = (ImageView) layout.findViewById(R.id.imageview_icon);
            messageTextView.setText(message);
            if (image != 0) imageView.setImageResource(image);
            else imageView.setVisibility(View.GONE);
            Toast toast = new Toast(activityContext);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
//            if (message.equalsIgnoreCase(Constant.EXPIRED_SESSION) || message.equalsIgnoreCase(Constant.EXPIRED_ACCESS_TOKEN)) {
//                BaseActivity base = new BaseActivity() {
//                    @Override
//                    protected int getLayoutResourceId() {
//                        return 0;
//                    }
//
//                    @Override
//                    protected void setContentViewOnChild() {
//
//                    }
//
//                    @Override
//                    protected void onCreateAtChild() {
//
//                    }
//
//                    @Override
//                    protected void onBackBtnPressed() {
//
//                    }
//                };
//                base.goToLoginPage();
//            }
        }
    }

    public static void showDialog(Dialog dialog, Context context, int layout, String title) {
        dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(layout);
        dialog.setTitle(title);
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

    public static Dialog showDialog(Context context, int layout, String title) {
        Dialog dialog = new Dialog(Objects.requireNonNull(context));
        //set content
        dialog.setContentView(layout);
        dialog.setTitle(title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.very_light_pink)));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        return dialog;
    }

    public static String toCurrencyFormat(final String value) {

        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 3 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append(".");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static String toDateFormat(final String value) {
        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 2 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append("/");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static String strToDateFormat(final String value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("MMM yy");

        try {
            return myFormat.format(fromUser.parse(value));
        } catch (ParseException e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String formatTokenNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append("-");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String formatCardNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String[] formatDateAndTime(String dateTime) {
        String[] tempDateTime = new String[2];
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("id")).parse(dateTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID"));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH : mm : ss");
            tempDateTime[0] = dateFormat.format(date);
            tempDateTime[1] = timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempDateTime;
    }

    public static String getHMac(String inputString, String unixTime, boolean isSearchMerchant) throws Exception {
        String sha256Hash;
        if (isSearchMerchant) {
            sha256Hash = inputString;
        } else {
            sha256Hash = inputString.replace("%20", "+");
        }

        String hash2 = BuildConfig.SECRET_ID;

        for (int i = 1; i < 11; i++) {
            sha256Hash = HashGenerator.generateSHA256(sha256Hash + HashGenerator.generateMD5(String.valueOf(i)) + unixTime);
        }

        for (int i = 1; i < 11; i++) {
            hash2 = HashGenerator.generateSHA256(hash2 + HashGenerator.generateMD5(String.valueOf(i)));
        }

        String hash3 = sha256Hash;
        for (int i = 1; i < 11; i++) {
            hash3 = HashGenerator.generateSHA256(hash3 + HashGenerator.generateMD5(String.valueOf(i)) + hash2);
        }

        return hash3;
    }

    public static String formatDateCreditcard(String date) {
        String cleanDate = date.trim();
        StringBuilder result = new StringBuilder();
        for (int i =0; i < cleanDate.length() ;i++) {
            if (i % 2 == 0 && i != 0) {
                result.append("/");
            }
            result.append(cleanDate.charAt(i));
        }
        return result.toString();

    }

    public static String GetCountryZipCode(Context ctx){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl= ctx.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public static String getResponseError(String json) throws JSONException {
        JSONObject jObjError = new JSONObject(json);
        return jObjError.getString("error");
    }
}
