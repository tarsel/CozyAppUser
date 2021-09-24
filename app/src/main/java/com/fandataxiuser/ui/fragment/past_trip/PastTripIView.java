package com.fandataxiuser.ui.fragment.past_trip;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Datum;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PastTripIView extends MvpView {
    void onSuccess(List<Datum> datumList);

    void onError(Throwable e);
}
