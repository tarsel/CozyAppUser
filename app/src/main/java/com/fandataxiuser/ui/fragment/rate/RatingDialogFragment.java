package com.fandataxiuser.ui.fragment.rate;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.fandataxiuser.MvpApplication;
import com.fandataxiuser.base.BaseBottomSheetDialogFragment;
import com.fandataxiuser.chat.ChatActivity;
import com.fandataxiuser.common.Constants;
import com.fandataxiuser.data.network.model.Datum;
import com.fandataxiuser.data.network.model.Provider;
import com.fandataxiuser.ui.activity.main.MainActivity;
import com.fandataxiuser.ui.fragment.invoice.InvoiceFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.fandataxiuser.R;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RatingDialogFragment extends BaseBottomSheetDialogFragment implements RatingIView {

    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.ratings_name)
    TextView ratingsName;
    private RatingPresenter<RatingDialogFragment> presenter = new RatingPresenter<>();

    public RatingDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rating_dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View view) {
        InvoiceFragment.isInvoiceCashToCard = false;
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        presenter.attachView(this);

        if (MvpApplication.DATUM != null) {
            Provider provider = MvpApplication.DATUM.getProvider();
            if (provider != null) {
                Glide.with(baseActivity()).load(provider.getAvatar()).
                        apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).
                                dontAnimate().error(R.drawable.ic_user_placeholder)).into(avatar);
                ratingsName.setText(getString(R.string.ratings) + " " +
                        provider.getFirstName() + " " + provider.getLastName());
            }
        }
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        dismiss();
        Objects.requireNonNull(getActivity()).sendBroadcast(new Intent(Constants.BroadcastReceiver.INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(Constants.Status.EMPTY);
        if (!TextUtils.isEmpty(ChatActivity.chatPath))
            FirebaseDatabase.getInstance().getReference().child(ChatActivity.chatPath).removeValue();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (MvpApplication.DATUM != null) {
            Datum datum = MvpApplication.DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            map.put("rating", Math.round(rating.getRating()));
            map.put("comment", comment.getText().toString());
            showLoading();
            presenter.rate(map);

        }
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
