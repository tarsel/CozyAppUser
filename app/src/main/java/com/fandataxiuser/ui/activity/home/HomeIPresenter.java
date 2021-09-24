package com.fandataxiuser.ui.activity.home;

import com.fandataxiuser.base.MvpPresenter;

public interface HomeIPresenter<V extends HomeIView> extends MvpPresenter<V> {

    void getUserInfo();

    void getAds();

    void logout(String id);

    void getNavigationSettings();


}
