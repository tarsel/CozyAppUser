package com.fandataxiuser.ui.activity.register;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.RegisterResponse;
import com.fandataxiuser.data.network.model.SettingsResponse;

public interface RegisterIView extends MvpView {

    void onSuccess(SettingsResponse response);

    void onSuccess(RegisterResponse object);

    void onSuccess(Object object);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);

    void onError(Throwable e);

    void onVerifyEmailError(Throwable e);
}
