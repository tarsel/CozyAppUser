package com.fandataxiuser.utils;

import android.content.Context;

import com.aapbd.appbajarlib.storage.PersistData;

public class AppConstant {

    private static final String CURRENTLANGUAGE = "CURRENTLANGUAGE";

    public static String phone = "";


    public static String getCurrentLanguage(Context con)
    {


        return PersistData.getStringData(con, AppConstant.CURRENTLANGUAGE);

    }

    public static void setCurrentLanguage(Context con, String langaugecode)
    {


        PersistData.setStringData(con, AppConstant.CURRENTLANGUAGE, langaugecode);

    }
}
