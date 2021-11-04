package com.taxialaan.app.Api;


import com.taxialaan.app.Api.request.LoginRequest;
import com.taxialaan.app.Api.request.RegisterRequest;
import com.taxialaan.app.Api.response.Default;
import com.taxialaan.app.Api.response.LoginResponse;
import com.taxialaan.app.Api.response.OTPResponse;
import com.taxialaan.app.Api.response.Profile;
import com.taxialaan.app.Api.response.ResponseCheckPushy;
import com.taxialaan.app.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.app.Api.response.TransactionItem;
import com.taxialaan.app.Api.response.UpdateResponse;
import com.taxialaan.app.Api.response.UserWalletDetail;
import com.taxialaan.app.Api.response.VerifyOTP;
import com.taxialaan.app.Api.response.WalletPayment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/user/charge_codes/apply")
    Call<Default> chargeCode(@Field("charge_code") String charge_code);


    @FormUrlEncoded
    @POST("api/user/transfer")
    Call<Default> transferCharge(@Field("wallet_id") int wallet_id, @Field("amount") int amount);


    @POST("api/user/reports/transactions")
    Call<List<TransactionItem>> getTransactionList();

    @FormUrlEncoded
    @POST("api/user/wallet/details")
    Call<UserWalletDetail> getUserWalletDetail(@Field("wallet_id") int wallet_id);


    @FormUrlEncoded
    @POST("api/user/payment")
    Call<WalletPayment> walletPayment(@Field("request_id") String request_id);

    @GET("api/user/details")
    Call<Profile> getProfile();

    @FormUrlEncoded
    @POST("api/user/verify")
    Call<OTPResponse> getOTP(@Field("mobile") String mobile_number);

    @FormUrlEncoded
    @POST("api/user/verify/pin")
    Call<VerifyOTP> verifyOTP(@Field("otp") String otp, @Field("request_id") String request_id);

    @POST("api/user/login")
    Call<LoginResponse> Login(@Body LoginRequest request);

    @POST("api/user/register")
    Call<LoginResponse> Register(@Body RegisterRequest request);


    @FormUrlEncoded
    @POST("api/update/check/android")
    Call<UpdateResponse> updateVersionApp(@Field("app") String app);

    @GET("api/user/push_token/check")
    Call<ResponseCheckPushy> checkPushy();


    @FormUrlEncoded
    @POST("api/user/push_token/update")
    Call<ResponseUpdateTokenPushy> updateTokenPushy(@Field("token") String token);
}
