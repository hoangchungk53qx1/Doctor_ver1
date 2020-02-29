package com.bhsoft.doctor.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhsoft.doctor.R;
import com.bhsoft.doctor.apisetup.PostDataServer;
import com.bhsoft.doctor.apisetup.RetrofitConfig;
import com.bhsoft.doctor.config.Constants;
import com.bhsoft.doctor.model.Account;
import com.bhsoft.doctor.utils.AccountUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyActivity extends AppCompatActivity {
private EditText edtVerify;
private Account account1;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        initView();
        getData();

    }
    private void initView() {
        edtVerify = findViewById(R.id.edtVerify);
    }

    public void onClickVerify(View view) {
        String codeVerify = edtVerify.getText().toString();
        if(codeVerify.isEmpty()){
            Toast.makeText(this, "Chưa nhập mã xác thực", Toast.LENGTH_SHORT).show();
            return;
        }
        if(codeVerify.length() != 6){
            Toast.makeText(this, "Mã xác thực cần có ít nhất 6 số", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeVerify);
        signInWithPhoneAuthCredential(credential);



    }

    private void getData() {
        account1 = (Account) getIntent().getSerializableExtra(Constants.KEY_ACCOUNT); // cast qua kiểu Account
        // chú ý chỗ này thằng Object nên phải getSerializableExtra

        //còn dưới này kiểu dữ liệu thường thì chỉ cần pass qua
        type = (int) getIntent().getIntExtra(Constants.KEY_TYPE,0);  //
        mAuth = FirebaseAuth.getInstance();
        sentOTP();

    }

    private void sentOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + account1.getPhoneNumber(),
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(getApplicationContext(), "Đã gửi mã xác thực", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if(e instanceof FirebaseAuthInvalidCredentialsException){
                Toast.makeText(VerifyActivity.this, "Kiểm tra lại số điện thoại", Toast.LENGTH_SHORT).show();
                finish();
            }
            if(e instanceof FirebaseAuthInvalidCredentialsException){
                Toast.makeText(VerifyActivity.this, "Mã xác thực không chính xác", Toast.LENGTH_SHORT).show();
                finish();
            }
            if(e instanceof FirebaseTooManyRequestsException){
              new AlertDialog.Builder(VerifyActivity.this)
                      .setTitle("Cảnh báo !!!")
                      .setMessage("Yêu cầu gửi OTP nhiều quá")
                      .setCancelable(false)
                      .setPositiveButton("Trở về", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              finish();
                          }
                      })
                      .show();

            }
            Toast.makeText(VerifyActivity.this, "Gửi mã thất bại rồi", Toast.LENGTH_SHORT).show();
        }

    };
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if(type==Constants.TYPE_REGISTER){
                                registerAccount();
                            }
                            else if(type == Constants.TYPE_LOGIN){
                                loginHouse();
                            }
                         else {
                            Toast.makeText(VerifyActivity.this, "Mã xác thực có vấn đề", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void loginHouse() {
       AccountUtils.getInstance(VerifyActivity.this).setAccount(account1);
        startActivity(new Intent(VerifyActivity.this,HouseAcitivity.class));
        finish();

    }


    private void registerAccount() {
        Call<String> call = RetrofitConfig.getInstance()
                .create(PostDataServer.class)
                .registerAccount(account1.getPhoneNumber(),account1.getPassword(),account1.getDeviceId(),account1.getName());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, Response<String> response) {
                if(response.body()!=null){
                   if(response.body().equals("Success")){
                       Toast.makeText(VerifyActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                   }
                   loginHouse();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Toast.makeText(VerifyActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
