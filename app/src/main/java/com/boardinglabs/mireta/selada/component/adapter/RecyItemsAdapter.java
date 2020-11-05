package com.boardinglabs.mireta.selada.component.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.listener.ItemActionListener;
import com.boardinglabs.mireta.selada.component.network.entities.Item;
import com.boardinglabs.mireta.selada.component.network.entities.User;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.boardinglabs.mireta.selada.modul.transactions.items.ItemsActivity;
import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 12/20/17.
 */

public class RecyItemsAdapter extends BaseSwipeAdapter {
    public List<Item> itemList;
    private ItemActionListener itemActionListener;
    private User user;
    private View v;
    private Context mContext;

    public RecyItemsAdapter() {

        user = PreferenceManager.getUser();
        itemList = new ArrayList<>();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_row_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        final Item item = itemList.get(position);

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void fillValues(final int position, View convertView) {

        final Item item = itemList.get(position);
        final LinearLayout container = convertView.findViewById(R.id.container_item);
        final ImageView item_image = convertView.findViewById(R.id.item_image);
        final TextView item_name = convertView.findViewById(R.id.item_name);
        final TextView item_desc = convertView.findViewById(R.id.item_desc);
        final TextView item_stok = convertView.findViewById(R.id.item_stok);
        final TextView item_price = convertView.findViewById(R.id.item_price);
        final EditText qty_input = convertView.findViewById(R.id.qty_input);
        final ImageButton add_button = convertView.findViewById(R.id.add_button);
        final ImageButton minus_button = convertView.findViewById(R.id.minus_button);
        final Button normal_add_button = convertView.findViewById(R.id.normal_add_button);
        final ImageButton delete_button = convertView.findViewById(R.id.delete_button);
        final LinearLayout add_button_container = convertView.findViewById(R.id.add_button_container);
        final LinearLayout add_minus_button_container = convertView.findViewById(R.id.add_minus_container);

        if (item.item != null){
            String image = item.item.getImage();
            final String name = item.item.getName();
            final String description = item.item.getDescription();
            final String qty = item.qty;
            final String price = item.item.getPrice();


            Glide.with(convertView.getContext()).load(image)
                    .placeholder(R.drawable.resto_default).fitCenter().dontAnimate()
                    .into(item_image);


            item_name.setText(name);
            item_desc.setText(description);

            if (qty != null) {
                if (!qty.equals("0")) {
                    item_stok.setText("Stok : " + item.getQty());
                    normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_round_gradient));
                    item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.gray_primary_dark));
                    normal_add_button.setEnabled(true);
                } else {
                    item_stok.setText("Stok Kosong");
                    normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
                    item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
                    normal_add_button.setEnabled(false);
                }
            } else {
                item_stok.setText("Stok Kosong");
                normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
                item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
                normal_add_button.setEnabled(false);
            }

            item_price.setText("Rp " + MethodUtil.toCurrencyFormat(price));

            if (item.order_qty <= 0) {
                add_button_container.setVisibility(View.VISIBLE);
                add_minus_button_container.setVisibility(View.GONE);
                delete_button.setVisibility(View.GONE);
            } else {
                qty_input.setText("" + item.order_qty);
                add_button_container.setVisibility(View.GONE);
                add_minus_button_container.setVisibility(View.VISIBLE);
                delete_button.setVisibility(View.VISIBLE);
            }

            add_button.setOnClickListener(view -> {
                Item item12 = itemList.get(position);
                int totalQty;
                totalQty = Integer.parseInt(item12.getQty());
                Log.d("TAG QTY", "" + totalQty);
                if (totalQty > 0) {
                    if (item12.getOrder_qty() < totalQty) {
                        item12.order_qty += 1;
                        Log.d("TAG ORDER QTY", "" + item12.getOrder_qty());
                        itemActionListener.itemAdd(position);
                        addItem(position, add_button_container, add_minus_button_container, qty_input);
                    } else {
                        if (mContext instanceof ItemsActivity) {
                            ((ItemsActivity) mContext).showSnackbarQty();
                        }
                    }
                } else {
                    if (mContext instanceof ItemsActivity) {
                        ((ItemsActivity) mContext).showSnackbar();
                    }
                }
            });

            minus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = itemList.get(position);
                    item.order_qty -= 1;
                    itemActionListener.itemMinus(position);
                    minusItem(position, add_button_container, add_minus_button_container, qty_input);
                }
            });

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                Item item = itemList.get(position);
//                item.order_qty -= 1;
//                itemActionListener.itemMinus(position);
//                minusItem(position, add_button_container, add_minus_button_container, qty_input);

                    Item item = itemList.get(position);
                    item.order_qty = 0;
                    itemActionListener.itemDeleted(position);
                    deleteItem(position, add_button_container, add_minus_button_container, qty_input);
                }
            });

            normal_add_button.setOnClickListener(view -> {
                Item item1 = itemList.get(position);

                if (item1.getQty() != null) {
                    int totalQty = Integer.parseInt(item1.getQty());
                    Log.d("TAG QTY", "" + totalQty);
                    if (totalQty > 0) {
                        item1.order_qty += 1;
                        Log.d("TAG ORDER QTY", "" + item1.getOrder_qty());
                        if (item1.getOrder_qty() <= totalQty) {
                            itemActionListener.itemAdd(position);
                            addItem(position, add_button_container, add_minus_button_container, qty_input);
                        } else {
                            if (mContext instanceof ItemsActivity) {
                                ((ItemsActivity) mContext).showSnackbarQty();
                            }
                        }
                    } else {
                        if (mContext instanceof ItemsActivity) {
                            ((ItemsActivity) mContext).showSnackbar();
                        }
                    }
                }
                if (item1.getQty() == null) {
                    if (mContext instanceof ItemsActivity) {
                        ((ItemsActivity) mContext).showSnackbar();
                    }
                }

//                totalQty = item.getTotal_qty().total_qty;
//                Log.d("TAG QTY", totalQty);
//                item.order_qty += 1;
//                itemActionListener.itemAdd(position);
//                addItem(position, add_button_container, add_minus_button_container, qty_input);
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                itemActionListener.itemClicked(position);
                }
            });
        }



//        if (item.total_today_qty != null) {
//            if (!item.total_today_qty.getTotal_daily_qty().equals("0")) {
//                item_stok.setText("Stok : " + item.total_today_qty.getTotal_daily_qty());
//                normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_round_gradient));
//                item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.gray_primary_dark));
//                normal_add_button.setEnabled(true);
//            } else {
//                item_stok.setText("Stok Habis");
//                normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
//                item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
//                normal_add_button.setEnabled(false);
//            }
//        } else {
//            item_stok.setText("Stok Habis");
//            normal_add_button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gray_button_background));
//            item_stok.setTextColor(ContextCompat.getColor(mContext, R.color.red_alert));
//            normal_add_button.setEnabled(false);
//        }
    }

    @SuppressLint("SetTextI18n")
    private void addItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        if (item.order_qty >= 1) {
            int totalQty;
            totalQty = Integer.parseInt(item.getQty());
            Log.d("TAG QTY", "" + totalQty);
            if (item.getOrder_qty() <= totalQty) {
                add_button_container.setVisibility(View.GONE);
                add_minus_button_container.setVisibility(View.VISIBLE);
                qty_input.setText("" + item.order_qty);
            } else {
                if (mContext instanceof ItemsActivity) {
                    ((ItemsActivity) mContext).showSnackbarQty();
                }
            }
        }
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void minusItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        if (item.order_qty <= 0) {
            add_button_container.setVisibility(View.VISIBLE);
            add_minus_button_container.setVisibility(View.GONE);
            qty_input.setText("0");
        } else {
            add_button_container.setVisibility(View.GONE);
            add_minus_button_container.setVisibility(View.VISIBLE);
            qty_input.setText("" + item.order_qty);
        }
        notifyDataSetChanged();
    }

    private void deleteItem(int position, View add_button_container, View add_minus_button_container, EditText qty_input) {
        Item item = itemList.get(position);
        add_button_container.setVisibility(View.VISIBLE);
        add_minus_button_container.setVisibility(View.GONE);
        qty_input.setText("0");
        notifyDataSetChanged();
    }

    public void setListener(ItemActionListener listClicked) {
        this.itemActionListener = listClicked;
    }

    public void setDataList(List<Item> transactionItems, Context context) {
        itemList = transactionItems;
        mContext = context;
        notifyDataSetChanged();
    }

    public void addDataList(List<Item> items) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.addAll(items);
        notifyDataSetChanged();
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

    public void updateData(List<Item> newUser) {
        itemList = new ArrayList<>();
        itemList.addAll(newUser);
    }
}
