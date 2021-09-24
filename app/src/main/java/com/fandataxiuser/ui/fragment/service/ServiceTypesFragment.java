package com.fandataxiuser.ui.fragment.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.fandataxiuser.R;
import com.fandataxiuser.base.BaseActivity;
import com.fandataxiuser.base.BaseFragment;
import com.fandataxiuser.common.EqualSpacingItemDecoration;
import com.fandataxiuser.data.network.APIClient;
import com.fandataxiuser.data.network.model.EstimateFare;
import com.fandataxiuser.data.network.model.Provider;
import com.fandataxiuser.data.network.model.Service;
import com.fandataxiuser.ui.activity.main.MainActivity;
import com.fandataxiuser.ui.activity.payment.PaymentActivity;
import com.fandataxiuser.ui.adapter.ServiceAdapter;
import com.fandataxiuser.ui.fragment.RateCardFragment;
import com.fandataxiuser.ui.fragment.book_ride.BookRideFragment;
import com.fandataxiuser.ui.fragment.deliveryitem.ItemDetailsFragment;
import com.fandataxiuser.ui.fragment.schedule.ScheduleFragment;
import com.fandataxiuser.MvpApplication;
import com.fandataxiuser.common.Constants;
import com.fandataxiuser.data.SharedHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fandataxiuser.data.SharedHelper.getKey;
import static com.fandataxiuser.data.SharedHelper.putKey;
import static com.fandataxiuser.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceTypesFragment extends BaseFragment implements ServiceTypesIView {

    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    ServiceAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;

    private ServiceTypesPresenter<ServiceTypesFragment> presenter = new ServiceTypesPresenter<>();
    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private double walletAmount;
    private int surge;

    private ServiceListener mListener = pos -> {
        isFromAdapter = true;
        servicePos = pos;
        String key = mServices.get(pos).getName() + mServices.get(pos).getId();
        MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SERVICE_TYPE, mServices.get(pos).getId());

        showLoading();
        estimatedApiCall();
        List<Provider> providers = new ArrayList<>();
        if(providers!=null) {
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId() == mServices.get(pos).getId())
                    providers.add(provider);

            ((MainActivity) getActivity()).addSpecificProviders(providers, key);
        }
    };

    public ServiceTypesFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        if(MvpApplication.isRide){
          presenter.services("ride");
        }else {
            presenter.services("delivery");
        }
        return view;
    }

    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SERVICE_TYPE, service.getId());
                        if (MvpApplication.RIDE_REQUEST.containsKey(Constants.RIDE_REQUEST.SERVICE_TYPE) && MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.SERVICE_TYPE) != null) {
                            showLoading();
                            estimatedApiCall();
                        }
                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:

                sendRequest();

                break;
            default:
                break;
        }
    }

    private void estimatedApiCall() {

        Log.d("Estimate Fare", "estimatedApiCall: "+ MvpApplication.RIDE_REQUEST.toString());
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(MvpApplication.RIDE_REQUEST);
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {
                if (ServiceTypesFragment.this.isVisible()) {
                    hideLoading();
                    if (response.body() != null) {
                        EstimateFare estimateFare = response.body();

                        RateCardFragment.SERVICE = estimateFare.getService();
                        mEstimateFare = estimateFare;
                        surge = estimateFare.getSurge();
                        walletAmount = estimateFare.getWalletBalance();
                        if (getContext() != null)
                            SharedHelper.putKey(getContext(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                        if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                        else {
                            walletBalance.setVisibility(View.VISIBLE);
                            walletBalance.setText(getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                        }
                        if (surge == 0) {
                            surgeValue.setVisibility(View.GONE);
                            tvDemand.setVisibility(View.GONE);
                        } else {
                            surgeValue.setVisibility(View.VISIBLE);
                            surgeValue.setText(estimateFare.getSurgeValue());
                            tvDemand.setVisibility(View.VISIBLE);
                        }
                        if (isFromAdapter) {
                            mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                            MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.DISTANCE_VAL, estimateFare.getDistance());
                            adapter.setEstimateFare(mEstimateFare);
                            adapter.notifyDataSetChanged();
                            if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                            else errorLayout.setVisibility(View.GONE);
                        } else if (adapter != null) {
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
                                bundle.putDouble("use_wallet", walletAmount);
                                if(!MvpApplication.isRide) {
                                    ItemDetailsFragment bookRideFragment = new ItemDetailsFragment();
                                    bookRideFragment.setArguments(bundle);
                                    ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                                }else{
                                    BookRideFragment bookRideFragment = new BookRideFragment();
                                    bookRideFragment.setArguments(bundle);
                                    ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);

                                }
                            }
                        }
                    } else if (response.raw().code() == 500) try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(baseActivity(), object.optString("error"), Toast.LENGTH_SHORT).show();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
                onErrorBase(t);
            }
        });
    }

    @Override
    public void onSuccess(List<Service> services) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SERVICE_TYPE, 1);
            mServices.clear();
            mServices.addAll(services);

            try {
                AsyncTask.execute(() -> {
                    for (Service s : mServices) {
                        String key = s.getName() + s.getId();
                        if (!TextUtils.isEmpty(s.getMarker()))
                            if (TextUtils.isEmpty(SharedHelper.getKey(Objects.requireNonNull(getActivity()), key))) {
                                Bitmap b = ((BaseActivity) getActivity()).getBitmapFromURL(s.getMarker());
                                if (b != null)
                                    SharedHelper.putKey(getActivity(), key, ((BaseActivity) getActivity()).encodeBase64(b));
                            }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SERVICE_TYPE, mService.getId());
            }
            mListener.whenClicked(0);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.PAYMENT_MODE, data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.CARD_ID, data.getStringExtra("card_id"));
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(MvpApplication.RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        baseActivity().sendBroadcast(new Intent(Constants.BroadcastReceiver.INTENT_FILTER));
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }

    public interface ServiceListener {
        void whenClicked(int pos);
    }

}
