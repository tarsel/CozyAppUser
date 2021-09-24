package com.fandataxiuser.ui.fragment.searching;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.fandataxiuser.MvpApplication;
import com.fandataxiuser.base.BaseBottomSheetDialogFragment;
import com.fandataxiuser.common.Constants;
import com.fandataxiuser.data.network.model.Datum;
import com.fandataxiuser.ui.activity.main.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;

import com.fandataxiuser.R;

import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchingFragment extends BaseBottomSheetDialogFragment implements SearchingIView {

    private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();

    public SearchingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_searching;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
        presenter.attachView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        alertCancel();
    }

    private void alertCancel() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (MvpApplication.DATUM != null) {
                        showLoading();
                        Datum datum = MvpApplication.DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());
                        presenter.cancelRequest(map);
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_ADD);
        MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_LAT);
        MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_LONG);

        baseActivity().sendBroadcast(new Intent(Constants.BroadcastReceiver.INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(Constants.Status.EMPTY);
        dismissAllowingStateLoss();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
        baseActivity().sendBroadcast(new Intent(Constants.BroadcastReceiver.INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(Constants.Status.EMPTY);
    }
}
