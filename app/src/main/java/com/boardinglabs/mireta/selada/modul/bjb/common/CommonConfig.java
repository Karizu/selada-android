/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boardinglabs.mireta.selada.modul.bjb.common;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author indra
 */
public class CommonConfig {

    public static final String KONFIRM_UPDATE_URL = "192.168.43.243:8080";


    public static final String HTTP_SSL_PROTOCOL = "https";
    public static final String HTTP_NON_SSL_PROTOCOL = "http";
    public static final String HTTP_PROTOCOL = HTTP_NON_SSL_PROTOCOL;

    public static final String WS_SSL_PROTOCOL = "wss";
    public static final String WS_NON_SSL_PROTOCOL = "ws";
    public static final String WS_PROTOCOL = WS_NON_SSL_PROTOCOL;

    public static final int WS_SSL_PORT = 443;
    public static final int WS_NORMAL_PORT = 80;
    public static final int WS_DEV_PORT = 8000;
    public static final int WS_NON_SSL_PORT = WS_NORMAL_PORT;
    public static final int WS_PORT = WS_SSL_PORT;

    public static final String DEV_IP = "192.168.43.28:8000";
    public static final String DEV_IP_SELADA = "36.94.58.181:8080";//192.168.43.28:8080 192.168.0.7:8080
    public static final String PROD_IP_SELADA = "36.94.58.182:8080";
    public static final String PROD_IP = "edc.bankbjb.co.id";

    public static final String IP = PROD_IP_SELADA;

    public static final String HTTP_REST_URL = IP+"/ARRest";
    public static final String WEBSOCKET_URL = IP+"/tms";
    public static final String HTTP_POST = HTTP_PROTOCOL+ "://"+IP+"/ARRest/api";

    public static final String POST_PATH = "api";

    public static final String SETTINGS_FILE = "settings";

}

