package com.fandataxiuser.ui.activity.main;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.AddressResponse;
import com.fandataxiuser.data.network.model.DataResponse;
import com.fandataxiuser.data.network.model.Provider;
import com.fandataxiuser.data.network.model.SettingsResponse;
import com.fandataxiuser.data.network.model.User;

import java.util.List;

public interface MainIView extends MvpView {

    void onSuccess(User user);

    void onSuccess(DataResponse dataResponse);

    void onDestinationSuccess(Object object);

    void onSuccessLogout(Object object);

    void onSuccess(AddressResponse response);

    void onSuccess(List<Provider> objects);

    void onError(Throwable e);

    void onSuccess(SettingsResponse response);

    void onSettingError(Throwable e);

}
