package com.fandataxiuser.ui.activity.upcoming_trip_detail;

import com.fandataxiuser.base.MvpPresenter;

public interface UpcomingTripDetailsIPresenter<V extends UpcomingTripDetailsIView> extends MvpPresenter<V> {

    void getUpcomingTripDetails(Integer requestId);
}
