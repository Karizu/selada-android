package com.boardinglabs.mireta.selada.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.gson.GBanks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 11/28/17.
 */

public class RecyBanksAdapter extends RecyclerView.Adapter<RecyBanksAdapter.ViewHolder>{
    private List<GBanks> listBank;
    private OnClickItem onClickItem;

    public RecyBanksAdapter(OnClickItem onClick) {
        listBank = new ArrayList<>();
        onClickItem = onClick;
    }

    public void setData(List<GBanks> list) {
        listBank = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyBanksAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_banks_layout, parent, false));
    }

    int getDrawableId(Context c, String imageName) {
        return c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GBanks bank = listBank.get(position);
        holder.bankInfo.setText(bank.accounts.account_name);
        holder.backAccount.setText(bank.accounts.account_no);
        holder.selectedIcon.setVisibility(bank.selected?View.VISIBLE:View.GONE);
        if(bank.selected) {
            holder.container.setBackgroundResource(R.drawable.border_round_blue_sm);
        }else{
            holder.container.setBackgroundResource(R.drawable.border_light_gray);
        }
        if (bank.logo != null) {
            Glide.with(holder.itemView.getContext()).load(bank.logo.base_url + "/" + bank.logo.path)
                    .dontAnimate().into(holder.bankImg);
        }
        switch(bank.name){
            case "Bank Mandiri" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_mandiri).dontAnimate().into(holder.bankImg);break;
            case "Bank BCA" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_bca).dontAnimate().into(holder.bankImg);break;
            case "Bank Permata" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_permata).dontAnimate().into(holder.bankImg);break;
            case "Bank BNI" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_bni).dontAnimate().into(holder.bankImg);break;
            case "Bank UOB" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_uob).dontAnimate().into(holder.bankImg);break;
            case "Bank Woori Saudara" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_woori_saudara).dontAnimate().into(holder.bankImg);break;
            case "Bank Cimb Niaga" : Glide.with(holder.itemView.getContext()).load(R.drawable.ic_bank_cimb_niaga).dontAnimate().into(holder.bankImg);break;
            default : Glide.with(holder.itemView.getContext()).load(R.drawable.pampasy_logo).dontAnimate().into(holder.bankImg);break;
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickItem != null) {
                    onClickItem.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBank.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bankImg;
        private TextView bankInfo;
        private LinearLayout container;
        private TextView backAccount;
        private ImageView selectedIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            bankImg = (ImageView) itemView.findViewById(R.id.bank_icon);
            bankInfo = (TextView) itemView.findViewById(R.id.bank_name);
            backAccount = (TextView) itemView.findViewById(R.id.bank_account);
            container = (LinearLayout) itemView.findViewById(R.id.container_banks_list);
            selectedIcon = (ImageView) itemView.findViewById(R.id.selectedIcon);
        }
    }

    public interface OnClickItem {
        void onClick(int position);
    }
}
