package com.fandataxiuser.ui.activity.location_pick;

import com.fandataxiuser.base.MvpPresenter;

public interface LocationPickIPresenter<V extends LocationPickIView> extends MvpPresenter<V> {
    void address();
}
