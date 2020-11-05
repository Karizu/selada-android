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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.fontview.RobotoBoldTextView;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.selada.modul.history.DetailTransactionActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionModel> transactionModels;
    private Context context;
    private Dialog dialog;

    public TransactionAdapter(List<TransactionModel> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_transaction, parent, false);

        return new TransactionAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position){
        final TransactionModel transactionModel = transactionModels.get(position);
        final String id = transactionModel.getId();
        final String amount = transactionModel.getAmount();
        final String order_date = transactionModel.getOrder_date();
        final String discount = transactionModel.getDiscount();
        final String order_no = transactionModel.getOrder_no();
        final String status = transactionModel.getStatus();

        Date d = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sdf.parse(order_date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

        sdf.applyPattern("dd MMM yyyy");
        String orderDate = sdf.format(d);
        sdf.applyPattern("HH:mm");
        String orderTime = sdf.format(d);

        switch (status){
            case "1":
                holder.tvStatusOrder.setText("PENDING");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                break;
            case "2":
                holder.tvStatusOrder.setText("BERHASIL");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Green));
                break;
            case "3":
                holder.tvStatusOrder.setText("DIBATALKAN");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.Red));
                break;
        }

        int mDiscount = Integer.parseInt(discount);
        int mHarga = Integer.parseInt(amount);
        String harga = String.valueOf(mHarga);

        holder.tvOrderNo.setText(order_no);
//        holder.tvDiscount.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mDiscount));
        holder.tvOrderDate.setText(orderDate);
        holder.tvAmount.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(mHarga));

        Date finalD = d;
        holder.layoutTransaction.setOnClickListener(view -> {
//            Snackbar snackbar = Snackbar.make(view.getRootView().findViewById(android.R.id.content), "On Progress", Snackbar.LENGTH_LONG);
//            snackbar.show();
            Intent intent = new Intent(context, DetailTransactionActivity.class);
            intent.putExtra("order_no", id);
            intent.putExtra("total", harga);
            intent.putExtra("order_date", orderDate);
            intent.putExtra("order_time", orderTime);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.layoutDelete.setOnClickListener(v -> {
//            Snackbar snackbar = Snackbar.make(v.getRootView().findViewById(android.R.id.content), "On Progress", Snackbar.LENGTH_LONG);
//            snackbar.show();
            transactionModels.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, transactionModels.size());
        });
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusOrder;
        RobotoBoldTextView tvOrderNo;
        RobotoBoldTextView tvAmount;
        TextView tvOrderDate;
        LinearLayout layoutTransaction;
        FrameLayout layoutDelete;

        ViewHolder(View v){
            super(v);

            tvStatusOrder = v.findViewById(R.id.status_transaction);
            tvOrderNo = v.findViewById(R.id.transaction_id);
            tvAmount = v.findViewById(R.id.amount_transaction);
            tvOrderDate = v.findViewById(R.id.time_transaction);
            layoutTransaction = v.findViewById(R.id.container_transaction);
            layoutDelete = v.findViewById(R.id.user_feed_row_bottom);
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

    public void updateData(List<TransactionModel> newUser){
        transactionModels = new ArrayList<>();
        transactionModels.addAll(newUser);
        notifyDataSetChanged();
    }
}
