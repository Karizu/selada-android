package com.boardinglabs.mireta.selada.component.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.gson.GTransaction;
import com.boardinglabs.mireta.selada.component.util.Constant;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by Dhimas on 9/25/17.
 */

public class RecyFeedTrxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<GTransaction> transactions;
    private OnListClicked onListClicked;
    private ListActionLoadMore mListener;
    private boolean isPPOB;

    private enum ITEM_TYPE {
        ITEM_TYPE_TRANSACTION,
        ITEM_TYPE_LOADMORE
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_TRANSACTION.ordinal()) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_feed_trx, parent, false));
        }
        return new ViewHolderLoadMore(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loadmore, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final GTransaction transaction = transactions.get(position);

            if (transaction.service != null ) {
                switch (transaction.service.category){
                    case Constant.SERVICE_PULSA : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_phone); break;
                    case Constant.SERVICE_PAKET_DATA : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_data); break;
                    case Constant.SERVICE_PDAM : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_pdam); break;
                    case Constant.SERVICE_PLN : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_pln); break;
                    case Constant.SERVICE_TELKOM : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_data); break;
                    case Constant.SERVICE_MULTIFINANCE : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_cicilan); break;
                    case Constant.SERVICE_ASURANSI : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.assetjiwasraya); break;
                    case Constant.SERVICE_KARTU_KREDIT : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_idr); break;
                    case Constant.SERVICE_ISP : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_internet); break;
                    case Constant.SERVICE_BPJS : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_bpjs); break;
                    default : ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_check); break;
                }
//                if (transaction.service.provider != null &&
//                        transaction.service.provider.logo != null) {
//                    Glide.with(holder.itemView.getContext()).
//                            load(transaction.service.provider.logo.base_url +"/" +
//                                    transaction.service.provider.logo.path).dontAnimate().
//                            into(((ViewHolder)holder).iconProduct);
//                }

//                ((ViewHolder)holder).topupText.setText(transaction.service.name);
                ((ViewHolder)holder).topupText.setText(transaction.id);
                ((ViewHolder)holder).paketText.setText("Rp " + MethodUtil.toCurrencyFormat(transaction.default_price));
//                ((ViewHolder)holder).infoText.setText(transaction.customer_no);
            } else {
//                if (PreferenceManager.getStatusAkupay()) {
//                    Glide.with(holder.itemView.getContext()).load(R.drawable.akupay_fix)
//                            .dontAnimate().into(((ViewHolder)holder).iconProduct);
//                } else {
//                    Glide.with(holder.itemView.getContext()).load(R.drawable.pampasy_icon)
//                            .dontAnimate().into(((ViewHolder)holder).iconProduct);
//                }
//                ((ViewHolder)holder).topupText.setText(transaction.merchant_name);
                if(transaction.merchant_name!=null){
                    ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_merchant);
                }else{
                    if(transaction.topup.sub_customer!=null && transaction.topup.balance_before!=null && transaction.topup.balance_after!=null){
                        long temp = Long.parseLong(transaction.topup.balance_before) - Long.parseLong(transaction.topup.balance_after);
                        if (temp > 0) {
                            ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_transfer);
                        }else{
                            ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_request);
                        }
                    }else{
                        ((ViewHolder)holder).iconProduct.setImageResource(R.drawable.ic_trx_topup);
                    }
                }
                ((ViewHolder)holder).topupText.setText(transaction.id);
                ((ViewHolder)holder).paketText.setText("Rp " + MethodUtil.toCurrencyFormat(transaction.amount_charged));
//                ((ViewHolder)holder).infoText.setText(transaction.notes);
            }
//            if(transaction.status!=null){
//                Log.i("kunam", transaction.status);
//            }else{
//                Log.i("kunam", transaction.status_label);
//            }
            if (transaction.status_label != null) {
                if (transaction.status_label.equalsIgnoreCase("waiting payment") || transaction.status_label.equalsIgnoreCase("on process")) {
                    ((ViewHolder) holder).statusText.setText("MENUNGGU");
                    ((ViewHolder) holder).statusText.setTextColor(Color.parseColor("#0080BB"));
                } else if (transaction.status_label.equalsIgnoreCase("success")) {
                    ((ViewHolder) holder).statusText.setText("BERHASIL");
                    ((ViewHolder) holder).statusText.setTextColor(Color.parseColor("#2CC013"));
                } else {
                    ((ViewHolder) holder).statusText.setText("GAGAL");
                    ((ViewHolder) holder).statusText.setTextColor(Color.parseColor("#A31627"));
                }
            } else {
                if (transaction.status.equalsIgnoreCase(Constant.TOPUP_STATUS_SUCCESS)) {
                    ((ViewHolder)holder).statusText.setText("BERHASIL");
                    ((ViewHolder)holder).statusText.setTextColor(Color.parseColor("#2CC013"));
                }/* else if (transaction.status.equalsIgnoreCase(Constant.TOPUP_STATUS_REJECT)) {
        holder.statusText.setText("GAGAL");
        holder.statusText.setTextColor(Color.parseColor("#A31627"));
        } */else {
                    ((ViewHolder)holder).statusText.setText("MENUNGGU");
                    ((ViewHolder)holder).statusText.setTextColor(Color.parseColor("#0080BB"));
                }
            }


            String[] dateTime = MethodUtil.formatDateAndTime(transaction.created_at);
            ((ViewHolder)holder).timeText.setText(dateTime[0] + " " + dateTime[1]);



            RxView.clicks(((ViewHolder)holder).container).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (onListClicked != null) {
                        onListClicked.listClick(transaction);
                    }
                }
            });
        } else if (holder instanceof ViewHolderLoadMore) {
            if (mListener != null) {
                mListener.onLoadMoreList();
            }
        }
    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return transactions.get(position) != null ? ITEM_TYPE.ITEM_TYPE_TRANSACTION.ordinal() :
                ITEM_TYPE.ITEM_TYPE_LOADMORE.ordinal();
    }

    public RecyFeedTrxAdapter(ListActionLoadMore mListener, boolean isPPOB) {
        transactions = new ArrayList<>();
        this.isPPOB = isPPOB;
        this.mListener = mListener;
    }

    public void setListenerOnClick(OnListClicked listenerOnClick) {
        onListClicked = listenerOnClick;
    }

    public void add(List<GTransaction> transactionList) {
        transactions = transactionList;
        notifyDataSetChanged();
    }

    public void addAll(List<GTransaction> transactionList) {
        transactions.addAll(transactionList);
        notifyDataSetChanged();
    }

    public void removeLoadingList() {
        transactions.remove(transactions.size() - 1);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconProduct;
        private TextView topupText;
        private TextView paketText;
//        private TextView infoText;
        private TextView statusText;
//        private TextView dateText;
        private TextView timeText;
        private LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            iconProduct = (ImageView) itemView.findViewById(R.id.icon_transaction);
            topupText = (TextView) itemView.findViewById(R.id.transaction_id);
            paketText = (TextView) itemView.findViewById(R.id.amount_transaction);
//            infoText = (TextView) itemView.findViewById(R.id.info_transaction);
            statusText = (TextView) itemView.findViewById(R.id.status_transaction);
//            dateText = (TextView) itemView.findViewById(R.id.date_transaction);
            timeText = (TextView) itemView.findViewById(R.id.time_transaction);
            container = (LinearLayout) itemView.findViewById(R.id.container_transaction);
        }
    }

    class ViewHolderLoadMore extends RecyclerView.ViewHolder {
        ViewHolderLoadMore(View view) {
            super(view);
        }
    }

    public interface OnListClicked{
        void listClick(GTransaction transaction);
    }

}
