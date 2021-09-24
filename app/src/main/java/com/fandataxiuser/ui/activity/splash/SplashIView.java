package com.fandataxiuser.ui.activity.splash;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.CheckVersion;
import com.fandataxiuser.data.network.model.Service;
import com.fandataxiuser.data.network.model.User;

import java.util.List;

public interface SplashIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onSuccess(User user);

    void onError(Throwable e);

    void onSuccess(CheckVersion checkVersion);
}
