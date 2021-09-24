package com.fandataxiuser.ui.activity.location_pick;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.AddressResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LocationPickIView extends MvpView {

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
