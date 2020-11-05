package com.boardinglabs.mireta.selada.modul.master.stok.inventori.model;

public class KatalogModel {

    private String id;
    private String image;
    private String name;
    private String deskripsi;
    private String harga;
    private String kategori;
    private String total_qty;
    private String is_daily_stock;
    private String total_today_qty;
    private String date;

    public KatalogModel(String id, String image, String name, String deskripsi, String harga, String total_qty, String is_daily_stock, String total_today_qty, String date, String kategori) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.total_qty = total_qty;
        this.is_daily_stock = is_daily_stock;
        this.total_today_qty = total_today_qty;
        this.date = date;
        this.kategori = kategori;
    }

    public KatalogModel(String id, String image, String name, String deskripsi, String harga, String total_qty, String is_daily_stock) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.total_qty = total_qty;
        this.is_daily_stock = is_daily_stock;
    }

    public KatalogModel(String id, String name, String deskripsi, String harga, String total_qty, String is_daily_stock, String total_today_qty, int i) {
        this.id = id;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.total_qty = total_qty;
        this.is_daily_stock = is_daily_stock;
        this.total_today_qty = total_today_qty;
    }

    public KatalogModel(String id, String name, String deskripsi, String harga, String total_today_qty, String is_daily_stock) {
        this.id = id;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.total_today_qty = total_today_qty;
        this.is_daily_stock = is_daily_stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(String total_qty) {
        this.total_qty = total_qty;
    }

    public String getIs_daily_stock() {
        return is_daily_stock;
    }

    public void setIs_daily_stock(String is_daily_stock) {
        this.is_daily_stock = is_daily_stock;
    }

    public String getTotal_today_qty() {
        return total_today_qty;
    }

    public void setTotal_today_qty(String total_today_qty) {
        this.total_today_qty = total_today_qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
