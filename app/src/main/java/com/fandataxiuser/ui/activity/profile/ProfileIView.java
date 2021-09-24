package com.fandataxiuser.ui.activity.profile;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.User;

public interface ProfileIView extends MvpView {

    void onSuccess(User user);

    void onUpdateSuccess(User user);

    void onError(Throwable e);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);
}
