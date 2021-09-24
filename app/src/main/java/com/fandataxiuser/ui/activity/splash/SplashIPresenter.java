package com.fandataxiuser.ui.activity.splash;

import com.fandataxiuser.base.MvpPresenter;

import java.util.HashMap;

public interface SplashIPresenter<V extends SplashIView> extends MvpPresenter<V> {

    void services(String s);

    void profile();

    void checkVersion(HashMap<String, Object> map);
}
