
package com.boardinglabs.mireta.selada.component.network.entities.Ardi;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topup implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("booth_id")
    @Expose
    private String boothId;
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("amount_unique")
    @Expose
    private int amount_unique;
    @SerializedName("expire")
    @Expose
    private String expire;
    @SerializedName("member")
    @Expose
    private Member member;
    private final static long serialVersionUID = -8186810077391587052L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoothId() {
        return boothId;
    }

    public void setBoothId(String boothId) {
        this.boothId = boothId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount_unique() {
        return amount_unique;
    }

    public void setAmount_unique(int amount_unique) {
        this.amount_unique = amount_unique;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }
}
