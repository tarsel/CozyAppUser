package com.fandataxiuser.ui.activity.wallet;

import com.appoets.paytmpayment.PaytmObject;
import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.AddWallet;
import com.fandataxiuser.data.network.model.BrainTreeResponse;
import com.fandataxiuser.data.network.model.User;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);

    void onSuccess(PaytmObject object);

    void onSuccess(BrainTreeResponse response);
    void onError(Throwable e);

    void onSuccess(User user);

}
