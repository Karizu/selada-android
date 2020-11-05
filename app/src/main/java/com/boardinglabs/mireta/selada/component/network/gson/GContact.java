package com.boardinglabs.mireta.selada.component.network.gson;

public class GContact {
    private String name;
    private String contact;
    private boolean isSelected = false;

    public GContact(String name, String contact){
        this.name = name;
        this.contact = contact;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
