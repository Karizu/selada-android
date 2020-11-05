package com.boardinglabs.mireta.selada.component.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.listener.ListActionListener;
import com.boardinglabs.mireta.selada.component.network.entities.Item;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 12/20/17.
 */

public class RecyOverviewItemsAdapter extends BaseSwipeAdapter {
    public List<Item> itemList;
    private ListActionListener listActionListener;
    private User user;

    public RecyOverviewItemsAdapter() {

        user = PreferenceManager.getUser();
        itemList = new ArrayList<>();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_row_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_overview, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        final Item item = itemList.get(position);

        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {

        final Item item = itemList.get(position);
        final LinearLayout container = convertView.findViewById(R.id.container_item);
        final TextView item_name = convertView.findViewById(R.id.item_name);
        final TextView item_qty = convertView.findViewById(R.id.item_total);
        final TextView item_price = convertView.findViewById(R.id.item_total_price);
        final ImageButton delete_button = convertView.findViewById(R.id.delete_button);

        item_name.setText(item.item.getName());
        item_qty.setText(item.order_qty + " Item");
        long total_price = (long) (Integer.valueOf(item.item.getPrice()) * item.order_qty);
        item_price.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(total_price)));

        delete_button.setOnClickListener(view -> {
            deleteItem(position);
            listActionListener.itemClicked(position);
        });
    }

    private void addItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input){
        Item item = itemList.get(position);
        item.order_qty += 1;
        if (item.order_qty >= 1){
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            qty_input.setText("" + item.order_qty);
        }
        notifyDataSetChanged();
    }

    private void minusItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input){
        Item item = itemList.get(position);
        item.order_qty -= 1;
        if (item.order_qty <= 0){
            add_button_container.setVisibility(View.VISIBLE);
            add_minus_button_container.setVisibility(View.GONE);
            qty_input.setText("0");
        }
        else{
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            qty_input.setText("" + item.order_qty);
        }
        notifyDataSetChanged();
    }

    private void deleteItem(int position){
//        itemList.remove(position);
//        notifyDataSetChanged();
    }

    public void setDataList(List<Item> transactionItems) {
        itemList = transactionItems;
        notifyDataSetChanged();
    }

    public void addDataList(List<Item> items) {
        if (itemList == null){
            itemList = new ArrayList<>();
        }
        itemList.addAll(items);
        notifyDataSetChanged();
    }
    public void setListener(ListActionListener listClicked) {
        this.listActionListener = listClicked;
    }

    @Override
    public int getCount() {
        return itemList.size();
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
