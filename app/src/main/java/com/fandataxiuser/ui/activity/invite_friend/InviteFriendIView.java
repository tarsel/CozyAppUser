package com.fandataxiuser.ui.activity.invite_friend;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.User;

public interface InviteFriendIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);

}
