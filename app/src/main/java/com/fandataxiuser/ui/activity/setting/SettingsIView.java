package com.fandataxiuser.ui.activity.setting;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
