package com.boardinglabs.mireta.selada.component.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.selada.component.network.ApiSelada;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.HistoryTopup.HistoryTopup;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionStatus;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.DetailTransactionHistoryActivity;
import com.boardinglabs.mireta.selada.modul.biller.selada.history.TransactionHistoryActivity;
import com.boardinglabs.mireta.selada.modul.selada.launcher.history.topup.DetailHistoryTopupActivity;
import com.boardinglabs.mireta.selada.modul.selada.launcher.history.topup.HistoryTopupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {
    private List<GSeladaTransaction> transactionModels;
    private Context context;
    private Dialog dialog;
    private RequestBody requestBody;
    private int status = 3;

    public TransactionHistoryAdapter(List<GSeladaTransaction> transactionModels, Context context) {
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_transaction_topup, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GSeladaTransaction transactionModel = transactionModels.get(position);
        final int id = transactionModel.id;
        final String transaction_code = transactionModel.code;
        final String merchant_no = transactionModel.merchant_no;
        final String product_name = transactionModel.service.product.name;
        final String amount = transactionModel.price;
        final String order_date = transactionModel.created_at;
        final String note = transactionModel.note != null ? transactionModel.note : "";
        int status = transactionModel.status;

        Date d = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sdf.parse(order_date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        sdf.applyPattern("dd-MM-yyyy HH:mm");

        holder.member_id.setText(transaction_code);
        holder.tvOrderNo.setText(product_name);
        holder.tvOrderDate.setText(sdf.format(d));
        holder.tvAmount.setText("Rp " + MethodUtil.toCurrencyFormat(amount));

        try {
            JSONObject json = new JSONObject(note);
            switch (json.getInt("sts")) {
                case 100:
                    switch (status) {
                        case 0:
                            holder.tvStatusOrder.setText("PENDING");
                            holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                            break;
                        case 1:
                            holder.tvStatusOrder.setText("BERHASIL");
                            holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                            break;
                        case 2:
                            holder.tvStatusOrder.setText("GAGAL");
                            holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                            break;
                    }
                    break;
                case 500:
                    status = 1;
                    holder.tvStatusOrder.setText("BERHASIL");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                    break;
                default:
                    status = 2;
                    holder.tvStatusOrder.setText("GAGAL");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                    break;
            }
        } catch (JSONException e) {
            switch (status) {
                case 0:
                    holder.tvStatusOrder.setText("PENDING");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                    break;
                case 1:
                    holder.tvStatusOrder.setText("BERHASIL");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                    break;
                case 2:
                    holder.tvStatusOrder.setText("GAGAL");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                    break;
            }
            e.printStackTrace();
        }
//        getTrxStatus(holder, merchant_no, transaction_code);

        switch (Integer.valueOf(transactionModel.service.category_id)) {
            case 1:
                holder.iconTransaction.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pulsa));
                break;
            case 2:
                holder.iconTransaction.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_data));
                break;
            case 3:
                holder.iconTransaction.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pln_prabayar_lightning));
                break;
            case 4:
                holder.iconTransaction.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pln_pascabayar_lightning));
                break;
        }

        Date finalD = d;
        int finalStatus = status;
        holder.layoutTransaction.setOnClickListener(view -> {
            if (context instanceof TransactionHistoryActivity) {
                Intent intent = new Intent(context, DetailTransactionHistoryActivity.class);
                intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_ID, id + "");
                intent.putExtra(DetailTransactionHistoryActivity.TRANSACTION_STATUS, finalStatus);
                context.startActivity(intent);
            }
        });

        holder.layoutDelete.setOnClickListener(v -> {
            transactionModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, transactionModels.size());
        });
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusOrder;
        RobotoBoldTextView tvOrderNo;
        RobotoBoldTextView tvAmount;
        RobotoBoldTextView member_id;
        TextView tvOrderDate;
        LinearLayout layoutTransaction;
        FrameLayout layoutDelete;
        ImageView iconTransaction;


        ViewHolder(View v) {
            super(v);

            tvStatusOrder = v.findViewById(R.id.status_transaction);
            tvOrderNo = v.findViewById(R.id.transaction_id);
            member_id = v.findViewById(R.id.member_id);
            tvAmount = v.findViewById(R.id.amount_transaction);
            tvOrderDate = v.findViewById(R.id.time_transaction);
            layoutTransaction = v.findViewById(R.id.container_transaction);
            layoutDelete = v.findViewById(R.id.user_feed_row_bottom);
            iconTransaction = v.findViewById(R.id.icon_transaction);
        }
    }

    public void getTrxStatus(ViewHolder holder, String nop, String trx_code) {
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nop", nop)
                .addFormDataPart("transaction_code", trx_code)
                .build();

        try {
            ApiSelada.apiInterface().doCheckStatus(requestBody, "Bearer "+PreferenceManager.getSessionToken()).enqueue(new Callback<ApiResponse<TransactionStatus>>() {
                @Override
                public void onResponse(Call<ApiResponse<TransactionStatus>> call, Response<ApiResponse<TransactionStatus>> response) {
                    if (response.isSuccessful()) {
                        try {
                            TransactionStatus transactionStatus = Objects.requireNonNull(response.body()).getData();
                            switch (transactionStatus.getSts()) {
                                case 100:
                                    holder.tvStatusOrder.setText("PENDING");
                                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                                    break;
                                case 500:
                                    holder.tvStatusOrder.setText("BERHASIL");
                                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                                    break;
                                case 200:
                                    holder.tvStatusOrder.setText("GAGAL");
                                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                                    break;
                                default:
                                    holder.tvStatusOrder.setText("PENDING");
                                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                                    break;
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            String resp = MethodUtil.getResponseError(Objects.requireNonNull(response.errorBody()).toString());
                            Log.d("CEK TRX STATUS", resp);
                            holder.tvStatusOrder.setText("PENDING");
                            holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<TransactionStatus>> call, Throwable t) {
                    holder.tvStatusOrder.setText("PENDING");
                    holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
                    t.printStackTrace();
                }
            });
        } catch (Exception e){
            holder.tvStatusOrder.setText("PENDING");
            holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.dusty_orange));
            e.printStackTrace();
        }
    }

    private void showDialog(int layout, Context context) {
        dialog = new Dialog(Objects.requireNonNull(context));
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

    public void updateData(List<GSeladaTransaction> newUser) {
        transactionModels = new ArrayList<>();
        transactionModels.addAll(newUser);
        notifyDataSetChanged();
    }
}
