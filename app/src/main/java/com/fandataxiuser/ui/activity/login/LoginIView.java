package com.fandataxiuser.ui.activity.login;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.ForgotResponse;
import com.fandataxiuser.data.network.model.Token;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(ForgotResponse object);

    void onError(Throwable e);
}
