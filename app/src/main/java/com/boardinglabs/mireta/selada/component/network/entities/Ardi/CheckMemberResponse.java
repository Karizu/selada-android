
package com.boardinglabs.mireta.selada.component.network.entities.Ardi;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckMemberResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("opt_1")
    @Expose
    private String opt1;
    @SerializedName("opt_2")
    @Expose
    private String opt2;
    @SerializedName("opt_3")
    @Expose
    private Object opt3;
    @SerializedName("opt_4")
    @Expose
    private Object opt4;
    @SerializedName("opt_5")
    @Expose
    private Object opt5;
    private final static long serialVersionUID = 6185931782370147721L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOpt1() {
        return opt1;
    }

    public void setOpt1(String opt1) {
        this.opt1 = opt1;
    }

    public String getOpt2() {
        return opt2;
    }

    public void setOpt2(String opt2) {
        this.opt2 = opt2;
    }

    public Object getOpt3() {
        return opt3;
    }

    public void setOpt3(Object opt3) {
        this.opt3 = opt3;
    }

    public Object getOpt4() {
        return opt4;
    }

    public void setOpt4(Object opt4) {
        this.opt4 = opt4;
    }

    public Object getOpt5() {
        return opt5;
    }

    public void setOpt5(Object opt5) {
        this.opt5 = opt5;
    }

}
