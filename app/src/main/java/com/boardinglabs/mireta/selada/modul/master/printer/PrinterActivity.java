package com.boardinglabs.mireta.selada.modul.master.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrinterActivity extends AppCompatActivity {

    private PrinterDevice printerDevice;
    private Format format;
    private TextView txt;
    private String str;
    private Context context = PrinterActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        ButterKnife.bind(this);

        printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                "cloudpos.device.printer");
        txt = findViewById(R.id.txt);
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = new Runnable() {
        public void run() {
            txt.setText(str);
        }
    };

    @OnClick(R.id.btnOpen)
     public void onClickBtnOpen(){
        try {
            str = context.getString(R.string.openingPrint) + "\n";
            handler.post(myRunnable);
            printerDevice.open();
            str += context.getString(R.string.printerOpenSuc) + "\n";
            handler.post(myRunnable);
            format = new Format();
            try {
                if (printerDevice.queryStatus() == printerDevice.STATUS_OUT_OF_PAPER) {
                    str += context.getString(R.string.queryStatus) + "\n";
                    handler.post(myRunnable);
                } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                    str += context.getString(R.string.statusNor) + "\n";
                    handler.post(myRunnable);
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {

                                format.setParameter("align", "left");
                                format.setParameter("size", "extra-small");
                                printerDevice.printText(format, "extra-small，POS sign purchase order\n");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "small，POS sign purchase order\n");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "medium，POS sign purchase order\n");
                                format.setParameter("size", "large");
                                printerDevice.printText(format, "large，POS sign purchase order\n");
                                format.setParameter("size", "extra-large");
                                printerDevice.printText(format, "extra-large，POS sign purchase order\n");

                                format = new Format();

                                printerDevice.printText(format, "<BARCODE_CODABAR >\n");
                                format.setParameter("HRI-location", "up-down");
                                printerDevice.printBarcode(format, printerDevice.BARCODE_UPC_A, "123456789012");

                                format = new Format();
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "large");
                                printerDevice.printText(format, "POS sign purchase order");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "MERCHANT COPY:\n" +
                                        "--------------------------------");
                                printerDevice.printText(format, "MERCHANT NAME:\n" +
                                        "XXX XXXX XXXX CO.LTO\n" +
                                        "MER:10237135411994\n" +
                                        "TER:52878059\n" +
                                        "--------------------------------");
                                printerDevice.printText(format, "ISSUER:XXXXBank\n" +
                                        "CARD NO.:\n" +
                                        "6228 45** **** ***0614/S\n" +
                                        "EXP DATE:XXXX/XX\n" +
                                        "TXN TYPE:\n" +
                                        "SALE:\n" +
                                        "BATCH NO: 100687\n" +
                                        "INVOICE NO :002500\n" +
                                        "TRACE NO :009404\n" +
                                        "DATE:xxxx/xx/xx\n" +
                                        "TIME:xx:xx:xx\n" +
                                        "REFER NO :648965012342\n" +
                                        "AMOUNT: \n" +
                                        "\t$ 0.00\n" +
                                        "OPERATOR NO :001\n" +
                                        "Taxpayer code:371326198402097610\n" +
                                        "Tax certificate number:320160607000243893\n" +
                                        "Accounting organization code:13713260000\n\n" +
                                        "REFERENCE:\n" +
                                        "CARDHOLDER\n" +
                                        "SIGNATURE:\n\n" +
                                        "Please do not sign this test operation!\n");
                                printerDevice.printText(format, "--------------------------------\n" +
                                        "I CONFIRM THAT THE ABOVE TRANSACTION AGREED TO BE CREDITED TO THE CREDIT CARD ACCOUNT.\n" +
                                        "--------------------------------");
                                printerDevice.printlnText(format, "\n");
                                printerDevice.printlnText(format, "\n");
                                printerDevice.printlnText(format, "\n");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }
                        }
                    });
                    thread.start();
                }
            } catch (DeviceException de) {
                str += context.getString(R.string.checkStatus) + "\n";
                handler.post(myRunnable);
                de.printStackTrace();
            }
        } catch (DeviceException de) {
            de.printStackTrace();
            str += context.getString(R.string.openFailed) + "\n";
            handler.post(myRunnable);
        }
    }

    @OnClick(R.id.btnClose)
    void onClickBtnClose(){
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        }
    }

    @OnClick(R.id.printCode)
    void onClickPrintCode(){
        try {
            str = context.getString(R.string.openingPrint) + "\n";
            handler.post(myRunnable);
            printerDevice.open();
            str += context.getString(R.string.printerOpenSuc) + "\n";
            handler.post(myRunnable);
            if (printerDevice.queryStatus() == printerDevice.STATUS_OUT_OF_PAPER) {
                str += context.getString(R.string.queryStatus) + "\n";
                handler.post(myRunnable);
            } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                str += context.getString(R.string.statusNor) + "\n";
                handler.post(myRunnable);
                Bitmap bitmap = encode("0123456789abc", 400, 90);
                printerDevice.printlnText("\n");
                printerDevice.printBitmap(bitmap);
                printerDevice.printlnText("          0123456789abc" + "\n" + "\n");
                printerDevice.printlnText("    ");
                printerDevice.printlnText("    ");
            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.closePrint)
    void onClickClosePrint(){
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        }
    }
    @OnClick(R.id.printEsc)
    void onClickPrintEsc(){
        byte[] command = new byte[]{
                (byte) 0x12, (byte) 0x54
        };
        try {
            str = context.getString(R.string.openingPrint) + "\n";
            handler.post(myRunnable);
            printerDevice.open();
            str += context.getString(R.string.printerOpenSuc) + "\n";
            handler.post(myRunnable);
            if (printerDevice.queryStatus() == printerDevice.STATUS_OUT_OF_PAPER) {
                str += context.getString(R.string.queryStatus) + "\n";
                handler.post(myRunnable);
            } else if (printerDevice.queryStatus() == printerDevice.STATUS_PAPER_EXIST) {
                str += context.getString(R.string.statusNor) + "\n";
                printerDevice.sendESCCommand(command);
            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.closeEsc)
    void onClickCloseEsc(){
        try {
            printerDevice.close();
            str += context.getString(R.string.closeSuc) + "\n";
            handler.post(myRunnable);
        } catch (DeviceException e) {
            e.printStackTrace();
            str += context.getString(R.string.closeFailed) + "\n";
            handler.post(myRunnable);
        }
    }

    public Bitmap encode(String contents, int width, int height) {
        Bitmap bitmap = null;
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.CODE_128, codeWidth, height, null);
//			MatrixToImageWriter.writeToStream(bitMatrix, "png",
//					new FileOutputStream(imgPath));
            int aWidth = bitMatrix.getWidth();
            int aHeight = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
