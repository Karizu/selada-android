
package com.boardinglabs.mireta.selada.component.network.entities.Ardi.HistoryTopup;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryTopup implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
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
    private Integer amount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("amount_unique")
    @Expose
    private int amount_unique;
    @SerializedName("booth")
    @Expose
    private Booth booth;
    @SerializedName("member")
    @Expose
    private Member member;
    @SerializedName("user")
    @Expose
    private User user;
    private final static long serialVersionUID = -974432226282513232L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Booth getBooth() {
        return booth;
    }

    public void setBooth(Booth booth) {
        this.booth = booth;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAmount_unique() {
        return amount_unique;
    }

    public void setAmount_unique(int amount_unique) {
        this.amount_unique = amount_unique;
    }
}
