package com.fandataxiuser.ui.activity.past_trip_detail;

import com.fandataxiuser.base.MvpPresenter;

public interface PastTripDetailsIPresenter<V extends PastTripDetailsIView> extends MvpPresenter<V> {

    void getPastTripDetails(Integer requestId);
}
