package com.fandataxiuser.ui.activity.payment;

import com.appoets.paytmpayment.PaytmObject;
import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.BrainTreeResponse;
import com.fandataxiuser.data.network.model.Card;
import com.fandataxiuser.data.network.model.CheckSumData;

import java.util.List;

public interface PaymentIView extends MvpView {

    void onSuccess(Object card);

    void onSuccess(BrainTreeResponse response);

    void onSuccess(List<Card> cards);

    void onAddCardSuccess(Object cards);

    void onError(Throwable e);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);

    void onPayTmCheckSumSuccess(PaytmObject payTmResponse);

}
