package com.boardinglabs.mireta.selada.component.network.response;

public class ApiResponse<T> {
    private String message;
    private String error;
    private T available_sort;
    private T data;
    private String token;
    private String access_token;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getAvailable_sort() {
        return available_sort;
    }

    public void setAvailable_sort(T available_sort) {
        this.available_sort = available_sort;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
