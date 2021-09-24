package com.fandataxiuser.ui.activity.notification_manager;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.NotificationManager;

import java.util.List;

public interface NotificationManagerIView extends MvpView {

    void onSuccess(List<NotificationManager> notificationManager);

    void onError(Throwable e);

}