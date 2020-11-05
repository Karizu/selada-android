package com.boardinglabs.mireta.selada.component.network;

import android.text.TextUtils;

import com.boardinglabs.mireta.selada.component.util.DateHelper;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkLogin {
    public static NetworkService instance;
    public static Retrofit retrofit;

    private static final int CONNECT_TIME_OUT = 300 * 1000;
    private static final int READ_TIME_OUT = 300 * 1000;

    public static synchronized NetworkService getInstance(){
        instance = null;
        final String sessionToken = PreferenceManager.getSessionToken();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
        httpClient.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS);
        httpClient.addNetworkInterceptor(interceptor);

//        final TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return new java.security.cert.X509Certificate[]{};
//                    }
//                }
//        };
//        try {
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//
//            httpClient.sslSocketFactory(sslContext.getSocketFactory())
//                    .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    });
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String input = bodyToString(chain.request().body());
                if (TextUtils.isEmpty(input)) {
                    input = chain.request().url().toString().replace(com.boardinglabs.mireta.selada.component.network.BuildConfig.SELADA_CORE_URL, "");
                } else if (chain.request().url().toString().equals(com.boardinglabs.mireta.selada.component.network.BuildConfig.SELADA_CORE_URL + "/uploads/image")) {
                    input = "ref=customers&type=avatar";
                }

                if (input.equalsIgnoreCase("/cashbacks/redeem")) {
                    input = "";
                }
                String unixTime = String.valueOf(DateHelper.getUnixTime());
                String HMac = "";

                try {
                    if (input.contains("/merchants")) {
                        HMac = MethodUtil.getHMac(input, unixTime, true);
                    } else {
                        HMac = MethodUtil.getHMac(input, unixTime, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HMac = "";
                }

//                Log.i("kunaminput", input);

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "application/pasy.v1+json")
                        .header("content-type", "application/x-www-form-urlencoded")
                        .header("App-ID", "mobile")
                        .header("AccessToken", sessionToken)
                        .header("cache-control", "no-cache")
                        .header("Time", unixTime)
                        .header("Hmac", HMac)
                        .build();

                Response response = chain.proceed(request);

                return response;
            }
        });

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                .build();

        httpClient.connectionSpecs(Collections.singletonList(spec));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SELADA_CORE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();



        instance = retrofit.create(NetworkService.class);
        return instance;
    }

    public static synchronized NetworkService getSeladaCoreInstance(){
        instance = null;
        final String sessionToken = PreferenceManager.getSessionToken();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
        httpClient.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS);
        httpClient.addNetworkInterceptor(interceptor);

//        final TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return new java.security.cert.X509Certificate[]{};
//                    }
//                }
//        };
//        try {
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//
//            httpClient.sslSocketFactory(sslContext.getSocketFactory())
//                    .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    });
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String input = bodyToString(chain.request().body());
                if (TextUtils.isEmpty(input)) {
                    input = chain.request().url().toString().replace(com.boardinglabs.mireta.selada.component.network.BuildConfig.SELADA_CORE_URL, "");
                } else if (chain.request().url().toString().equals(com.boardinglabs.mireta.selada.component.network.BuildConfig.SELADA_CORE_URL + "/uploads/image")) {
                    input = "ref=customers&type=avatar";
                }

                if (input.equalsIgnoreCase("/cashbacks/redeem")) {
                    input = "";
                }
                String unixTime = String.valueOf(DateHelper.getUnixTime());
                String HMac = "";

                try {
                    if (input.contains("/merchants")) {
                        HMac = MethodUtil.getHMac(input, unixTime, true);
                    } else {
                        HMac = MethodUtil.getHMac(input, unixTime, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HMac = "";
                }

//                Log.i("kunaminput", input);

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "application/pasy.v1+json")
                        .header("content-type", "application/x-www-form-urlencoded")
                        .header("App-ID", "mobile")
                        .header("AccessToken", sessionToken)
                        .header("cache-control", "no-cache")
                        .header("Time", unixTime)
                        .header("Hmac", HMac)
                        .build();

                Response response = chain.proceed(request);

                return response;
            }
        });

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                .build();

        httpClient.connectionSpecs(Collections.singletonList(spec));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(com.boardinglabs.mireta.selada.component.network.BuildConfig.SELADA_CORE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();



        instance = retrofit.create(NetworkService.class);
        return instance;
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static OkHttpClient client(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
