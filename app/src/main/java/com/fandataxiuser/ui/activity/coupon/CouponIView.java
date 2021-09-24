package com.fandataxiuser.ui.activity.coupon;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);

    void onError(Throwable e);
}
