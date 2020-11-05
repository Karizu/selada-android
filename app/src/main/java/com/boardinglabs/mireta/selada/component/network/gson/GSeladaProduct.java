
package com.boardinglabs.mireta.selada.component.network.gson;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GSeladaProduct implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("provider_id")
    @Expose
    private Integer providerId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("biller_inq_cmd")
    @Expose
    private String biller_inq_cmd;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("provider")
    @Expose
    private GSeladaProvider provider;
    private final static long serialVersionUID = -2208778279370224634L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBiller_inq_cmd() {
        return biller_inq_cmd;
    }

    public void setBiller_inq_cmd(String biller_inq_cmd) {
        this.biller_inq_cmd = biller_inq_cmd;
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

    public GSeladaProvider getProvider() {
        return provider;
    }

    public void setProvider(GSeladaProvider provider) {
        this.provider = provider;
    }

}
