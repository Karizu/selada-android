package com.boardinglabs.mireta.selada.modul.master.brand.model;

public class Categories {

    private String id;
    private String name;
    private String id_category_nested;
    private String business_id;

    public Categories(String id, String name, String id_category_nested, String business_id) {
        this.id = id;
        this.name = name;
        this.id_category_nested = id_category_nested;
        this.business_id = business_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_category_nested() {
        return id_category_nested;
    }

    public void setId_category_nested(String id_category_nested) {
        this.id_category_nested = id_category_nested;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }
}
