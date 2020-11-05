package com.boardinglabs.mireta.selada.component.network.response;

import com.boardinglabs.mireta.selada.component.network.entities.Business;
import com.boardinglabs.mireta.selada.component.network.entities.Merchant;
import com.boardinglabs.mireta.selada.component.network.entities.StockLocation;
import com.boardinglabs.mireta.selada.component.network.entities.User;

public class LoginResponse extends BaseResponse{
    public String token;
    public String access_token;
    public User data;
    public Business business;
    public StockLocation user_location;
}
