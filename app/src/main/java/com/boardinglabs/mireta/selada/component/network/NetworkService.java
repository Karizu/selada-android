package com.boardinglabs.mireta.selada.component.network;

import com.boardinglabs.mireta.selada.component.network.entities.AppVersion;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Booths;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.HistoryTopup.HistoryTopup;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Trx.TransactionArdi;
import com.boardinglabs.mireta.selada.component.network.entities.Ardi.Users;
import com.boardinglabs.mireta.selada.component.network.entities.CategoriesResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Items.Category;
import com.boardinglabs.mireta.selada.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.selada.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionStatus;
import com.boardinglabs.mireta.selada.component.network.entities.TransactionToCashier;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProduct;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaProvider;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;
import com.boardinglabs.mireta.selada.component.network.response.ApiResponse;
import com.boardinglabs.mireta.selada.component.network.response.ItemsResponse;
import com.boardinglabs.mireta.selada.component.network.response.LoginResponse;
import com.boardinglabs.mireta.selada.component.network.response.SPIInquiryResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionListResponse;
import com.boardinglabs.mireta.selada.component.network.response.SeladaTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.response.TransactionListResponse;
import com.boardinglabs.mireta.selada.modul.master.brand.model.Categories;
import com.boardinglabs.mireta.selada.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.selada.modul.master.laporan.model.LaporanResponse;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.Item;
import com.boardinglabs.mireta.selada.modul.master.stok.inventori.model.ItemResponse;
import com.google.gson.JsonObject;
import com.boardinglabs.mireta.selada.component.network.gson.GAgent;
import com.boardinglabs.mireta.selada.component.network.gson.GBalance;
import com.boardinglabs.mireta.selada.component.network.gson.GBanks;
import com.boardinglabs.mireta.selada.component.network.gson.GCard;
import com.boardinglabs.mireta.selada.component.network.gson.GCashback;
import com.boardinglabs.mireta.selada.component.network.gson.GCashbackRedeem;
import com.boardinglabs.mireta.selada.component.network.gson.GCreditCard;
import com.boardinglabs.mireta.selada.component.network.gson.GLogo;
import com.boardinglabs.mireta.selada.component.network.gson.GMerchant;
import com.boardinglabs.mireta.selada.component.network.gson.GPromo;
import com.boardinglabs.mireta.selada.component.network.gson.GRedeem;
import com.boardinglabs.mireta.selada.component.network.gson.GReward;
import com.boardinglabs.mireta.selada.component.network.gson.GTopup;
import com.boardinglabs.mireta.selada.component.network.oldresponse.AgentResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.BankTransferResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.CalculateResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.CashbackResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.CreditCardResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.DealsResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MerchantResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.ParkingResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.PostsResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.PromoResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.ServicesResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.SinglePostResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.SyncContactResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TopupResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionHistoryResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionParkingResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransferRequestLogGroupResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransferRequestLogResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.TransferRequestResponse;
import com.boardinglabs.mireta.selada.component.network.oldresponse.VoucherResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface NetworkService {
    String BASE_NEW_URL_LOCAL = "http://36.94.58.180/api/pos/public/index.php/api/";
    String SELADA_SEC_URL = "http://36.94.58.180/api/core/public/index.php/api/";
    String BASE_ARDI = "http://36.94.58.180/ardi-api/public/api/";

    @FormUrlEncoded
    @POST("customers/auth")
    Observable<AgentResponse> login(@Field("mobile") String mobile,
                                    @Field("passcode") String passcode,
                                    @Field("device_active") String imei);

    @GET("customers/balance")
    Observable<GBalance> getBalance();

    @FormUrlEncoded
    @POST("premiums/subscribe")
    Observable<MessageResponse> subscribePremium(@Field("referral_id") String refferalId);

    @GET("services")
    Observable<ServicesResponse> getService(@Query("type") String type,
                                            @Query("amount") String amount,
                                            @Query("customer_no") String customerNo,
                                            @Query("category") String cat,
                                            @Header("Authorization") String token,
                                            @Header("user-key") String rToken,
                                            @Header("user-key-gen") String keyGen);

    @GET("services")
    Observable<com.boardinglabs.mireta.selada.component.network.response.ServicesResponse>
    getSeladaService(@Query("category_id") String cat,
                     @Query("provider_id") String provider,
                     @Header("Authorization") String token,
                     @Header("user-key") String rToken,
                     @Header("user-key-gen") String keyGen,
                     @Header("key-code") String key);

//    @Headers("Content-Type: text/plain")
//    @POST("transactions/checkInquiry")
//    Observable<SPIInquiryResponse> spiCMDInquiry(@Body String body,
//                                                 @Header("Authorization") String token,
//                                                 @Header("user-key") String rToken,
//                                                 @Header("user-key-gen") String keyGen);

    @FormUrlEncoded
    @POST("staging/transactions/checkInquiry")
    Observable<SPIInquiryResponse> spiCMDInquiry(@Query("cmd") String cmd, @Query("nop") String nop, @Query("voc") String voc,
                                                 @Field("cmd") String cmdField, @Field("nop") String nopField, @Field("voc") String vocField,
                                                 @Header("Authorization") String token,
                                                 @Header("user-key") String rToken,
                                                 @Header("user-key-gen") String keyGen,
                                                 @Header("key-code") String key);

    @FormUrlEncoded
    @POST("staging/transactions/checkInquiry")
    Observable<ResponseBody> spiCMDInquiryResponse(@Query("cmd") String cmd, @Query("nop") String nop, @Query("voc") String voc,
                                                   @Field("cmd") String cmdField, @Field("nop") String nopField, @Field("voc") String vocField,
                                                   @Header("Authorization") String token,
                                                   @Header("user-key") String rToken,
                                                   @Header("user-key-gen") String keyGen,
                                                   @Header("key-code") String key);

//    @Headers("Content-Type: text/plain")
//    @POST("transactions/checkInquiry")
//    Observable<SPIInquiryResponse> spiCMDInquiryBPJS(@Body String body,
//                                                     @Header("Authorization") String token,
//                                                     @Header("user-key") String rToken,
//                                                     @Header("user-key-gen") String keyGen);

    @FormUrlEncoded
    @POST("staging/transactions/checkInquiry")
    Observable<SPIInquiryResponse> spiCMDInquiryBPJS(@Query("cmd") String cmd, @Query("nop") String nop, @Query("bln") String voc,
                                                     @Field("cmd") String cmdField, @Field("nop") String nopField, @Field("bln") String vocField,
                                                     @Header("Authorization") String token,
                                                     @Header("user-key") String rToken,
                                                     @Header("user-key-gen") String keyGen,
                                                     @Header("key-code") String key);

    @FormUrlEncoded
    @POST("transactionsppob/pay")
    Observable<TransactionResponse> payTransaction(@Field("transaction_id") String transactionId, @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("transactions")
    Observable<SeladaTransactionResponse> createSeladaTransaction(
            @Field("service_id") String service_id,
            @Field("merchant_id") String merchant_id,
            @Field("merchant_no") String merchant_no,
            @Field("price") String price,
            @Field("vendor_price") String vendor_price,
            @Field("note") String note,
            @Header("Authorization") String token,
            @Header("key-code") String key);

    @FormUrlEncoded
    @POST("staging/transactions")
    Call<SeladaTransactionResponse> createSeladaTrx(
            @Field("service_id") String service_id,
            @Field("merchant_id") String merchant_id,
            @Field("merchant_no") String merchant_no,
            @Field("price") String price,
            @Field("vendor_price") String vendor_price,
            @Field("note") String note,
            @Field("stan") String stan,
            @Header("Authorization") String token,
            @Header("user-key") String rToken,
            @Header("user-key-gen") String keyGen,
            @Header("key-code") String key);

    @FormUrlEncoded
    @POST("transactionsppob/inquiry")
    Observable<TransactionResponse> setInquiry(@Field("customer_no") String customerNo,
                                               @Field("service_id") String serviceId,
                                               @Field("amount") String amount,
                                               @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/transactionsppob/updateamount")
    Observable<TransactionResponse> updateAmountInquiry(@Field("transaction_id") String transactionId,
                                                        @Field("amount") String amount);

    @FormUrlEncoded
    @POST("transactionsppob")
    Observable<TransactionResponse> getTransactionWithoutInquiry(@Field("customer_no") String customerNo,
                                                                 @Field("service_id") String serviceId,
                                                                 @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("transactions/calculate_charge")
    Observable<CalculateResponse> calculateAmount(@Field("amount") String amount,
                                                  @Field("card_type") String cardType,
                                                  @Field("merchant_id") String merchantId);

    @FormUrlEncoded
    @POST("deals/redeem")
    Observable<JsonObject> redeem(@Field("deal_id") String dealId);

    @FormUrlEncoded
    @PUT("customers/logout")
    Observable<JsonObject> logout(@Field("customer_id") String costumerId);

    @FormUrlEncoded
    @POST("topup/request")
    Observable<TransferRequestResponse> request(@Field("to_customer_id") String to_customer_id,
                                                @Field("amount") String amount);

/////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////


    @GET("transactions/{transaction_id}")
    Observable<SeladaTransactionResponse> getSeladaTransactionDetail(@Path("transaction_id") String transaction_id,
                                                                     @Header("Authorization") String token,
                                                                     @Header("key-code") String key);

    @GET("transactions")
    Observable<SeladaTransactionListResponse> getSeladaTransactionHistory(@Query("merchant_id") String merchant_id,
                                                                          @Header("Authorization") String token,
                                                                          @Header("key-code") String key);

    @FormUrlEncoded
    @POST("auth/login")
    Observable<LoginResponse> loginBusiness(@Field("username") String username,
                                            @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> loginMireta(@Field("username") String username,
                                    @Field("password") String password);

    @GET("categories")
    Call<ApiResponse<List<Category>>> getListCategory(@Header("Authorization") String token);


    @GET("transactions")
    Observable<TransactionListResponse> getTransactions(@Query("stock_location_id") String stockLocationId,
                                                        @Query("with_details") boolean withDetails,
                                                        @Header("Authorization") String token);

    @GET("stocks")
    Observable<ItemsResponse> getStockItems(@Query("location_id") String location_id,
                                            @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("transactions")
    Observable<ResponseBody> createTransaction(@Body TransactionPost transactionPost,
                                               @Header("Authorization") String token);

    @POST("transactions")
    Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.TransactionResponse>> createTransactions(@Body TransactionPost transactionPost,
                                                                                                                            @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("transaction")
    Call<ApiResponse<com.boardinglabs.mireta.selada.modul.transactions.items.pembayaran.model.TransactionResponse>> createTransactionToCashier(@Body TransactionToCashier transactionToCashier,
                                                                                                                                               @Header("Authorization") String token);

    @GET("transactions")
    Observable<List<com.boardinglabs.mireta.selada.component.network.entities.TransactionResponse>> getLastTransactionNow(@Query("status") String status,
                                                                                                                          @Query("business_id") String business_id,
                                                                                                                          @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>>> getLastTransaction(@Query("status") String status,
                                                                                                                           @Query("location_id") String location_id,
                                                                                                                           @Query("date") String order_date,
                                                                                                                           @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>>> getMonthTransaction(@Query("status") String status,
                                                                                                                            @Query("location_id") String location_id,
                                                                                                                            @Query("month") String month,
                                                                                                                            @Query("year") String year,
                                                                                                                            @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>>> getHistory(@Query("location_id") String location_id,
                                                                                                                   @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>>> getHistoryToday(@Query("location_id") String location_id,
                                                                                                                        @Query("status") String status,
                                                                                                                        @Query("date") String order_date,
                                                                                                                        @Header("Authorization") String token);

    @GET("transactions/{transaction_id}")
    Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>> getDetailTransaction(@Path("transaction_id") String transaction_id,
                                                                                                                       @Header("Authorization") String token);

    @GET("stocks")
    Call<ApiResponse<List<StockResponse>>> getKatalogStok(@Query("location_id") String location_id,
                                                          @Query("item_id") String item_id,
                                                          @Header("Authorization") String token);

    @GET("items")
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>>> getListKatalog(@Query("business_id") String business_id,
                                                                                                                         @Header("Authorization") String token);

    @GET("categories/{categories_id}")
    Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.Items.ItemResponse>> getDetailKategori(@Path("categories_id") String categories_id,
                                                                                                                      @Header("Authorization") String token);


    @POST("items")
    Call<ApiResponse<ItemResponse>> createItem(@Body RequestBody requestBody,
                                               @Header("Authorization") String token);

    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> updateKategori(@Path("id") String id,
                                                   @Body RequestBody requestBody,
                                                   @Header("Authorization") String token);

    @GET("categories")
    Call<ApiResponse<List<CategoryModel>>> getListCategory(@Query("business_id") String business_id,
                                                           @Query("category_id") String id_category_nested,
                                                           @Header("Authorization") String token);


    @POST("items/{id}")
    Call<ApiResponse> postImageItem(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories")
    Call<ApiResponse> postCategories(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories")
    Call<ApiResponse<CategoriesResponse>> postCategoriesInCreateItem(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories/{id}")
    Call<ApiResponse<CategoryModel>> kelolaCategories(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @DELETE("categories/{id}")
    Call<ApiResponse<CategoryModel>> deleteCategories(@Path("id") String id, @Header("Authorization") String token);


    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> showIsDailyStok(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> updateStok(@Path("id") String id, @Body Item item, @Header("Authorization") String token);

    @GET("laporan")
    Call<ApiResponse<List<LaporanResponse>>> getLaporanStock(@Query("stock_location_id") String stock_location_id,
                                                             @Header("Authorization") String token);

    // New API

    @POST("auth/regBusiness")
    Call<ApiResponse> registerUser(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);

    @POST("locations/{location_id}")
    Call<ApiResponse> updateProfilToko(@Path("location_id") String location_id, @Body RequestBody requestBody,
                                       @Header("Authorization") String token);

    @POST("locations/{id}")
    Call<ApiResponse<DetailLocationResponse>> getDetailLocation(@Path("id") String id,
                                                                @Header("Authorization") String token);

    @DELETE("items/{id}")
    Call<ApiResponse> deleteItems(@Path("id") String id,
                                  @Header("Authorization") String token);

    @POST("items/{id}")
    Call<ApiResponse> updateItem(@Path("id") String id, @Body RequestBody requestBody,
                                 @Header("Authorization") String token);

    @POST("stocks")
    Call<ApiResponse> createStock(@Body RequestBody requestBody,
                                  @Header("Authorization") String token);

    @POST("stocks/{stock_id}")
    Call<ApiResponse> updateStock(@Path("stock_id") String stock_id,
                                  @Body RequestBody requestBody,
                                  @Header("Authorization") String token);

    @POST("auth/changePassword")
    Call<ApiResponse> changePassword(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);

    @GET("transactions/report")
    Call<ApiResponse> getReport(@Query("is_settled") String is_settled,
                                @Query("location_id") String location_id,
                                @Header("Authorization") String token);

    @POST("auth/forgot")
    Call<ApiResponse> postForgotPassword(@Body RequestBody requestBody);

    @POST("auth/recover")
    Call<ApiResponse> postRecoverPassword(@Body RequestBody requestBody);

    @GET("members/{member_id}")
    Call<ApiResponse<Members>> cekSaldo(@Path("member_id") String member_id,
                                        @Header("Authorization") String token);

    @POST("transactions")
    Call<ApiResponse<TransactionArdi>> postTransactionArdi(@Body RequestBody requestBody,
                                                           @Header("Authorization") String token);

    @POST("auth/login")
    Call<ApiResponse<com.boardinglabs.mireta.selada.component.network.entities.LoginResponse>> loginArdi(@Body RequestBody requestBody);


    @POST("topups")
    Call<ApiResponse<Topup>> doTopup(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);

    @GET("booths")
    Call<ApiResponse<List<Booths>>> getListBooth(@Header("Authorization") String token);

    @GET("users")
    Call<ApiResponse<List<Users>>> getListUsers(@Header("Authorization") String token);

    @GET("topups")
    Call<ApiResponse<List<HistoryTopup>>> getListTopup(@Query("booth_id") String booth_id,
                                                       @Header("Authorization") String token);

    @GET("topups")
    Call<ApiResponse<List<HistoryTopup>>> getListTopupSelada(@Query("member_id") String member_id,
                                                             @Header("Authorization") String token);

    @GET("topups/{id}")
    Call<ApiResponse<Topup>> getDetailTopup(@Path("id") String topup_id,
                                            @Header("Authorization") String token);

    @GET("topups")
    Call<ApiResponse<List<HistoryTopup>>> getListTopupWithDate(@Query("booth_id") String booth_id,
                                                               @Query("date") String date,
                                                               @Header("Authorization") String token);

    @POST("booths/{booth_id}/register")
    Call<ApiResponse> updateBoothStatus(@Path("booth_id") String booth_id,
                                        @Body RequestBody requestBody,
                                        @Header("Authorization") String token);

    @POST("reversal")
    Call<ApiResponse> sendReversal(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);

    @POST("transactions/{transaction_id}/void")
    Call<ApiResponse> doVoid(@Path("transaction_id") String trxId,
                             @Header("Authorization") String token);

    @POST("transactions/void")
    Call<ApiResponse> doVoidArdi(@Body RequestBody requestBody,
                                 @Header("Authorization") String token);

    @POST("transactions/checkSettled")
    Call<ApiResponse> doCheckSettled(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);


    @POST("transactions/settlement")
    Call<ApiResponse> doSettlement(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);


    @GET
    Call<ApiResponse<List<com.boardinglabs.mireta.selada.component.network.entities.Trx.Transactions>>> getHistoryWithFilter(@Url String url,
                                                                                                                             @Query("location_id") String location_id,
                                                                                                                             @Header("Authorization") String token);

    @GET
    Call<ApiResponse> getReportByFilter(@Url String url,
                                        @Query("location_id") String location_id,
                                        @Header("Authorization") String token);

    @POST("members")
    Call<ApiResponse> doRegisterMember(@Body RequestBody requestBody,
                                       @Header("Authorization") String token);

    @GET("members/searchCardNumber")
    Call<ApiResponse<CheckMemberResponse>> checkMember(@Query("card_number") String card_number,
                                                       @Header("Authorization") String token);

    @POST("members/exchangeFreeMeal")
    Call<ApiResponse> doCheckFreeMeal(@Body RequestBody requestBody,
                                      @Header("Authorization") String token);

    @GET("versions/lastest")
    Call<ApiResponse<AppVersion>> checkVersion(@Header("Authorization") String token);

    @POST("transactions/checkStatus")
    Call<ApiResponse<TransactionStatus>> doCheckStatus(@Body RequestBody requestBody,
                                                       @Header("Authorization") String token);

    @GET("products")
    Observable<ApiResponse<List<GSeladaProduct>>> getListProductSelada(@Query("provider_id") String provider_id,
                                                                       @Header("Authorization") String token,
                                                                       @Header("key-code") String key);

    @GET("providers")
    Observable<ApiResponse<List<GSeladaProvider>>> getListProviderSelada(@Query("category_id") String category_id,
                                                                         @Header("Authorization") String token,
                                                                         @Header("key-code") String key);
}
