package com.fandataxiuser.ui.fragment.searching;

import com.fandataxiuser.base.MvpView;

public interface SearchingIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
