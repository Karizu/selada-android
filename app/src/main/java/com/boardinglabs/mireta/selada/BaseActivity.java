package com.boardinglabs.mireta.selada;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.component.network.entities.Business;
import com.boardinglabs.mireta.selada.component.network.entities.Merchant;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.view.RxView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.boardinglabs.mireta.selada.component.dialog.CustomProgressBar;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.ResponeError;
import com.boardinglabs.mireta.selada.component.network.gson.GAgent;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.auth.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.boardinglabs.mireta.selada.component.util.Utils.IV;
import static com.boardinglabs.mireta.selada.component.util.Utils.bytesToHex;
import static com.boardinglabs.mireta.selada.component.util.Utils.encodeToString;
import static com.boardinglabs.mireta.selada.component.util.Utils.decodeToBytes;
import static com.boardinglabs.mireta.selada.component.util.Utils.decrypt;
import static com.boardinglabs.mireta.selada.component.util.Utils.encrypt;
import static com.boardinglabs.mireta.selada.component.util.Utils.hexStringToByteArray;
import static com.boardinglabs.mireta.selada.component.util.Utils.stringToBytes;


/**
 * Created by Dhimas on 9/19/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected  static  int CHANGE_PASSCODE = 11;
    protected Toolbar toolbar;
    protected ImageView backBtn;
    protected ImageView closeBtn;
    protected ImageView menuBtn;
    protected TextView toolbarTitle;
    protected LinearLayout header;
    protected ImageView imageHeader;
    protected SlidingMenu mSlidingMenu;
    protected FrameLayout normalToolbarWrapper;
    protected TextView nameTV;
    protected TextView onlineStatusTV;
    protected Button submitButton;

    public static CustomProgressBar progressBar = new CustomProgressBar();
    protected TextView changePassode;
    public Context context;

    protected User loginUser;
    protected StockLocation loginStockLocation;
    protected Business loginBusiness;
    protected Merchant merchant;
    protected Boolean isSeladaPos = false;
    protected byte[] keyGen;
    protected byte[] rToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        loginUser = PreferenceManager.getUser();
        loginStockLocation = PreferenceManager.getStockLocation();
        loginBusiness = PreferenceManager.getBusiness();
        merchant = PreferenceManager.getMerchant();

        try {
            isSeladaPos = PreferenceManager.getIsSeladaPos();
        } catch (Exception e){e.printStackTrace();}

        onCreateAtChild();
        setContentView(getLayoutResourceId());
        initComponent();
        setEventBackButton();
        setContentViewOnChild();
        launchKey();
    }

    private Context getContext() {
        return context;
    }

    protected abstract int getLayoutResourceId();

    protected abstract void setContentViewOnChild();

    protected abstract void onCreateAtChild();

    protected abstract void onBackBtnPressed();

    protected abstract void onSubmitBtnPressed();

    private void initComponent() {
        header = (LinearLayout) findViewById(R.id.header);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backBtn = (ImageView) findViewById(R.id.hometoolbar_imgBtnBack);
        closeBtn = (ImageView) findViewById(R.id.hometoolbar_imgBtnClose);
        menuBtn = (ImageView) findViewById(R.id.hometoolbar_imgBtnMenu);
        toolbarTitle = (TextView) findViewById(R.id.hometoolbar_title);
        imageHeader = (ImageView) findViewById(R.id.hometoolbar_logo);
        submitButton = (Button) findViewById(R.id.submit_button);
        normalToolbarWrapper = (FrameLayout) findViewById(R.id.normal_toolbar_layout);
        nameTV = (TextView) findViewById(R.id.chat_name);
        onlineStatusTV = (TextView) findViewById(R.id.chat_status);

        RxView.clicks(submitButton).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onSubmitBtnPressed();
            }
        });

        RxView.clicks(menuBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                menuButtonPressed();
            }
        });

    }

    protected void refreshChat(){

    }

    protected void menuButtonPressed(){

    }

    protected void setSlidingMenu(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mSlidingMenu = new SlidingMenu(context);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mSlidingMenu.setShadowWidthRes(R.dimen.d2);
        mSlidingMenu.setFadeDegree(0.0f);
        mSlidingMenu.setBehindWidth(metrics.widthPixels / 3 + (metrics.widthPixels / 2));
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.hamburger);
        mSlidingMenu.setShadowDrawable(R.drawable.sidemenu_shadow);

        ImageView close = (ImageView) mSlidingMenu.findViewById(R.id.close_menu);
        TextView logout = (TextView) mSlidingMenu.findViewById(R.id.logout);
        TextView menu = (TextView)  mSlidingMenu.findViewById(R.id.menu);
        TextView editProfile = (TextView)  mSlidingMenu.findViewById(R.id.edit_profile);
        TextView premiumMember = (TextView)  mSlidingMenu.findViewById(R.id.premium_member);
        TextView sendAmount = (TextView)  mSlidingMenu.findViewById(R.id.send_amount);
        TextView transactionHistory = (TextView)  mSlidingMenu.findViewById(R.id.transaction_history);
        TextView customerService = (TextView)  mSlidingMenu.findViewById(R.id.customer_service);
        TextView saveCard = (TextView)  mSlidingMenu.findViewById(R.id.save_card);
        changePassode = (TextView) mSlidingMenu.findViewById(R.id.ubah_pin);

        RxView.clicks(close).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mSlidingMenu.toggle();
            }
        });


        RxView.clicks(logout).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                logoutAction(false);
            }
        });

    }

    protected void setToolbarTitle(String title) {
        backBtn.setVisibility(View.VISIBLE);
        closeBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(R.color.mireta_blue));
        imageHeader.setVisibility(View.GONE);
        header.setBackgroundColor(getResources().getColor(R.color.very_light_pink));
    }

    protected void setSeladaToolbarTitle(String title) {
        backBtn.setVisibility(View.VISIBLE);
        closeBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(R.color.primaryAccentBlue));
        imageHeader.setVisibility(View.GONE);
    }

    protected void setToolbarTitleWithSubmitButton(String title) {
        backBtn.setVisibility(View.VISIBLE);
        closeBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(R.color.white));
        imageHeader.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        header.setBackgroundColor(getResources().getColor(R.color.mireta_blue));
    }

    protected void setToolbarTitleWithCloseButton(String title) {
        backBtn.setVisibility(View.GONE);
        closeBtn.setVisibility(View.VISIBLE);
        menuBtn.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.VISIBLE);
        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(getResources().getColor(R.color.white));
        imageHeader.setVisibility(View.GONE);
        header.setBackgroundColor(getResources().getColor(R.color.mireta_blue));
    }


    protected void setToolbarLogo() {
        backBtn.setVisibility(View.GONE);
        closeBtn.setVisibility(View.GONE);
        toolbarTitle.setVisibility(View.GONE);
        imageHeader.setVisibility(View.VISIBLE);
        header.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void setEventBackButton() {
        RxView.clicks(backBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onBackBtnPressed();
            }
        });
        RxView.clicks(closeBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onBackBtnPressed();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginUser = PreferenceManager.getUser();
        merchant = PreferenceManager.getMerchant();
        loginStockLocation = PreferenceManager.getStockLocation();
        loginBusiness = PreferenceManager.getBusiness();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void logoutAction(final boolean isFromBackground) {
        if (!isFromBackground) {
            progressBar.show(this, "", false, null);
        }
        logout().subscribe(new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (!isFromBackground) {
                    progressBar.getDialog().dismiss();
                }
                showError(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                if (!isFromBackground) {
                    progressBar.getDialog().dismiss();
                }
                if (PreferenceManager.isLogin()) {
                    goToLoginPage();
                }
            }
        });
    }

    private void showError(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    public void goToLoginPage() {
            PreferenceManager.logOut();
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

    }

    protected void goToLoginPage1(Context context) {
        PreferenceManager.logOut();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void launchKey(){
        if (merchant!=null){
            String serialNumber = merchant.getTerminal().getSerialNumber();
            String imei = merchant.getTerminal().getImei();
            String iccid = merchant.getTerminal().getIccid();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("ssmmHHyyyyMMdd");
            String timeDate = s.format(new Date());
            String primToken = imei+iccid+timeDate;
            String secToken = serialNumber+timeDate+"SL";

            try {
                //KEY FOR DECRYPT RTOKEN
                //KEY SEND TO SERVER
                keyGen = encrypt(stringToBytes(secToken), stringToBytes(serialNumber), IV());
//                Log.d("KEYGENSEC", bytesToHex(keyGen));
//                Log.d("KEYGENSEC", encodeToString(keyGen));
                String encKeyGen = bytesToHex(keyGen);
//                String encKeyGen = encodeToString(keyGen);
                byte[] decKeyGen = hexStringToByteArray(encKeyGen);
//                byte[] decKeyGen = decodeToBytes(encKeyGen);
//                Log.d("HEX SN", bytesToHex(stringToBytes(serialNumber)));
//                Log.d("DECRYPT KEYGENSEC", decrypt(decKeyGen, stringToBytes(serialNumber), IV()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                //RTOKEN ENCRYPTED
                //SEND TO SERVER
                rToken = encrypt(stringToBytes(primToken), stringToBytes(secToken), IV());
                Log.d("KEYGENPRIM", encodeToString(rToken));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    Observable<JsonObject> logout(){
        GAgent agent = PreferenceManager.getAgent();
        String userId = "";
        if (agent != null)
        userId = agent.id;
        return NetworkManager.getInstance().logout(userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());
    }
}
