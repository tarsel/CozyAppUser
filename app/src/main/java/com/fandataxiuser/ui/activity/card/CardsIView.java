package com.fandataxiuser.ui.activity.card;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Card;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CardsIView extends MvpView {
    void onSuccess(List<Card> cardList);

    void onError(Throwable e);
}
