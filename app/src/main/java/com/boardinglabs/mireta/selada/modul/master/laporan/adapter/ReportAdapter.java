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
import com.boardinglabs.mireta.selada.component.network.entities.Report.ReportModels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private LinkedHashMap<String, ArrayList<ReportModels>> itemCategory;
    private ArrayList<ReportModels> reportModels;
    private Context context;
    private Dialog dialog;
    private BottomSheetDialog dialogBottom;
    private List<String> itemImages;
    private String stock_location_id;

    public ReportAdapter(LinkedHashMap<String, ArrayList<ReportModels>> itemCategory, Context context) {
        this.itemCategory = itemCategory;
        this.context = context;
    }

    public ReportAdapter(ArrayList<ReportModels> reportModels, Context context) {
        this.reportModels = reportModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report, parent, false);

        return new ReportAdapter.ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        try {
            ReportModels models = reportModels.get(position);
            holder.tvCategory.setText(models.getCategory_name());
            holder.tvName.setText(models.getItem_name());
            int mHarga = Integer.parseInt(models.getItem_price());
            holder.tvAmount.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(mHarga));
            holder.tvQty.setText(models.getItem_qty() + " pcs");
            int mTotal = mHarga*models.getItem_qty();
            holder.tvTotal.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(mTotal));
        } catch (Exception e) {
            position = 0;
        }
//        for (int i = 0; i < itemCategory.size(); i++) {
//            ArrayList<ReportModels> reportModels = (new ArrayList<>(itemCategory.values())).get(i);
//            try {
//                ReportModels models = reportModels.get(position);
//                holder.tvCategory.setText(models.getCategory_name());
//                holder.tvName.setText(models.getItem_name());
//                int mHarga = Integer.parseInt(models.getItem_price());
//                holder.tvAmount.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(mHarga));
//                holder.tvQty.setText(models.getItem_qty()+" pcs");
//            } catch (Exception e){
//                position = 0;
//            }
//
//        }
//
//        for (Map.Entry<String, ArrayList<ReportModels>> entry : itemCategory.entrySet()) {
//            ArrayList<ReportModels> reportModels = itemCategory.get(entry.getKey());
//            ReportModels models = reportModels.get(position);
//        }

//        final ArrayList<ReportModels> reportModels = itemCategory.values().get(position);
//        final String id = transactionModel.getId();
//        final String name = transactionModel.getName();
//        final String description = transactionModel.getDeskripsi();
//        String qty = transactionModel.getTotal_qty();
//        String is_daily_stok = transactionModel.getIs_daily_stock();
//        final String harga = transactionModel.getHarga();
//
//        holder.tvName.setText(name);
//        holder.tvAmount.setText(description);
//        holder.tvQty.setText(qty+" Pcs");
//        holder.layout.setOnClickListener(view -> {
//
//        });
    }

    @Override
    public int getItemCount() {
//        int count = 0;
//        for (int i = 0; i < itemCategory.size(); i++) {
//            ArrayList<ReportModels> reportModels = (new ArrayList<>(itemCategory.values())).get(i);
//            count += reportModels.size();
//        }
//        Log.d("TAG COUNT", count+"");
//        return count;
        return reportModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvName;
        TextView tvAmount;
        TextView tvQty;
        TextView tvTotal;
        View line;
        LinearLayout layout;

        ViewHolder(View v) {
            super(v);

            tvCategory = v.findViewById(R.id.tvTitleCategory);
            tvName = v.findViewById(R.id.tvItemName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvQty = v.findViewById(R.id.tvQty);
            tvTotal = v.findViewById(R.id.tvTotal);
            line = v.findViewById(R.id.viewLine);
            layout = v.findViewById(R.id.layoutPenjualan);
        }
    }
}
