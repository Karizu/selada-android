package com.boardinglabs.mireta.selada.component.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiSelada {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .client(NetworkManager.client())
                .baseUrl(NetworkService.SELADA_SEC_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static NetworkService apiInterface() {
        return builder(NetworkService.class);
    }

}
