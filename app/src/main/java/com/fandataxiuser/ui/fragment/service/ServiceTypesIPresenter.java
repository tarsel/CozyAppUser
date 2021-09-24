package com.fandataxiuser.ui.fragment.service;

import com.fandataxiuser.base.MvpPresenter;

import java.util.HashMap;

public interface ServiceTypesIPresenter<V extends ServiceTypesIView> extends MvpPresenter<V> {

    void services(String type);

    void rideNow(HashMap<String, Object> obj);

}
