package com.fandataxiuser.ui.activity.help;

import com.fandataxiuser.base.MvpView;
import com.fandataxiuser.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help help);

    void onError(Throwable e);
}
