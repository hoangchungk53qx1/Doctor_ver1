package com.bhsoft.doctor.apisetup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://medicalschedule.000webhostapp.com/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()  // Bắt dữ liệu trả về bên này do bên PostDataServer dùng trực tiếp Call<Account>
                    .setLenient()
                    .create();
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
