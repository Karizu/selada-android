package com.boardinglabs.mireta.selada.modul.biller.selada;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.selada.component.network.entities.Items.Category;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.Utils;
import com.boardinglabs.mireta.selada.modul.biller.selada.purchase.PurchaseActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeBillerAdapter extends RecyclerView.Adapter<HomeBillerAdapter.ViewHolder> {
    private List<Category> transactionModels;
    private HashMap<String, Integer> listDrwb;
    private Context context;

    public HomeBillerAdapter(List<Category> transactionModels, Context context) {
        this.transactionModels = transactionModels;
        this.listDrwb = Utils.icons();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category category = transactionModels.get(position);
        final int id = category.getId();
        final String product_name = category.getName();
        final int status = Integer.parseInt(category.getStatus());
        final int drawable = listDrwb.get(category.getName()) != null ? listDrwb.get(category.getName()) : listDrwb.get("Default");

        holder.menuTitle.setText(product_name);
        Glide.with(context).load(category.getImage())
                .placeholder(drawable).fitCenter().dontAnimate()
                .into(holder.menuIcon);

        switch (status) {
            case 0:
                holder.menuLayout.setVisibility(View.GONE);
                break;
            case 1:
                holder.menuLayout.setVisibility(View.VISIBLE);
                break;
            default:
        }

        holder.menuLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PurchaseActivity.class);
            intent.putExtra(Constant.POSITION, id);
            intent.putExtra(Constant.POSITION_NAME, product_name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RobotoRegularTextView menuTitle;
        LinearLayout menuLayout;
        ImageView menuIcon;


        ViewHolder(View v) {
            super(v);

            menuTitle = v.findViewById(R.id.menu_title);
            menuLayout = v.findViewById(R.id.menu_layout);
            menuIcon = v.findViewById(R.id.menu_icon);
        }
    }

    public void updateData(List<Category> newUser) {
        transactionModels = new ArrayList<>();
        transactionModels.addAll(newUser);
        notifyDataSetChanged();
    }
}
