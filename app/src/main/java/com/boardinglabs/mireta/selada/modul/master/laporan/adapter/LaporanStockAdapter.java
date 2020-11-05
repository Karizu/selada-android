package com.boardinglabs.mireta.selada.modul.master.laporan.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.modul.master.laporan.model.LaporanModel;

import java.util.List;

public class LaporanStockAdapter extends RecyclerView.Adapter<LaporanStockAdapter.ViewHolder> {
    private List<LaporanModel> transactionModels;
    private Context context;
    private Dialog dialog;
    private BottomSheetDialog dialogBottom;
    private List<String> itemImages;
    private String stock_location_id;

    public LaporanStockAdapter(List<LaporanModel> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    public LaporanStockAdapter(List<LaporanModel> transactionModels, Context context, List<String> itemImages, String stock_location_id){
        this.transactionModels = transactionModels;
        this.context = context;
        this.itemImages = itemImages;
        this.stock_location_id = stock_location_id;
    }

    @NonNull
    @Override
    public LaporanStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_stock, parent, false);

        return new LaporanStockAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull LaporanStockAdapter.ViewHolder holder, int position){
        final LaporanModel transactionModel = transactionModels.get(position);
        final String name = transactionModel.getName();
//        final String description = transactionModel.getDescription();
        Long qtyOut = transactionModel.getQtyOut();
        Long qtySisa = Long.valueOf(transactionModel.getTotal_today_qty());
        Long qtyAwal = qtySisa + qtyOut;

        holder.tvName.setText(name);
        holder.tvQtyTersedia.setText(qtyAwal+" Pcs");
        holder.tvQtyKeluar.setText(qtyOut+" Pcs");
        holder.tvQtySisa.setText(qtySisa+" Pcs");
        holder.layout.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvQtyTersedia;
        TextView tvQtyKeluar;
        TextView tvQtySisa;
        LinearLayout layout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvNameBarang);
            tvQtyTersedia = v.findViewById(R.id.tvQtyTersedia);
            tvQtyKeluar = v.findViewById(R.id.tvQtyKeluar);
            tvQtySisa = v.findViewById(R.id.tvQtySisa);
            layout = v.findViewById(R.id.layoutStock);
        }
    }
}
