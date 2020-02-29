package com.bhsoft.doctor.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhsoft.doctor.R;
import com.bhsoft.doctor.apisetup.PostDataServer;
import com.bhsoft.doctor.apisetup.RetrofitConfig;
import com.bhsoft.doctor.config.AppConfig;
import com.bhsoft.doctor.config.Constants;
import com.bhsoft.doctor.model.Account;
import com.bhsoft.doctor.utils.AccountUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtPhoneNumber, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        Account account = AccountUtils.getInstance(getApplicationContext()).getAccount();
        if(account ==null){
            return;
        }
        edtPhoneNumber.setText(account.getPhoneNumber()); // lấy số điện thoại đã nhập ném ra
    }

    private void initView() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
    }


    public void onClickLogin(View view) {
        String phoneNumber = edtPhoneNumber.getText().toString();
        final String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Bạn chưa điền số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Bạn chưa điền mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có tối thiểu 6 kí tự", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Account> call = RetrofitConfig.getInstance().create(PostDataServer.class).getAccount(phoneNumber);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NotNull Call<Account> call, @NotNull Response<Account> response) {

                    // response là nội dung trả về, response.body nó sẽ ép kiểu cái chuỗi trả về thành đối tượng java cho mình
                    Account account = response.body();
                     if (account != null){
                    // Tài khoản đã tồn tại.

                    if (account.getDeviceId() == null) {
                        startVerify(account);
                        return;
                    }
                    // Kiểm tra mật khẩu có trùng hay không
                    if (account.getPassword().equals(password)) {
                        // Kiểm tra id thiết bị hiện tại với id thiết bị trên server có trùng nhau hay không
                        // Nếu trùng nhau thì sẽ ko cần xác thực OTP và ngược lại
                        if (account.getDeviceId().equals(AppConfig.getDeviceID(LoginActivity.this))) {

                            // Lưu đối tượng account
                            AccountUtils.getInstance(getApplicationContext()).setAccount(account);
                            // Mở màn hình chính
                            startActivity(new Intent(LoginActivity.this, HouseAcitivity.class));
                            finish();
                        } else {
                            // Chuyển đến màn hình xác thực
                            startVerify(account);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                edtPassword.setText("");
            }
        });
    }

    private void startVerify(Account account) {
        Intent intent = new Intent(this, VerifyActivity.class);
        intent.putExtra(Constants.KEY_ACCOUNT, account);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_LOGIN);
        startActivity(intent);
    }

    public void onClickRegister (View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
