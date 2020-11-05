package com.boardinglabs.mireta.selada.component.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppVersion {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("application_version")
    @Expose
    private String application_version;
    @SerializedName("dashboard_version")
    @Expose
    private String dashboard_version;
    @SerializedName("android_version")
    @Expose
    private String android_version;
    @SerializedName("android_link")
    @Expose
    private String android_link;
    @SerializedName("android_bjb_version")
    @Expose
    private String android_bjb_version;
    @SerializedName("android_bjb_link")
    @Expose
    private String android_bjb_link;
    @SerializedName("database_version")
    @Expose
    private String database_version;
    @SerializedName("api_version")
    @Expose
    private String api_version;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;

    private final static long serialVersionUID = -8641326579664017981L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplication_version() {
        return application_version;
    }

    public void setApplication_version(String application_version) {
        this.application_version = application_version;
    }

    public String getDashboard_version() {
        return dashboard_version;
    }

    public void setDashboard_version(String dashboard_version) {
        this.dashboard_version = dashboard_version;
    }

    public String getAndroid_version() {
        return android_version;
    }

    public void setAndroid_version(String android_version) {
        this.android_version = android_version;
    }

    public String getAndroid_link() {
        return android_link;
    }

    public void setAndroid_link(String android_link) {
        this.android_link = android_link;
    }

    public String getAndroid_bjb_version() {
        return android_bjb_version;
    }

    public void setAndroid_bjb_version(String android_bjb_version) {
        this.android_bjb_version = android_bjb_version;
    }

    public String getAndroid_bjb_link() {
        return android_bjb_link;
    }

    public void setAndroid_bjb_link(String android_bjb_link) {
        this.android_bjb_link = android_bjb_link;
    }

    public String getDatabase_version() {
        return database_version;
    }

    public void setDatabase_version(String database_version) {
        this.database_version = database_version;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }
}
