package com.boardinglabs.mireta.selada.component.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.entities.Request.CreateSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.entities.Request.GetSeladaService;
import com.boardinglabs.mireta.selada.component.network.entities.Request.GetServices;
import com.boardinglabs.mireta.selada.component.network.entities.Request.SpiCMDInquiry;
import com.boardinglabs.mireta.selada.component.network.entities.Request.SpiCMDInquiryBPJS;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.modul.bjb.common.CommonConfig;
import com.cloudpos.DeviceException;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.gson.Gson;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.guava.base.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import okio.ByteString;

import static com.boardinglabs.mireta.selada.component.util.Utils.stringToBytes;

/**
 * Created by imrankst1221@gmail.com
 */

public class Utils {

    private DataBaseHelper helperDb;
    private String str;
    private Format format;
    private Handler handler = new Handler();
    private Runnable myRunnable = () -> {
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    };

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidMobile(String phone) {
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }

    // UNICODE 0x23 = #
    public static final byte[] UNICODE_TEXT = new byte[]{0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean openApp(Context context, String packageName, HashMap<String, String> dataMap) {

        final PackageManager manager = context.getPackageManager();
        final Intent appLauncherIntent = new Intent(Intent.ACTION_MAIN);
        appLauncherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(appLauncherIntent, 0);
        if ((null != resolveInfos) && (!resolveInfos.isEmpty())) {
            for (ResolveInfo rInfo : resolveInfos) {
                String className = rInfo.activityInfo.name.trim();
                String targetPackageName = rInfo.activityInfo.packageName.trim();
                Log.d("AppsLauncher", "Class Name = " + className + " Target Package Name = " + targetPackageName + " Package Name = " + packageName + " Intent = " + dataMap.get("menu"));
                if (packageName.trim().equals(targetPackageName)) {
                    Intent intent = new Intent();
                    intent.putExtra("menu", dataMap.get("menu"));
                    try {
                        intent.putExtra("serviceId", dataMap.get("serviceId"));
                        intent.putExtra("mid", dataMap.get("mid"));
                        intent.putExtra("mobileNumber", dataMap.get("mobileNumber"));
                        intent.putExtra("nominal", dataMap.get("nominal"));
                        intent.putExtra("amount", dataMap.get("amount"));
                        intent.putExtra("margin", dataMap.get("margin"));
                    } catch (Exception e) {
                    }

                    try {
                        intent.putExtra("stan", dataMap.get("stan"));
                    } catch (Exception e) {
                    }

                    try {
                        intent.putExtra("tid", dataMap.get("tid"));
                        intent.putExtra("mids", dataMap.get("mids"));
                        intent.putExtra("mn", dataMap.get("mn"));
                        intent.putExtra("ma", dataMap.get("ma"));
//                        intent.putExtra("ip", dataMap.get("ip"));
                    } catch (Exception e) {
                    }

                    try {
                        intent.putExtra("storeName", dataMap.get("storeName"));
                    } catch (Exception e) {
                    }

                    try {
                        intent.putExtra("json", dataMap.get("json"));
                    } catch (Exception e) {
                    }

                    intent.putExtra("is_from_selada", "true");

                    intent.setClassName(targetPackageName, className);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Set<Map.Entry<String, String>> set = dataMap.entrySet();

                    for (Map.Entry<String, String> data : set) {
                        Log.d(data.getKey() + ": ", "");
                        Log.d("", data.getValue());
                        intent.putExtra(data.getKey(), data.getValue());
                    }

                    context.startActivity(intent);
                    Log.d("AppsLauncher", "Launching Package '" + packageName + "' with Activity '" + className + "'");
                    return true;
                }
            }
        }
        return false;
    }

    public static String encodeJson(String requestName, String key, String serialNumber, String string1, String string2, String string3,
                                    String string4, String string5, String string6) {
        GetServices services = new GetServices();
        GetSeladaService seladaService = new GetSeladaService();
        SpiCMDInquiry spiCMDInquiry = new SpiCMDInquiry();
        SpiCMDInquiryBPJS spiCMDInquiryBPJS = new SpiCMDInquiryBPJS();
        CreateSeladaTransaction createSeladaTransaction = new CreateSeladaTransaction();

        Gson gson = new Gson();
        String json = "";
        if (requestName.equals("getService")) {
            services.setType(string1);
            services.setAmount(string2);
            services.setNo(string3);
            services.setCat(string4);
            json = gson.toJson(services);
        } else if (requestName.equals("getSeladaService")) {
            seladaService.setCategory_id(string1);
            seladaService.setProvider_id(string2);
            json = gson.toJson(seladaService);
        } else if (requestName.equals("spiCMDInquiry")) {
            spiCMDInquiry.setCmd(string1);
            spiCMDInquiry.setNop(string2);
            spiCMDInquiry.setVoc(string3);
            json = gson.toJson(spiCMDInquiry);
        } else if (requestName.equals("spiCMDInquiryBPJS")) {
            spiCMDInquiryBPJS.setCmd(string1);
            spiCMDInquiryBPJS.setNop(string2);
            spiCMDInquiryBPJS.setBln(string3);
            json = gson.toJson(spiCMDInquiryBPJS);
        } else if (requestName.equals("createSeladaTrx")) {
            createSeladaTransaction.setService_id(string1);
            createSeladaTransaction.setMerchant_id(string2);
            createSeladaTransaction.setMerchant_no(string3);
            createSeladaTransaction.setPrice(string4);
            createSeladaTransaction.setVendor_price(string5);
            createSeladaTransaction.setNote("");
            createSeladaTransaction.setStan(string6);
            json = gson.toJson(createSeladaTransaction);
        }

//        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        byte[] decKeyGen = decodeToBytes(key);
        String strKeyGen = decrypt(decKeyGen, stringToBytes(serialNumber), decKeyGen);
        byte[] finalKeyGen = stringToBytes(Objects.requireNonNull(strKeyGen));
        byte[] data = null;
        try {
            data = encrypt(stringToBytes(json), finalKeyGen, finalKeyGen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static byte[] stringToBytes(String plaintext) {
        return plaintext.getBytes(StandardCharsets.UTF_8);
    }

    public static String encodeToString(byte[] bytes) {
//        String credentials = new String(plaintext, StandardCharsets.UTF_8);
//        String credentials =  Base64.encodeToString(plaintext, Base64.DEFAULT);
//        return Base64.encodeToString(plaintext, Base64.NO_WRAP);

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static byte[] decodeToBytes(String plaintext) {
//        String credentials = new String(plaintext, StandardCharsets.UTF_8);
//        String credentials =  Base64.encodeToString(plaintext, Base64.DEFAULT);
        return Base64.decode(plaintext, Base64.DEFAULT);
    }

    public static byte[] IV() {
        byte[] IV = new byte[16];
        SecureRandom random;
        random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }

    public static String encoderfun(byte[] decval) {
        return Base64.encodeToString(decval, Base64.DEFAULT);
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plaintext);
    }

    public static String decrypt(byte[] cipherText, byte[] key, byte[] IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
//            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getDiffTime(String sDate1, String sDate2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(sDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(sDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = (date2 != null ? date2.getTime() : 0) - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);
        int minutes = min < 0 ? -min : min;
        Log.i("======= min", " :: " + minutes);
        return min < 0 ? -min : min;
    }

    public static void saveReversalLog(String serviceId, String msgId, Context context) {
        //save per request
        DataBaseHelper helperDb = new DataBaseHelper(context);
        SQLiteDatabase clientDB = null;
        String logText = "Save log";
        try {
            helperDb.openDataBase();
            clientDB = helperDb.getActiveDatabase();
            int logid = 0;
            logText += ", try get log seq";
            String qry = "select max(log_id) from edc_log ";
            Cursor mxid = clientDB.rawQuery(qry, null);
            if (mxid.moveToFirst()) {
                if (mxid != null) {
                    logid = mxid.getInt(0) + 1;
                } else {
                    logid = 1;
                }
            } else {
                logid = 1;
            }
            logText += ", found = " + logid;
            String newLog = "insert or replace into edc_log("
                    + "log_id, service_id, messageid, rqtime, settled, reversed) values "
                    + "(" + String.valueOf(logid)
                    + ",'" + serviceId + "'"
                    + ",'" + msgId
                    + "', datetime('now','local'), 0, 't')";
            logText += ", exec " + newLog;
            clientDB.execSQL(newLog);
            logText += ", data saved";
            if (clientDB != null) {
                clientDB.close();
            }
        } catch (Exception ex) {
            if (clientDB != null) {
                clientDB.close();
            }
            Log.e("TX", "DB error");
            ex.printStackTrace();
            logText += ", data not saved";
        }
        if (logText != null) {
            Log.i("VLG", logText);
        }
//        Toast.makeText(context, logText, Toast.LENGTH_LONG).show();
    }

    public static Boolean isTransactionReversed(Context context, String msgId) {
        DataBaseHelper helperDb = new DataBaseHelper(context);
        boolean isReversed = false;
        SQLiteDatabase clientDB = null;
        String logText = "Check isReversed";
        try {
            helperDb.openDataBase();
            clientDB = helperDb.getActiveDatabase();
            String qLog = "select * from edc_log "
                    + "where messageid = " + "'" + msgId + "'";
            Cursor cLog = clientDB.rawQuery(qLog, null);
            if (cLog.moveToFirst()) {
                String dttx = "";
                String reversed = cLog.getString(cLog.getColumnIndex("reversed"));
                Log.i("reversed", reversed);
                if (reversed.equals("t")) {
                    isReversed = true;
                }
                while (cLog.moveToNext()) {
                    dttx = "";
                }
            }
        } catch (Exception ex) {
            Log.i("TX", "DB error");
            ex.printStackTrace();
            logText += ", excptn";
        }
        if (logText != null) {
            Log.i("VLG", logText);
        }
//        Toast.makeText(context, logText, Toast.LENGTH_LONG).show();
        return isReversed;
    }

    @SuppressLint("HardwareIds")
    public static String getMsgId(Context context, String date) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return (telephonyManager != null ? telephonyManager.getDeviceId() : null) + date;
    }

    private void closePrinter(PrinterDevice printerDevice, Context context) {
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPrintLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine) {
        if (value != null) {
            int lineCharCount = 32;
            int labelCharCount = label.length();
            int valueCharCount = value.length();

            if (labelCharCount + valueCharCount > (lineCharCount - 2)) {
                usingNextLine = true;
            }

            String output = "";
            if (!usingNextLine) {
                output += label;
                for (int i = labelCharCount; i < lineCharCount - valueCharCount; i++) {
                    output += " ";
                }
                output += value;
            } else {
                output = label;
                for (int i = labelCharCount; i < lineCharCount; i++) {
                    output += " ";
                }
                output += "\n";
                for (int i = 0; i < valueCharCount; i++) {
                    output += " ";
                }
                output += value;
            }
            if (endWithNewLine) {
                output += "\n";
            }
            return output;
        }
        return "";

    }

    public static List<Integer> listMenuId(Activity activity){
        View inflatedView = activity.getLayoutInflater().inflate(R.layout.activity_home_biller, null);
        List<Integer> list = new ArrayList<>();
        list.add(R.id.seq_0);
        list.add(R.id.seq_1);
        list.add(R.id.seq_2);
        list.add(R.id.seq_3);
        list.add(R.id.seq_4);
        list.add(R.id.seq_5);
        list.add(R.id.seq_6);
        list.add(R.id.seq_7);
        list.add(R.id.seq_8);
        list.add(R.id.seq_9);
        list.add(R.id.seq_10);
        list.add(R.id.seq_11);
        list.add(R.id.seq_12);
        list.add(R.id.seq_13);
        list.add(R.id.seq_14);
        list.add(R.id.seq_15);
        list.add(R.id.seq_16);
        list.add(R.id.seq_17);
        list.add(R.id.seq_18);
        list.add(R.id.seq_19);

//        if (PreferenceManager.getLayIdList() == null){
//            PreferenceManager.setLayIdList(list);
//        }

//        return PreferenceManager.getLayIdList()!=null ? PreferenceManager.getLayIdList() : list;
        return list;
    }

    public static List<Integer> listIconId(Activity activity){
        View inflatedView = activity.getLayoutInflater().inflate(R.layout.activity_home_biller, null);
        List<Integer> list = new ArrayList<>();
        list.add(R.id.menu_icon_1);
        list.add(R.id.menu_icon_2);
        list.add(R.id.menu_icon_3);
        list.add(R.id.menu_icon_4);
        list.add(R.id.menu_icon_5);
        list.add(R.id.menu_icon_6);
        list.add(R.id.menu_icon_7);
        list.add(R.id.menu_icon_8);
        list.add(R.id.menu_icon_9);
        list.add(R.id.menu_icon_10);
        list.add(R.id.menu_icon_11);
        list.add(R.id.menu_icon_12);
        list.add(R.id.menu_icon_13);
        list.add(R.id.menu_icon_14);
        list.add(R.id.menu_icon_15);
        list.add(R.id.menu_icon_16);
        list.add(R.id.menu_icon_17);
        list.add(R.id.menu_icon_18);
        list.add(R.id.menu_icon_19);
        list.add(R.id.menu_icon_20);
        return list;
    }

    public static List<Integer> listTitleId(Activity activity){
        View inflatedView = activity.getLayoutInflater().inflate(R.layout.activity_home_biller, null);
        List<Integer> list = new ArrayList<>();
        list.add(R.id.menu_title_1);
        list.add(R.id.menu_title_2);
        list.add(R.id.menu_title_3);
        list.add(R.id.menu_title_4);
        list.add(R.id.menu_title_5);
        list.add(R.id.menu_title_6);
        list.add(R.id.menu_title_7);
        list.add(R.id.menu_title_8);
        list.add(R.id.menu_title_9);
        list.add(R.id.menu_title_10);
        list.add(R.id.menu_title_11);
        list.add(R.id.menu_title_12);
        list.add(R.id.menu_title_13);
        list.add(R.id.menu_title_14);
        list.add(R.id.menu_title_15);
        list.add(R.id.menu_title_16);
        list.add(R.id.menu_title_17);
        list.add(R.id.menu_title_18);
        list.add(R.id.menu_title_19);
        list.add(R.id.menu_title_20);
        return list;
    }

    public static HashMap<String, Integer> icons(){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("PLN Pascabayar", R.drawable.ic_pln_pascabayar_lightning);
        map.put("PLN Prabayar", R.drawable.ic_pln_prabayar_lightning);
        map.put("Televisi Kabel", R.drawable.tv_berlangganan);
        map.put("Multifinance", R.drawable.multi_finance);
        map.put("Pulsa Pasca", R.drawable.pulsa_pascabayar);
        map.put("Pulsa Data", R.drawable.ic_pulsa);
        map.put("PDAM", R.drawable.pdam);
        map.put("BPJS Kesehatan", R.drawable.icon_bpjs_kesehatan);
        map.put("Voucher Game", R.drawable.icon_pasca);
        map.put("E-Wallet", R.drawable.icon_pulsa);
        map.put("Pulsa", R.drawable.pulsa_reguler);
        map.put("Telkom", R.drawable.speedy);
        map.put("Default", R.drawable.ic_transaksi_baru);
        return map;
    }

    public static String formatString(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
