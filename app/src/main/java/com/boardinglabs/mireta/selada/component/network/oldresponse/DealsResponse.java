package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GDeals;
import com.boardinglabs.mireta.selada.component.network.gson.GPagination;

import java.util.List;

/**
 * Created by Dhimas on 4/26/18.
 */

public class DealsResponse {
    public List<GDeals> items;
    public GPagination pagination;
}
