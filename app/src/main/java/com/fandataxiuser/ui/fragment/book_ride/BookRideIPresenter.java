package com.fandataxiuser.ui.fragment.book_ride;

import com.fandataxiuser.base.MvpPresenter;

import java.util.HashMap;


public interface BookRideIPresenter<V extends BookRideIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj,String oj);

    void getCouponList();
}
