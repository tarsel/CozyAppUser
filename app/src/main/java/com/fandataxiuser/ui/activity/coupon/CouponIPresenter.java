package com.fandataxiuser.ui.activity.coupon;

import com.fandataxiuser.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
