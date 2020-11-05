package com.boardinglabs.mireta.selada.modul.akun.rfid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.modul.ardi.registermember.RegisterMember;
import com.wizarpos.apidemo.contactlesscard.ContactlessControler;
import com.wizarpos.apidemo.printer.PrintSize;
import com.wizarpos.apidemo.smartcard.NeoSmartCardController;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TapCard extends RelativeLayout {

    public final String TAG = "BRIZZI";
    private final SimpleDateFormat DATE = new SimpleDateFormat("ddMMyy");
    private final SimpleDateFormat DATE_TOCARD = new SimpleDateFormat("yyMMdd");
    private final SimpleDateFormat TIME = new SimpleDateFormat("HHmmss");
    private final SimpleDateFormat DATE_TOCOMP = new SimpleDateFormat("dd-MM-yyyy");
    public static final String cardType = "BRIZZI CARD (FLY)";

    boolean DEBUG_LOG = true;
    boolean DEBUG_MODE = false;
    /* Setup Aktivasi Kartu Close
    boolean DEBUG_MODE = true; */
    SQLiteDatabase clientDB = null;
    List<String> mdata = new ArrayList<>();
    String tid = null;
    String mid = null;
    String stan = null;
    String svrDt = "";
    String svrTm = "";
    private int printcount = 0;
    private int printcountbutton = 0;
    private Boolean footerAdded = false;
    private android.widget.Button btnOk, btnPrint, btnNoPrint;
    private android.widget.TextView confirmationText;
    private String[] printConfirm = {
            "Print Customer Copy ?",
            "Print Bank Copy ?",
            "Print Merchant Copy ?", "",
            "Print Duplicate Copy ?", "", "", ""
    };
    private boolean printInUse = false;
    private boolean isAntiDDOSPrint = true;

    private boolean enablePrint = false;
    private JSONObject formReponse = new JSONObject();
    private List<PrintSize> printSizes = new ArrayList<>();
    private android.widget.TextView txtMessage;
    private Context context;
    private ContactlessControler cc;
//        private RFCardControler cc;
    private NeoSmartCardController smc;
    private JSONObject printData = new JSONObject();
    private String logid;
    private String nomorKartu;
    private Long maxDeduct;
    private boolean traceAdded = false;
    private String gtmStamp, uid;
    RfidActivity rfidActivity = new RfidActivity();
    RegisterMember registerMember = new RegisterMember();

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            uid = bundle.getString("uid");
            Log.d("UID", uid);
            registerMember.UID = uid;
        }
    };

    public TapCard(Context context) {
        super(context);
        this.context = context;
    }

    public TapCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public TapCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setMessage(String message) {
        txtMessage.setText(message);
    }

    public void setMessage(String message, int mode) {
        txtMessage.setGravity(mode);
        setMessage(message);
    }

    public void searchBegin() {
        boolean openDevice = smc.starting(1);
        openDevice = openDevice && cc.searchBegin();
        if (!openDevice) {
//            setMessage("Terjadi kesalahan.\n ERROR [05]");
//            setMessage("Tidak dapat melakukan transaksi\nSilahkan coba beberapa saat lagi");
            Log.e(TAG, "ERROR WHEN OPENING DEVICES");
            cc.searchEnd();
            smc.closedevice();
            searchBegin();
        }
    }

    public void searchEnd() {
        if (clientDB!=null) {
            if (clientDB.isOpen()) {
                clientDB.close();
            }
        }
        cc.searchEnd();
        smc.closedevice();
    }

    public void init(Handler handler) {
        //test uncaughterror
//        Integer.valueOf("asd");
        txtMessage = findViewById(R.id.txtMessage);
//        cc = new RFCardControler(handler, context);
        cc = new ContactlessControler(handler, context);
        smc = new NeoSmartCardController(context);
        isAntiDDOSPrint = true;
        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkListener(v);
            }
        });
        btnOk.setVisibility(GONE);
        btnPrint = (android.widget.Button) findViewById(R.id.btnCetak);
        btnPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TF KLIK", "Print Clicked");
                if (isAntiDDOSPrint) {
                    Log.d("KLIK", "Processed");
                    isAntiDDOSPrint = false;
                } else {
                    Log.d("KLIK", "Ignored");
                }
            }
        });
        confirmationText = (TextView) findViewById(R.id.printConfirmText);
        btnNoPrint = (android.widget.Button) findViewById(R.id.btnNoPrint);
        btnNoPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TF KLIK", "No Print Clicked");
                btnOk.performClick();
            }
        });
        printPanelVisibility(GONE);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 2000);
        DATE_TOCOMP.set2DigitYearStart(cal.getTime());
        DATE.set2DigitYearStart(cal.getTime());
        //init print header
    }

    private void onOkListener(View v) {

    }

    private void printPanelVisibility(int visibility) {
        btnPrint.setVisibility(visibility);
        confirmationText.setVisibility(visibility);
        btnNoPrint.setVisibility(visibility);
    }

    private void refreshConfirmation() {
        confirmationText.setText(printConfirm[printcountbutton]);
    }

    public void setOkListener(View.OnClickListener onClickListener) {
        btnOk.setOnClickListener(onClickListener);
        btnNoPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TF OKLIK", "No Print Clicked");
                if (printcountbutton > 1) {
                    btnOk.performClick();
                }
                if (printcountbutton == 0) {
                    footerAdded = false;
                }
                printcountbutton++;
                printcount++;
                refreshConfirmation();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TF OKLIK", "Print Clicked");
                if (!printInUse && isAntiDDOSPrint) {
                    Log.d("TF OKLIK", "Processed");
                    isAntiDDOSPrint = false;
                    if (printcountbutton > 1) {
                        btnOk.performClick();
                    }
                    if (printcountbutton == 0) {
                        footerAdded = false;
                    }

                    printcountbutton++;
                    refreshConfirmation();
                } else {
                    Log.d("TF OKLIK", "Ignored");
                }
            }
        });
    }
}
