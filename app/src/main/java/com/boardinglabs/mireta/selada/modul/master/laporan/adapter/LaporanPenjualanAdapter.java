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
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.KatalogModel;

import java.util.List;

public class LaporanPenjualanAdapter extends RecyclerView.Adapter<LaporanPenjualanAdapter.ViewHolder> {
    private List<KatalogModel> transactionModels;
    private Context context;
    private Dialog dialog;
    private BottomSheetDialog dialogBottom;
    private List<String> itemImages;
    private String stock_location_id;

    public LaporanPenjualanAdapter(List<KatalogModel> transactionModels, Context context){
        this.transactionModels = transactionModels;
        this.context = context;
    }

    public LaporanPenjualanAdapter(List<KatalogModel> transactionModels, Context context, List<String> itemImages, String stock_location_id){
        this.transactionModels = transactionModels;
        this.context = context;
        this.itemImages = itemImages;
        this.stock_location_id = stock_location_id;
    }

    @NonNull
    @Override
    public LaporanPenjualanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_laporan, parent, false);

        return new LaporanPenjualanAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull LaporanPenjualanAdapter.ViewHolder holder, int position){
        final KatalogModel transactionModel = transactionModels.get(position);
        final String id = transactionModel.getId();
        final String name = transactionModel.getName();
        final String description = transactionModel.getDeskripsi();
        String qty = transactionModel.getTotal_qty();
        String is_daily_stok = transactionModel.getIs_daily_stock();
        final String harga = transactionModel.getHarga();

        holder.tvName.setText(name);
        holder.tvSKU.setText(description);
        holder.tvQty.setText(qty+" Pcs");
        holder.layout.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount(){
        return transactionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSKU;
        TextView tvQty;
        LinearLayout layout;

        ViewHolder(View v){
            super(v);

            tvName = v.findViewById(R.id.tvNameBarang);
            tvSKU = v.findViewById(R.id.tvSKU);
            tvQty = v.findViewById(R.id.tvQty);
            layout = v.findViewById(R.id.layoutPenjualan);
        }
    }
}
