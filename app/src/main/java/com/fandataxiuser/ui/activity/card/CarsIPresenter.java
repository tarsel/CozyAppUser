package com.fandataxiuser.ui.activity.card;

import com.fandataxiuser.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
