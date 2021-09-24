package com.fandataxiuser.ui.activity.past_trip_detail;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);

    void onError(Throwable e);
}
