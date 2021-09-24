package com.fandataxiuser.ui.fragment.service;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Service;

import java.util.List;

public interface ServiceTypesIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onError(Throwable e);

    void onSuccess(Object object);
}
