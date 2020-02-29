package com.bhsoft.doctor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bhsoft.doctor.config.AppConfig;
import com.bhsoft.doctor.config.Constants;
import com.bhsoft.doctor.model.Account;
import com.google.gson.Gson;

public class AccountUtils {

    private static AccountUtils accountUtils;
    private SharedPreferences preferences;

    public static AccountUtils getInstance(Context context){
        if (accountUtils == null){
            accountUtils = new AccountUtils(context);
        }
        return accountUtils;
    }

    private AccountUtils(Context context) { // sin
        // Đoạn đấy mình đặt tên đơn giản cũng đc, mà mình viết thế cho nó ngầu thôi =)))
        // Oke
        preferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    }

    public void setAccount(Account account){
        // Đoạn này mình sẽ lưu đối tượng Account vào SharedPreferences
        // Nhưng SharedPreferences nó ko cho lưu trực tiếp 1 object, nên mình dùng gson để chuyển 1 object thành 1 chuỗi json

        preferences.edit()

                .putString(Constants.KEY_ACCOUNT,account.toString())
                .apply();
    }

    public Account getAccount(){
        // Đoạn này mình sẽ lấy chuỗi json đã lưu từ SharedPreferences ra rồi chuyển sang đối tượng object
        String jsonAccount = preferences.getString(Constants.KEY_ACCOUNT,null);

        if (jsonAccount == null){
            return null;
        }
        return new Gson().fromJson(jsonAccount,Account.class);
    }
}
