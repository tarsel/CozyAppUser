package com.fandataxiuser.ui.activity.social;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView {
    void onSuccess(Token token);

    void onError(Throwable e);
}
