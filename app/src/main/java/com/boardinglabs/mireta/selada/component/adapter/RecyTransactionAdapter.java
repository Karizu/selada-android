package com.boardinglabs.mireta.selada.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.component.network.entities.Transaction;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionModel;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.listener.ListActionListener;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 12/20/17.
 */

public class RecyTransactionAdapter extends BaseSwipeAdapter {
    public List<Transaction> transactionList;
    private List<TransactionModel> transactionModels;
    private ListActionListener itemActionListener;
    private User user;

    public RecyTransactionAdapter(List<TransactionModel> transactionModels) {
        this.transactionModels = transactionModels;
        user = PreferenceManager.getUser();
        transactionList = new ArrayList<>();
    }

    public RecyTransactionAdapter() {
        user = PreferenceManager.getUser();
        transactionList = new ArrayList<>();
        transactionModels = new ArrayList<>();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.transaction_row_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        final Transaction transaction = transactionList.get(position);

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void fillValues(final int position, View convertView) {

        if (transactionList != null){
            final Transaction transaction = transactionList.get(position);
            final LinearLayout container = convertView.findViewById(R.id.container_transaction);
            final TextView status = convertView.findViewById(R.id.status_transaction);
            final TextView transaction_id = convertView.findViewById(R.id.transaction_id);
            final TextView amount_transaction = convertView.findViewById(R.id.amount_transaction);
            final TextView time_transaction = convertView.findViewById(R.id.time_transaction);

//        if (transaction.status == 1) {
//            status.setText("SELESAI");
//            status.setTextColor(Color.parseColor("#2CC013"));
//        } else if (transaction.status == 2) {
//            status.setText("PENDING");
//            status.setTextColor(Color.parseColor("#E0D000"));
//        } else {
//            status.setText("DIBATALKAN");
//            status.setTextColor(Color.parseColor("#A31627"));
//        }

//        status.setText(transaction.stock_location.name);
            status.setText("PENDING");
            transaction_id.setText(transaction.transaction_code);
            amount_transaction.setText(transaction.total_price);
            String[] dateTime = MethodUtil.formatDateAndTime(transaction.created_at);
//        time_transaction.setText(dateTime[0] + " " + dateTime[1]);
            time_transaction.setText(transaction.created_at);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemActionListener.itemClicked(position);
                }
            });
        } else {

            final TransactionModel transaction = transactionModels.get(position);
            final LinearLayout container = convertView.findViewById(R.id.container_transaction);
            final TextView status = convertView.findViewById(R.id.status_transaction);
            final TextView transaction_id = convertView.findViewById(R.id.transaction_id);
            final TextView amount_transaction = convertView.findViewById(R.id.amount_transaction);
            final TextView time_transaction = convertView.findViewById(R.id.time_transaction);

            int mStatus = Integer.parseInt(transaction.getStatus());

        if (mStatus == 1) {
            status.setText("PENDING");
            status.setTextColor(Color.parseColor("#2CC013"));
        } else if (mStatus == 2) {
            status.setText("SELESAI");
            status.setTextColor(Color.parseColor("#E0D000"));
        } else {
            status.setText("DIBATALKAN");
            status.setTextColor(Color.parseColor("#A31627"));
        }

//        status.setText(transaction.stock_location.name);
//            status.setText("PENDING");
            transaction_id.setText(transaction.getOrder_no());
            amount_transaction.setText(transaction.getAmount());
//            String[] dateTime = MethodUtil.formatDateAndTime(transaction.created_at);
//        time_transaction.setText(dateTime[0] + " " + dateTime[1]);
            time_transaction.setText(transaction.getOrder_date());
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemActionListener.itemClicked(position);
                }
            });

        }
    }

    public void setListener(ListActionListener listClicked) {
        this.itemActionListener = listClicked;
    }

    public void setDataList(List<Transaction> transactions) {
        transactionList = transactions;
        notifyDataSetChanged();
    }

    public void setDataList(List<TransactionModel> transactions, Context context) {
        transactionModels = transactions;
        notifyDataSetChanged();
    }

    public void addDataList(List<Transaction> transactions) {
        if (transactionList == null){
            transactionList = new ArrayList<>();
        }
        transactionList.addAll(transactions);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (transactionList!=null){
            return transactionList.size();
        } else {
            return transactionModels.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}