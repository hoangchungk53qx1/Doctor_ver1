package com.bhsoft.doctor.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhsoft.doctor.R;
import com.bhsoft.doctor.apisetup.PostDataServer;
import com.bhsoft.doctor.apisetup.RetrofitConfig;
import com.bhsoft.doctor.config.AppConfig;
import com.bhsoft.doctor.config.Constants;
import com.bhsoft.doctor.model.Account;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
EditText edtPhoneNumber,edtPassword,edtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword    = findViewById(R.id.edtPassword);
        edtName        = findViewById(R.id.edtName);
    }

    public void onClickMainRegister(View view) {
        String phoneNumber = edtPhoneNumber.getText().toString();
        String password    = edtPassword.getText().toString();
        String name        = edtName.getText().toString();
        if(phoneNumber.isEmpty()){
            Toast.makeText(this, "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        }
        if(password.isEmpty()){
            Toast.makeText(this, "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }
        if(name.isEmpty()){
            Toast.makeText(this, "Bạn chưa nhập tên", Toast.LENGTH_SHORT).show();
        }
        if(phoneNumber.length() !=10){
            Toast.makeText(this, "Số điện thoại không đúng", Toast.LENGTH_SHORT).show();
        }
        Call<Account> call = RetrofitConfig.getInstance().create(PostDataServer.class).getAccount(phoneNumber);
       call.enqueue(new Callback<Account>() {
           @Override
           public void onResponse(@NotNull Call<Account> call, @NotNull Response<Account> response) {
               Account account = response.body();
               if(account !=null){
                   Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onFailure(@NotNull Call<Account> call, @NotNull Throwable t) {
               Account account = new Account();
               account.setPhoneNumber(phoneNumber);
               account.setDeviceId(AppConfig.getDeviceID(RegisterActivity.this));
               account.setPassword(password);
               account.setName(name);
               Intent intent = new Intent(RegisterActivity.this,VerifyActivity.class);
               intent.putExtra(Constants.KEY_TYPE,Constants.TYPE_REGISTER);
               intent.putExtra(Constants.KEY_ACCOUNT,account);
               startActivity(intent);
               finish();
           }
       });

    }
}
