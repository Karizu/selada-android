package com.boardinglabs.mireta.selada.modul.master.brand;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.boardinglabs.mireta.selada.BaseActivity;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.modul.master.brand.adapter.BrandAdapter;
import com.boardinglabs.mireta.selada.modul.master.brand.model.Categories;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandActivity extends BaseActivity {

    private List<Categories> categories;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_brand;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("MEREK");

        setDummyData();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDummyData();
            }
        });

    }

    @Override
    protected void onCreateAtChild() {

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    private void setDummyData(){
        swipeRefresh.setRefreshing(true);
        categories = new ArrayList<>();

        Categories categories1 = new Categories("1", "Merek 1", "1", "0");
        categories.add(categories1);
        Categories categories2 = new Categories("2", "Merek 2", "1", "0");
        categories.add(categories2);
        Categories categories3 = new Categories("3", "Merek 3", "1", "0");
        categories.add(categories3);
        Categories categories4 = new Categories("4", "Merek 4", "1", "0");
        categories.add(categories4);

        BrandAdapter adapter = new BrandAdapter(categories, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setRefreshing(false);
    }
}
