package com.boardinglabs.mireta.selada.component.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.boardinglabs.mireta.selada.component.network.entities.Brand;
import com.boardinglabs.mireta.selada.component.network.entities.Business;
import com.boardinglabs.mireta.selada.component.network.entities.Items.Category;
import com.boardinglabs.mireta.selada.component.network.entities.Merchant;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.orhanobut.hawk.Hawk;
import com.boardinglabs.mireta.selada.component.network.gson.GAgent;
import com.boardinglabs.mireta.selada.component.network.gson.GTopup;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 10/9/17.
 */

public class PreferenceManager {

    private static final String SESSION_TOKEN = "sessionToken";
    private static final String SESSION_TOKEN_MIRETA = "sessionTokenMireta";
    private static final String SESSION_TOKEN_ARDI = "sessionTokenArdi";
    private static final String USER_ID_ARDI = "userIdArdi";
    private static final String IS_LOGIN = "isLogin";
    private static final String IS_ARDI = "isARDI";
    private static final String USER_LOGIN = "userLogin";
    private static final String AGENT = "agent";
    private static final String TOPUP = "topup";
    private static final String REFFERAL_ID = "refferalId";
    private static final String IS_AKUPAY = "isAkupay";
    private static final String QR_RESPONSE = "qrResponse";
    private static final String PARKING_ID = "parkingId";
    private static final String IMEI = "imei";
    private static final String AVATAR = "avatar";
    private static final String SAVED_TOKEN = "tokenfirebase";
    private static final String KEY_ACCESS = "token";
    private static final String FB_TOKEN = "firebasetoken";
    private static final String PASS_VOID = "passVoid";
    private static final String BITMAP_STRING = "bitmapString";
    private static final String MEMBER_ID = "memberId";
    private static final String MERCHANT_ID = "merchantId";
    private static final String TID = "tid";
    private static final String MID = "mid";
    private static final String MerchantName = "MerchantName";
    private static final String MerchantAddress = "MerchantAddress";
    private static final String IP = "IP";
    private static final String SELADA_USER_ID = "seladaUserId";
    private static final String IS_SELADA_POS = "isSeladaPos";


    private static Bitmap largeIcon;
    private static final Bitmap BITMAP_HEADER = null;


    private static final String USER = "user";
    private static final String BRAND = "brand";
    private static final String STOCK_LOCATION = "stock_location";
    private static final String BUSINESS = "business";
    private static final String MERCHANT = "merchant";

    private static final String BOOTH_ID = "booth_id";
    private static final String MASTER_KEY = "master_key";
    private static final String LAY_ID = "lay_id";

    private static Context ctx;
    private static PreferenceManager mInstance;

    public PreferenceManager(Context context) {
//        Hawk.init(context)
//                .setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST)
//                .setStorage(HawkBuilder.newSharedPrefStorage(context))
//                .setPassword("P@ssw0rd123")
//                .build();
        Hawk.init(context).build();
    }
    public static synchronized PreferenceManager getInstance(Context context){
        if (mInstance == null)
            mInstance = new PreferenceManager(context);
        return mInstance;
    }


    public static Bitmap getBitmapHeader() {
        return Hawk.get(BITMAP_STRING, null);
    }


    public static void setBitmapHeader(Bitmap bitmapHeader) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapHeader.compress(Bitmap.CompressFormat.PNG, 90, stream);
        Hawk.put(BITMAP_STRING, bitmapHeader);
    }



    public static String getSessionToken() {
        return Hawk.get(SESSION_TOKEN, "");
    }


    public static void setSessionToken(String token) {
        Hawk.put(SESSION_TOKEN, token);
    }

    public static String getMemberId() {
        return Hawk.get(MEMBER_ID, "");
    }


    public static void setMemberId(String member_id) {
        Hawk.put(MEMBER_ID, member_id);
    }

    public static String getSessionTokenMireta() {
        return Hawk.get(SESSION_TOKEN_MIRETA, "");
    }

    public static void setSessionTokenMireta(String token) {
        Hawk.put(SESSION_TOKEN_MIRETA, token);
    }

    public static String getSessionTokenArdi() {
        return Hawk.get(SESSION_TOKEN_ARDI, "");
    }

    public static void setSessionTokenArdi(String token) {
        Hawk.put(SESSION_TOKEN_ARDI, token);
    }

    public static void setPassVoid(String pass) {
        Hawk.put(PASS_VOID, pass);
    }

    public static String getPassVoid() {
        return Hawk.get(PASS_VOID, "");
    }

    public static String getUserIdArdi() {
        return Hawk.get(USER_ID_ARDI, "");
    }

    public static void setUserIdArdi(String user_id) {
        Hawk.put(USER_ID_ARDI, user_id);
    }

    public static void logIn(String token, String name, String mobile, String whitelistTopUp) {
        Hawk.put(IS_LOGIN, true);
        Hawk.put(SESSION_TOKEN, token);
        Hawk.put(USER_LOGIN, new String[]{name, mobile, whitelistTopUp});
    }

    public static void setMenu(List<Category> list){
        Hawk.put(LAY_ID, list);
    }

    public static List<Category> getMenu(){
       return Hawk.get(LAY_ID, null);
    }

    public static void clearMenu(){
        Hawk.delete(LAY_ID);
    }

    public static void setAvatar(String avatarString) {
        Hawk.put(AVATAR,avatarString);
    }

    public static String getAvatar() {
        return Hawk.get(AVATAR);
    }

    public static void logOut() {
        //Hawk.put(USER_LOGIN, null);
        Hawk.put(IS_LOGIN, false);
        Hawk.put(IS_ARDI, false);
        Hawk.put(SESSION_TOKEN, "");
        Hawk.put(SESSION_TOKEN_ARDI, "");
        Hawk.put(AGENT, null);
        Hawk.deleteAll();
    }

    public static String[] getUserInfo() {
        return Hawk.get(USER_LOGIN);
    }

    public static Boolean isLogin() {
        return Hawk.get(IS_LOGIN, false);
    }

    public static Boolean isArdi() {
        return Hawk.get(IS_ARDI, false);
    }

    public static void setAgent(GAgent agent) {
        Hawk.put(AGENT, agent);
    }

    public static GAgent getAgent() {
        return Hawk.get(AGENT);
    }

    public static void setTopup(GTopup topup) {
        Hawk.put(TOPUP, topup);
    }

    public static GTopup getTopup() {
        return Hawk.get(TOPUP);
    }

    public static void setRefferalId(String id) {
        Hawk.put(REFFERAL_ID, id);
    }

    public static String getRefferalId() {
        return Hawk.get(REFFERAL_ID,"");
    }

    public static void setStatusAkupay(boolean isAkupay) {
        Hawk.put(IS_AKUPAY, isAkupay);
    }

    public static boolean getStatusAkupay() {
        return Hawk.get(IS_AKUPAY, false);
    }

    public static void setParkingId(String transactionId) {
        Hawk.put(PARKING_ID, transactionId);
    }

    public static String getParkingId() {
        return Hawk.get(PARKING_ID);
    }

    public static void emptyParkingId() {
        setParkingId("");
    }

    //public static boolean saveToken(String token){
    //    SharedPreferences sharedPreferences = ctx.getSharedPreferences(SAVED_TOKEN, Context.MODE_PRIVATE);
    //    SharedPreferences.Editor editor = sharedPreferences.edit();
    //    editor.putString(KEY_ACCESS, token);
    //    editor.apply();
    //    return true;
    //}

    //public String getToken(){
    //    SharedPreferences sharedPreferences = ctx.getSharedPreferences(SAVED_TOKEN, Context.MODE_PRIVATE);
    //    return sharedPreferences.getString(KEY_ACCESS.null)
    //}

    public static void setSavedToken(String token){
        Hawk.put(FB_TOKEN,token);
    }
    public static String getSavedToken() {
        return Hawk.get(FB_TOKEN);
    }
	public static String getImei() {
        return Hawk.get(IMEI);
    }

    public static void setImei(String imei) {
        Hawk.put(IMEI, imei);
    }

    public static void setMerchantId(String merchantId) {
        Hawk.put(MERCHANT_ID, merchantId);
    }
    public static String getMerchantId() {
        return Hawk.get(MERCHANT_ID);
    }

    public static void setTID(String tid) {
        Hawk.put(TID, tid);
    }
    public static String getTID() {
        return Hawk.get(TID);
    }

    public static void setMID(String mid) {
        Hawk.put(MID, mid);
    }
    public static String getMID() {
        return Hawk.get(MID);
    }

    public static void setIsSeladaPos(Boolean isSeladaPos) {
        Hawk.put(IS_SELADA_POS, isSeladaPos);
    }
    public static Boolean getIsSeladaPos() {
        return Hawk.get(IS_SELADA_POS);
    }

    public static void setMerchantName(String merchantName) {
        Hawk.put(MerchantName, merchantName);
    }
    public static String getMerchantName() {
        return Hawk.get(MerchantName);
    }

    public static void setMerchantAddress(String merchantAddress) {
        Hawk.put(MerchantAddress, merchantAddress);
    }
    public static String getMerchantAddress() {
        return Hawk.get(MerchantAddress);
    }

    public static void setIP(String ip) {
        Hawk.put(IP, ip);
    }

    public static String getIP() {
        return Hawk.get(IP);
    }

    public static void saveLogIn(String token, String id, String first_name, String username, String member_id) {
        Hawk.put(IS_LOGIN, true);
        Hawk.put(SESSION_TOKEN, token);
        Hawk.put(MEMBER_ID, member_id);
        Hawk.put(SELADA_USER_ID, id);
        Hawk.put(USER_LOGIN, new String[]{id, username, first_name});
    }

    public static String getSeladaUserId() {
        return Hawk.get(SELADA_USER_ID);
    }

    public static void setBoothId(String boothId){
        Hawk.put(BOOTH_ID, boothId);
    }

    public static String getBoothId(){
        return Hawk.get(BOOTH_ID, "");
    }

    public static void setMasterKey(String masterKey){
        Hawk.put(MASTER_KEY, masterKey);
    }

    public static String getMasterKey(){
        return Hawk.get(MASTER_KEY, "");
    }

    public static void saveLogInArdi(){
        Hawk.put(IS_ARDI, true);
    }

    public static void saveUser(User user) {
        Hawk.put(USER, user);
    }

    public static User getUser() {
        return Hawk.get(USER);
    }

    public static void saveMerchant(Merchant merchant) {
        Hawk.put(MERCHANT, merchant);
    }

    public static Merchant getMerchant() {
        return Hawk.get(MERCHANT);
    }

    public static void saveBusiness(Business business) {
        Hawk.put(BUSINESS, business);
    }

    public static Business getBusiness() {
        return Hawk.get(BUSINESS);
    }

    public static void saveBrand(Brand brand) {
        Hawk.put(BRAND, brand);
    }

    public static Brand getBrand() {
        return Hawk.get(BRAND);
    }


    public static void saveStockLocation(StockLocation stockLocation) {
        Hawk.put(STOCK_LOCATION, stockLocation);
    }

    public static StockLocation getStockLocation() {
        return Hawk.get(STOCK_LOCATION);
    }
}
