package com.boardinglabs.mireta.selada.component.network.entities;

import java.util.List;

public class User extends BaseEntity{
    public String first_name;
    public String last_name;
    public String fullname;
    public String username;
    public String email;
    public boolean is_user_mireta;
    public String ref_ardi;
    public boolean user_is_active;
    public boolean user_is_block;
    public String user_last_login;
    public boolean user_change_password;
    public boolean user_is_pristine;
    public Business business;
    public StockLocation user_location;
    public Merchant merchant;
    public List<UserGroup_> user_group;
}
