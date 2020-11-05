package com.boardinglabs.mireta.selada.component.network.entities;

public class LoginResponse {
    public String id;
    public String first_name;
    public String last_name;
    public String fullname;
    public String username;
    public String email;
    public boolean user_is_active;
    public boolean user_is_block;
    public String user_last_login;
    public boolean user_change_password;
    public boolean user_is_pristine;
    public Business business;
    public StockLocation user_location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUser_is_active() {
        return user_is_active;
    }

    public void setUser_is_active(boolean user_is_active) {
        this.user_is_active = user_is_active;
    }

    public boolean isUser_is_block() {
        return user_is_block;
    }

    public void setUser_is_block(boolean user_is_block) {
        this.user_is_block = user_is_block;
    }

    public String getUser_last_login() {
        return user_last_login;
    }

    public void setUser_last_login(String user_last_login) {
        this.user_last_login = user_last_login;
    }

    public boolean isUser_change_password() {
        return user_change_password;
    }

    public void setUser_change_password(boolean user_change_password) {
        this.user_change_password = user_change_password;
    }

    public boolean isUser_is_pristine() {
        return user_is_pristine;
    }

    public void setUser_is_pristine(boolean user_is_pristine) {
        this.user_is_pristine = user_is_pristine;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public StockLocation getUser_location() {
        return user_location;
    }

    public void setUser_location(StockLocation user_location) {
        this.user_location = user_location;
    }
}
