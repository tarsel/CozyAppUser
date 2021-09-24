package com.fandataxiuser.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fandataxiuser.BuildConfig;
import com.fandataxiuser.R;
import com.fandataxiuser.base.BaseActivity;
import com.fandataxiuser.data.SharedHelper;
import com.fandataxiuser.data.network.model.ForgotResponse;
import com.fandataxiuser.data.network.model.Token;
import com.fandataxiuser.ui.activity.forgot_password.ForgotPasswordActivity;
import com.fandataxiuser.ui.activity.home.HomeActivity;
import com.fandataxiuser.ui.activity.register.RegisterActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends BaseActivity implements LoginIView {

    public static String TAG = "";
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private String email;
    private loginPresenter<PasswordActivity> presenter = new loginPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_password;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(v -> finish());

        presenter.attachView(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) email = extras.getString("email");

    }

    private void login() {
        try {
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (SharedHelper.getKey(this, "device_token").isEmpty()) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedHelper.putKey(this, "device_token", task.getResult().getToken());
                        Log.e("FCM_TOKEN", task.getResult().getToken());
                    } else Log.e("PasswordActivity", "getInstanceId failed", task.getException());
                });
            }else {

              //  MyToast.customToast(this, "Push Token isn't found, Push won't work properly", "s");

            }

            Log.e("FCM_TOKEN at password",  SharedHelper.getKey(this, "device_token", "No device"));



            HashMap<String, Object> map = new HashMap<>();
       //     map.put("grant_type", "password");
            map.put("email", email);
            map.put("password", password.getText().toString());
//            map.put("client_secret", BuildConfig.CLIENT_SECRET);
//            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("device_token", SharedHelper.getKey(this, "device_token", "No device"));
            map.put("device_id", SharedHelper.getKey(this, "device_id", "123"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);

            showLoading();
            presenter.login(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.sign_up, R.id.forgot_password, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_password:
                showLoading();
                presenter.forgotPassword(email);
                break;
            case R.id.next:
                login();
                break;
        }
    }

    @Override
    public void onSuccess(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String accessToken =  "Bearer " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onSuccess(ForgotResponse forgotResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("email", forgotResponse.getUser().getEmail());
        intent.putExtra("otp", forgotResponse.getUser().getOtp());
        intent.putExtra("id", forgotResponse.getUser().getId());
        startActivity(intent);
    }

    @Override
    public void onError(Throwable e) {
        TAG = "PasswordActivity";
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
