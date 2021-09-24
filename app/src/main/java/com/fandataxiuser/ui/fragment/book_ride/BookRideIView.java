package com.fandataxiuser.ui.fragment.book_ride;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);
}
