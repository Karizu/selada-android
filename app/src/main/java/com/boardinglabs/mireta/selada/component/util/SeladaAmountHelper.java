package com.boardinglabs.mireta.selada.component.util;

import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;

public class SeladaAmountHelper {
    public static String convertServiceAmount(GSeladaService services){
        int biller_price = 0;
        if (services.biller_price != null){
            try{
                biller_price = Integer.valueOf(services.biller_price);
            }
            catch (Exception e){

            }
        }
        int markup = 0;
        if (services.biller_price != null){
            try{
                markup = Integer.valueOf(services.markup);
            }
            catch (Exception e){

            }
        }

        return String.valueOf(biller_price + markup);
    }
}
