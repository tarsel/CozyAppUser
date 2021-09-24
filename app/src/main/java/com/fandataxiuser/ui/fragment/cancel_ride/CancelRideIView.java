package com.fandataxiuser.ui.fragment.cancel_ride;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.CancelResponse;

import java.util.List;

public interface CancelRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccess(List<CancelResponse> response);

    void onReasonError(Throwable e);
}
