
package com.boardinglabs.mireta.selada.component.network.entities;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGroup implements Serializable
{

    @SerializedName("user_group")
    @Expose
    private List<UserGroup_> userGroup = null;
    private final static long serialVersionUID = 2056488004938461772L;

    public List<UserGroup_> getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(List<UserGroup_> userGroup) {
        this.userGroup = userGroup;
    }

}
