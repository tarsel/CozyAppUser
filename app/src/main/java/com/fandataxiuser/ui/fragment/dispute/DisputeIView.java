package com.fandataxiuser.ui.fragment.dispute;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.DisputeResponse;
import com.fandataxiuser.data.network.model.Help;

import java.util.List;

public interface DisputeIView extends MvpView {

    void onSuccess(Object object);

    void onSuccessDispute(List<DisputeResponse> responseList);

    void onError(Throwable e);

    void onSuccess(Help help);
}
