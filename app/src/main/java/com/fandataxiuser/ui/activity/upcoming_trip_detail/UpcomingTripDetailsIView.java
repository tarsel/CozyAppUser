package com.fandataxiuser.ui.activity.upcoming_trip_detail;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);

    void onError(Throwable e);
}
