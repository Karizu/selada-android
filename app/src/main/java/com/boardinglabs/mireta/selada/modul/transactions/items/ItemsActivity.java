package com.boardinglabs.mireta.selada.modul.transactions.items;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.adapter.RecyItemsAdapter;
import com.boardinglabs.mireta.selada.component.adapter.RecyOverviewItemsAdapter;
import com.boardinglabs.mireta.selada.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.selada.component.listener.ItemActionListener;
import com.boardinglabs.mireta.selada.component.listener.ListActionListener;
import com.boardinglabs.mireta.selada.component.network.NetworkManager;
import com.boardinglabs.mireta.selada.component.network.NetworkService;
import com.boardinglabs.mireta.selada.component.network.entities.Item;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionToCashier;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.modul.CommonInterface;
import com.boardinglabs.mireta.selada.modul.home.HomeActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.PembayaranActivity;
import com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.pembayaranardi.PembayaranArdiActivity;
import com.google.gson.Gson;
import com.paging.listview.PagingListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;


public class ItemsActivity extends BaseActivity implements ItemsView, CommonInterface, ItemActionListener, ListActionListener {
    private ItemsPresenter mPresenter;


    protected RecyclerView reportRecyclerView;

    private List<Item> items;
    private List<Item> orederditems;
    private List<Item> itemList;
    private PagingListView itemsListView;
    private PagingListView overviewListView;
    private RecyItemsAdapter itemsAdapter;
    private RecyOverviewItemsAdapter overviewItemsAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private int currentPage;
    private int total_post;

    private LinearLayout collapsed_view;
    private LinearLayout expanded_view;
    private ConstraintLayout overview_view;
    private ImageButton bottom_button;
    private TextView total_item;
    private TextView total_price;
    private TextView total_price_overview;
    private TransactionPost transactionPost;
    private List<TransactionPost.Items> transactionItems;
    private TransactionToCashier transactionToCashier;
    private List<TransactionToCashier.Items> itemsList;
    private String listOrder;
    private long mTotalPrice;
    private Context context;

    private boolean expanded = false;


    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.header_view)
    LinearLayout header_view;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ItemsPresenter(this, this);
        context = this;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pick_item;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Tambah Transaksi");
        initComponent();
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new ItemsPresenter(this, this);

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private void initComponent() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            loadTransactionsData();
            pullToRefresh.setRefreshing(false);
        });

        orederditems = new ArrayList<>();
        itemList = new ArrayList<>();

        total_post = 0;
        currentPage = 0;

        itemsAdapter = new RecyItemsAdapter();
        itemsAdapter.setListener(this);

        itemsListView = findViewById(R.id.item_list);
        itemsListView.setAdapter(itemsAdapter);

        itemsListView.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                itemsListView.onFinishLoading(false, null);
            }
        });
        itemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (itemsListView.getChildAt(0) != null) {
                    pullToRefresh.setEnabled(itemsListView.getFirstVisiblePosition() == 0 && itemsListView.getChildAt(0).getTop() == 0);
                }
            }
        });
        loadTransactionsData();


//        btn_bayar = findViewById(R.id.btnBayar);
//        btn_bayar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                onClickBayar();
//                Intent intent = new Intent(ItemsActivity.this, PembayaranActivity.class);
//                Log.d("mTotal", String.valueOf(mTotalPrice));
//                Log.d("listOrder", listOrder);
//                String mTotalPrices = String.valueOf(mTotalPrice);
//                intent.putExtra("total", mTotalPrices);
//                intent.putExtra("json", listOrder);
//                startActivity(intent);
//            }
//        });

        overviewItemsAdapter = new RecyOverviewItemsAdapter();
        overviewListView = findViewById(R.id.item_list_overview);
        overviewListView.setAdapter(overviewItemsAdapter);
        overviewItemsAdapter.setListener(this);


//        private LinearLayout collapsed_view;
//        private LinearLayout expanded_view;
//        private Button bottom_button;
//        private TextView total_item;
//        private TextView total_price;
        collapsed_view =  (LinearLayout) findViewById(R.id.collapsed_view);
        expanded_view =  (LinearLayout) findViewById(R.id.expanded_view);
        overview_view = (ConstraintLayout) findViewById(R.id.overview_view);
        bottom_button = (ImageButton) findViewById(R.id.bottom_button);
        total_item = (TextView) findViewById(R.id.total_item);
        total_price = (TextView) findViewById(R.id.total_price);
        total_price_overview = (TextView) findViewById(R.id.total_price_overview);
        total_item = (TextView) findViewById(R.id.total_item);

        bottom_button.setOnClickListener(view -> {
            if (total_item.getText().toString().equals("0 Item") || total_item.getText().toString().equals("0 x")){
                Toast.makeText(ItemsActivity.this, "Silahkan pesan item yang anda inginkan", Toast.LENGTH_SHORT).show();
            } else {
                expandBottomViewTo(!expanded);
            }
        });
        overview_view.setOnClickListener(view -> expandBottomView(false));
        expandBottomView(false);
    }

    public void showSnackbar(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Stok kosong, silahkan tambah stok terlebih dahulu", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSnackbarQty(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Pesanan melebihi stok", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void updateTotalBottom(){
        long totalQty = 0;
        long totalPrice = 0;
        transactionItems = new ArrayList<>();
        itemsList = new ArrayList<>();

        for (Item orditem:orederditems) {
            String uniqueId = UUID.randomUUID().toString();
            transactionItems.add(new TransactionPost.Items(orditem.id, orditem.order_qty, 0));
            itemsList.add(new TransactionToCashier.Items(uniqueId, orditem.name, orditem.order_qty, orditem.item.getPrice(), "0"));
            totalQty += orditem.order_qty;
            long total_price = (long) (Integer.valueOf(orditem.item.getPrice()) * orditem.order_qty);
            totalPrice += total_price;
        }

        total_item.setText(totalQty + " x");
        total_price.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(totalPrice)));
        total_price_overview.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(totalPrice)));

        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        Log.d("TAG", format);

        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
        String format1 = s1.format(new Date());
        Log.d("TAG", format);

        transactionToCashier = new TransactionToCashier("03023de0-96d7-41e6-a729-93cb9d3c8d32", "7e90a7db-5f29-4721-b93d-7e345a1bacb4", format, format1, "1", "1", totalPrice, "0", "1", itemsList);
        transactionPost = new TransactionPost(loginStockLocation.id, "", 1, 1, 1, "", null, transactionItems);

        Gson gson = new Gson();
        listOrder = gson.toJson(orederditems);
        mTotalPrice = totalPrice;
        String json = gson.toJson(transactionPost);
        String json2 = gson.toJson(transactionToCashier);
        Log.d("TAG JSON listOrder", listOrder + "\n TAG JSON 2: " + json2);
    }

//    private void onClickBayar(){
//        String token = "Bearer "+ PreferenceManager.getSessionToken();
//        Log.d("TAG TOKEN", token);
//        mPresenter.createTransaction(transactionPost, token);
//
//        Api.apiInterface().createTransactionToCashier(transactionToCashier, token).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d("TAG onResponse", response.message());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("TAG onFailure", t.getMessage());
//            }
//        });
//    }

    private void expandBottomView(boolean isExpanded){
        expanded = isExpanded;
        if (!expanded){
            expanded_view.setVisibility(View.GONE);
            collapsed_view.setVisibility(View.VISIBLE);
            overview_view.setVisibility(View.GONE);
        }
        else{
            expanded_view.setVisibility(View.VISIBLE);
            collapsed_view.setVisibility(View.GONE);
            overview_view.setVisibility(View.VISIBLE);
            overviewItemsAdapter.setDataList(orederditems);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);

        }
    }

    private void expandBottomViewTo(boolean isExpanded){
        expanded = isExpanded;
        if (!expanded){
            expanded_view.setVisibility(View.GONE);
            collapsed_view.setVisibility(View.VISIBLE);
            overview_view.setVisibility(View.GONE);
            if (orederditems != null){
                Gson gson = new Gson();
                listOrder = gson.toJson(orederditems);
//                Pembayaran Without ARDI
                Intent intent = new Intent(ItemsActivity.this, PembayaranActivity.class);
//                Pemayaran ARDI
//                Intent intent = new Intent(ItemsActivity.this, PembayaranArdiActivity.class);
                Log.d("mTotal", String.valueOf(mTotalPrice));
                Log.d("listOrder", listOrder);
                String mTotalPrices = String.valueOf(mTotalPrice);
                intent.putExtra("total", mTotalPrices);
                intent.putExtra("json", listOrder);
                startActivity(intent);
            }
        }
        else{
            expanded_view.setVisibility(View.VISIBLE);
            collapsed_view.setVisibility(View.GONE);
            overview_view.setVisibility(View.VISIBLE);
            overviewItemsAdapter.setDataList(orederditems);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);

        }
    }

    private void loadTransactionsData(){
        currentPage = 0;
////        mPresenter.fetchData(currentPage+1);
//        mPresenter.stockItems(loginBusiness.id);
        mPresenter.stockItems(loginStockLocation.id);

    }

    private void loadMorePostsData(){
//        mPresenter.fetchData(currentPage+1);
    }


    private void initEvent() {
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.stockItems(loginBusiness.id);
        mPresenter.stockItems(loginStockLocation.id);
    }


    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }

    @Override
    public void onSuccessGetItems(List<Item> items) {
        itemList = items;
        itemsAdapter.setDataList(items, context);
        itemsListView.setHasMoreItems(false);
    }

    @Override
    public void onSuccessCreateTransaction(ResponseBody responseBody) {
        Log.d("TAG SUKSES", String.valueOf(responseBody));
        Intent intent = new Intent(ItemsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void itemClicked(int position) {
        Item item = (Item) overviewItemsAdapter.itemList.get(position);
        int deletedPos = -1;
        int pos = 0;
        for (Item orditem:itemsAdapter.itemList) {
            if (orditem.id.equalsIgnoreCase(item.id)){
                deletedPos = pos;
                break;
            }
            pos++;
        }
        if (deletedPos != -1){
            itemsAdapter.itemList.get(pos).order_qty = 0;
            itemsAdapter.notifyDataSetChanged();
            overviewItemsAdapter.itemList.remove(position);
            overviewItemsAdapter.notifyDataSetChanged();
        }
        updateTotalBottom();
    }

    @Override
    public void itemDeleted(int position) {
        Item item = itemsAdapter.itemList.get(position);
        int deletedPos = -1;
        int pos = 0;
        for (Item orditem:orederditems) {
            if (orditem.id.equalsIgnoreCase(item.id)){
                deletedPos = pos;
                break;
            }
            pos++;
        }
        if (deletedPos != -1){
//            overviewItemsAdapter.itemList.remove(smansa);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);
        }
        updateTotalBottom();
    }

    @Override
    public void itemAdd(int position) {
        Item item = (Item) itemsAdapter.itemList.get(position);
        int found = -1;
        int pos = 0;
        for (Item orditem:orederditems) {
            if (orditem.id.equalsIgnoreCase(item.id)){
                found = pos;
                break;
            }
            pos++;
        }
        if (found != -1){
            orederditems.get(pos).order_qty = item.order_qty;
            itemsAdapter.notifyDataSetChanged();
        }
        else{
            orederditems.add(item);
        }
        updateTotalBottom();
    }

    @Override
    public void itemMinus(int position) {
        Item item = (Item) itemsAdapter.itemList.get(position);
        int found = -1;
        int pos = 0;
        for (Item orditem:orederditems) {
            if (orditem.id.equalsIgnoreCase(item.id)){
                found = pos;
                break;
            }
            pos++;
        }
        if (found != -1){
            orederditems.get(pos).order_qty = item.order_qty;
            itemsAdapter.notifyDataSetChanged();
        }
        else{
            orederditems.add(item);
        }
        updateTotalBottom();
    }

    private void openOverviewView(){
        overviewItemsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(overviewListView);

        overviewListView.setHasMoreItems(false);
    }
    void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @OnClick(R.id.imgClose)
    void onCLickClose2(){
        etSearch.setText("");
        item_name.setVisibility(View.VISIBLE);
        imgSearch.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgSearch)
    void onClickSearch2(){
        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Item> newWorker = new ArrayList<>();
                String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                for (Item user : itemList) {
                    if (user.getName().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(user);
                    }
                }
                if (newWorker.size() >= 1){
                    itemsAdapter.setDataList(newWorker, context);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
