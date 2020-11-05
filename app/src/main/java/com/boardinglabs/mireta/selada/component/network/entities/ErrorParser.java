package com.boardinglabs.mireta.selada.component.network.entities;

public class ErrorParser {
    public Boolean status;
    public String error;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
