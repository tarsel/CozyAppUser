package com.fandataxiuser.ui.activity.home;

import com.fandataxiuser.base.BasePresenter;
import com.fandataxiuser.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter<V extends HomeIView> extends BasePresenter<V> implements HomeIPresenter<V> {

    @Override
    public void getUserInfo() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }


    @Override
    public void logout(String id) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .logout(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessLogout, getMvpView()::onError));
    }

    @Override
    public void getNavigationSettings() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getSettings()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onSettingError));
    }


    @Override
    public void getAds() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getAds()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onAdsSuccess, getMvpView()::onError));
    }


}
