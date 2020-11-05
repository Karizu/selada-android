package com.boardinglabs.mireta.selada.component.network.oldresponse;

import com.boardinglabs.mireta.selada.component.network.gson.GCostumer;
import com.boardinglabs.mireta.selada.component.network.gson.GLogo;
import com.boardinglabs.mireta.selada.component.network.gson.GMerchant;
import com.boardinglabs.mireta.selada.component.network.gson.GRate;

/**
 * Created by Dhimas on 4/24/18.
 */

public class ParkingResponse {
    public String id;
    public String ticket_no;
    public String type_vehicle;
    public String amount;
    public String settlement_id;
    public String status;
    public String updated_at;
    public String created_at;
    public String completed_at;
    public GCostumer customer;
    public GMerchant merchant;
    public GRate rate;
    public GLogo qr;
}
