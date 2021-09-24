package com.fandataxiuser.ui.activity.passbook;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);

    void onError(Throwable e);
}
