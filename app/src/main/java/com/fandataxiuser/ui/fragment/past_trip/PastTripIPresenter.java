package com.fandataxiuser.ui.fragment.past_trip;

import com.fandataxiuser.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PastTripIPresenter<V extends PastTripIView> extends MvpPresenter<V> {
    void pastTrip();
}
