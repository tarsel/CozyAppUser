package com.fandataxiuser.ui.activity.passbook;

import com.fandataxiuser.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
