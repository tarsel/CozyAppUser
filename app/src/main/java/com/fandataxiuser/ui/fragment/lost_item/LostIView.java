package com.fandataxiuser.ui.fragment.lost_item;

import com.fandataxiuser.base.MvpView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LostIView extends MvpView {
    void onSuccess(Object object);
    void onError(Throwable e);
}
