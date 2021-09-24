package com.fandataxiuser.ui.activity.notification_manager;

import com.fandataxiuser.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
