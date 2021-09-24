package com.fandataxiuser.ui.activity.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.fandataxiuser.BuildConfig;
import com.fandataxiuser.MvpApplication;
import com.fandataxiuser.R;
import com.fandataxiuser.base.BaseActivity;
import com.fandataxiuser.data.SharedHelper;
import com.fandataxiuser.data.network.model.DataResponse;
import com.fandataxiuser.data.network.model.SettingsResponse;
import com.fandataxiuser.data.network.model.User;
import com.fandataxiuser.data.network.model.UserAddress;
import com.fandataxiuser.data.network.model.ads.AdsResponse;
import com.fandataxiuser.data.network.model.ads.DataItem;
import com.fandataxiuser.ui.activity.coupon.CouponActivity;
import com.fandataxiuser.ui.activity.help.HelpActivity;
import com.fandataxiuser.ui.activity.invite_friend.InviteFriendActivity;
import com.fandataxiuser.ui.activity.main.MainActivity;
import com.fandataxiuser.ui.activity.notification_manager.NotificationManagerActivity;
import com.fandataxiuser.ui.activity.passbook.WalletHistoryActivity;
import com.fandataxiuser.ui.activity.payment.PaymentActivity;
import com.fandataxiuser.ui.activity.profile.ProfileActivity;
import com.fandataxiuser.ui.activity.setting.SettingsActivity;
import com.fandataxiuser.ui.activity.wallet.WalletActivity;
import com.fandataxiuser.ui.activity.your_trips.YourTripActivity;
import com.fandataxiuser.ui.adapter.AdsAdapter;
import com.fandataxiuser.ui.fragment.service_flow.ServiceFlowFragment;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.fandataxiuser.common.Constants.Status.EMPTY;
import static com.fandataxiuser.data.SharedHelper.key.PROFILE_IMG;

public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        HomeIView {

    private static String CURRENT_STATUS = EMPTY;
    private HomePresenter<HomeActivity> mainPresenter = new HomePresenter<>();

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    @BindView(R.id.tv_delivery)
    TextView tv_delivery;

    @BindView(R.id.iv_delivery)
    ImageView iv_delivery;

    @BindView(R.id.rv_ads)
    RecyclerView rv_ads;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private BottomSheetBehavior bsBehavior;
    private CircleImageView picture;
    private TextView name;
    private TextView sub_name;

    private HashMap<Integer, Marker> providersMarker;
    private ArrayList<LatLng> polyLinePoints;
    private Marker srcMarker, destMarker;
    private Polyline mPolyline;
    private LatLng start = null, end = null;
    private Location mLastKnownLocation;

    private DataResponse checkStatusResponse = new DataResponse();
    private UserAddress home = null, work = null;

    AdsAdapter adsAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);


        mainPresenter.attachView(this);

        providersMarker = new HashMap<>();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        picture = headerView.findViewById(R.id.picture);
        name = headerView.findViewById(R.id.name);
        sub_name = headerView.findViewById(R.id.sub_name);
        headerView.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, picture, ViewCompat.getTransitionName(picture));
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class), options.toBundle());
        });


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                mainPresenter.getNavigationSettings();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        mainPresenter.getAds();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDetach();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().findFragmentById(R.id.container)
                    instanceof ServiceFlowFragment) {
                getSupportFragmentManager().popBackStack();
            }
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

                 }
        }
        else {
                super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_payment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_coupon:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_passbook:
                startActivity(new Intent(this, WalletHistoryActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_become_driver:
                alertBecomeDriver();
                break;
            case R.id.nav_notification:
                startActivity(new Intent(this, NotificationManagerActivity.class));
                break;
            case R.id.nav_invite_friend:
                startActivity(new Intent(this, InviteFriendActivity.class));
                break;
            case R.id.nav_logout:
                ShowLogoutPopUp();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void ShowLogoutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> mainPresenter.logout(SharedHelper.getKey(this, "user_id")))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertBecomeDriver() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.DRIVER_PACKAGE));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    @OnClick({R.id.menu,  R.id.ivBack,R.id.tv_delivery,R.id.iv_delivery,R.id.rv_ride})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
                    if (user != null) {
                        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                        sub_name.setText(user.getEmail());
                        SharedHelper.putKey(HomeActivity.this, PROFILE_IMG, user.getPicture());
                        Glide.with(HomeActivity.this)
                                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(picture);
                    }
                    drawerLayout.openDrawer(Gravity.START);
                }

                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.rv_ride:
                MvpApplication.isRide=true;
            //    RIDE_REQUEST=null;
                startActivity(new Intent(this, MainActivity.class));
                this.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                break;

            case R.id.tv_delivery:
                MvpApplication.isRide=false;
                startActivity(new Intent(this, MainActivity.class));
                this.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                break;
            case R.id.iv_delivery:
                MvpApplication.isRide=false;
                startActivity(new Intent(this, MainActivity.class));
                this.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                break;

        }
    }


    @Override
    public void onSuccess(@NonNull User user) {

        /*
        set language
        for jewel
         */

        // putKey(this, "lang", user.getLanguage());  //remove server side call



        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));

        SharedHelper.putKey(this, "referral_code", user.getReferral_unique_id());
        SharedHelper.putKey(this, "referral_count", user.getReferral_count());
        SharedHelper.putKey(this, "referral_text", user.getReferral_text());
        SharedHelper.putKey(this, "referral_total_text", user.getReferral_total_text());

        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(HomeActivity.this, PROFILE_IMG, user.getPicture());
        Glide.with(HomeActivity.this)
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
        MvpApplication.showOTP = user.getRide_otp().equals("1");
    }

    @Override
    public void onAdsSuccess(AdsResponse adsResponse) {

        adsAdapter= new AdsAdapter(getApplicationContext(), (ArrayList<DataItem>) adsResponse.getData());
        rv_ads.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_ads.setAdapter(adsAdapter);
    }

    @Override
    public void onSuccessLogout(Object object) {
        LogoutApp();
    }


    @Override
    public void onError(Throwable e) {
        Log.d("Error", "onError: "+ e.getMessage());
            super.onError(e);
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        if (response.getReferral().getReferral().equalsIgnoreCase("1")) navMenuVisibility(true);
        else navMenuVisibility(false);
    }

    private void navMenuVisibility(boolean visibility) {
        navigationView.getMenu().findItem(R.id.nav_invite_friend).setVisible(visibility);
    }

    @Override
    public void onSettingError(Throwable e) {
        navMenuVisibility(false);
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


}
