package com.fandataxiuser.ui.fragment.schedule;

import com.fandataxiuser.base.BasePresenter;
import com.fandataxiuser.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SchedulePresenter<V extends ScheduleIView> extends BasePresenter<V> implements ScheduleIPresenter<V> {

    @Override
    public void sendRequest(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().sendRequest(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(sendReqResponse -> getMvpView().onSuccess(sendReqResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
