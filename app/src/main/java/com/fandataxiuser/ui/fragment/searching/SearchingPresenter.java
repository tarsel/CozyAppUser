package com.fandataxiuser.ui.fragment.searching;

import com.fandataxiuser.base.BasePresenter;
import com.fandataxiuser.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.FieldMap;

public class SearchingPresenter<V extends SearchingIView> extends BasePresenter<V> implements SearchingIPresenter<V> {

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
