package com.fandataxiuser.ui.activity.home;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.SettingsResponse;
import com.fandataxiuser.data.network.model.User;
import com.fandataxiuser.data.network.model.ads.AdsResponse;

public interface HomeIView extends MvpView {

    void onSuccess(User user);

    void onAdsSuccess(AdsResponse adsResponse);


    void onSuccessLogout(Object object);


    void onError(Throwable e);

    void onSuccess(SettingsResponse response);

    void onSettingError(Throwable e);

}
