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
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.modul.history.DetailTransactionActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecyProductAdapter extends RecyclerView.Adapter<RecyProductAdapter.ViewHolder> {

    private List<GSeladaService> listServices;
    private Context context;
    private Dialog dialog;

    public RecyProductAdapter(List<GSeladaService> listServices, Context context) {
        this.listServices = listServices;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_transaction, parent, false);

        return new RecyProductAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull RecyProductAdapter.ViewHolder holder, int position) {
        final GSeladaService seladaService = listServices.get(position);

    }

    @Override
    public int getItemCount() {
        return listServices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusOrder;
        RobotoBoldTextView tvOrderNo;
        RobotoBoldTextView tvAmount;
        TextView tvOrderDate;
        LinearLayout layoutTransaction;
        FrameLayout layoutDelete;

        ViewHolder(View v) {
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
}
