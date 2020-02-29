package com.bhsoft.doctor.apisetup;

import com.bhsoft.doctor.model.Account;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostDataServer {
    @FormUrlEncoded
    @POST("/login.php")
    Call<Account> getAccount(@Field("phoneNumber") String phoneNumber);

    @FormUrlEncoded
    @POST("/register.php")
    Call<String> registerAccount(@Field("phoneNumber")  String phoneNumber,
                                 @Field("password")     String password,
                                 @Field("deviceId")     String deviceId,
                                 @Field("name")         String name);
}
