package com.fandataxiuser.ui.activity.location_pick;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fandataxiuser.MvpApplication;
import com.fandataxiuser.base.BaseActivity;
import com.fandataxiuser.common.Constants;
import com.fandataxiuser.common.RecyclerItemClickListener;
import com.fandataxiuser.data.network.model.AddressResponse;
import com.fandataxiuser.data.network.model.UserAddress;
import com.fandataxiuser.ui.adapter.PlacesAutoCompleteAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.fandataxiuser.R;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationPickIView {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-3.24437008301, 4.71046214438),new LatLng( 1.0601216976, 11.0983409693));
    private Location mLastKnownLocation;
    protected GoogleApiClient mGoogleApiClient;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;
    boolean isEnableIdle = false;
    @BindView(R.id.llSource)
    LinearLayout llSource;

    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private UserAddress home, work = null;
    private LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();
    private EditText selectedEditText;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    //Base on action we are show/hide view and setResults
    private String actionName = Constants.LocationActions.SELECT_SOURCE;

    PlacesClient placesClient =null;
    Handler handler= new Handler();
    private TextWatcher filterTextWatcher = new TextWatcher() {
        boolean isTyping = false;

        private Handler timer = new Handler();
        private final long DELAY = 2000; // milliseconds
        String str ;
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Log.d("UI thread", "I am the UI thread");
        //        mAutoCompleteAdapter.getFilter().filter(str.toString());
                isTyping = false;
                Log.d("TYPING", "stopped typing");
                hideKeyboard();
            }
        };

        public void afterTextChanged(Editable s) {
            if (isEditable) if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                locationsRv.setVisibility(View.VISIBLE);

                //hideKeyboard();
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (!mGoogleApiClient.isConnected()) Log.e("ERROR", "API_NOT_CONNECTED");
            if (s.toString().equals("")) locationsRv.setVisibility(View.GONE);

            if(!isTyping) {
                Log.d("TYPING", "started typing");
                // Send notification for start typing event
                isTyping = true;
            }else{

            }
            str=s.toString();
//            handler.removeCallbacks(runnable);
//            handler.postDelayed(runnable, DELAY);

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isEditable) if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                locationsRv.setVisibility(View.VISIBLE);
                //   hideKeyboard();
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (!mGoogleApiClient.isConnected()) Log.e("ERROR", "API_NOT_CONNECTED");
            if (s.toString().equals("")) locationsRv.setVisibility(View.GONE);

        }

    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }

    @Override
    public void initView() {
        buildGoogleApiClient();
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
        placesClient = Places.createClient(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item_location, mGoogleApiClient, BOUNDS_INDIA);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        locationsRv.setAdapter(mAutoCompleteAdapter);

        source.addTextChangedListener(filterTextWatcher);
        destination.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = source;
        });

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = destination;
        });

        destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });


        source.setText(MvpApplication.RIDE_REQUEST.containsKey(Constants.RIDE_REQUEST.SRC_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.SRC_ADD)).toString())
                ? ""
                : String.valueOf(MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.SRC_ADD))
                : "");

        destination.setText(MvpApplication.RIDE_REQUEST.containsKey(Constants.RIDE_REQUEST.DEST_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.DEST_ADD)).toString())
                ? ""
                : String.valueOf(MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.DEST_ADD))
                : "");


        locationsRv.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {
                    if (mAutoCompleteAdapter.getItemCount() == 0) return;
                    final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);
                    Log.i("LocationPickActivity", "Autocomplete item selected: " + item.address);

                    List<Field> placeFields = Arrays.asList(Field.LAT_LNG);

                    // Construct a request object, passing the place ID and fields array.
                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                            .build();

                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place myPlace = response.getPlace();
                        Log.e("Place", "Place found: " + myPlace.getAddress());

                        LatLng latLng = myPlace.getLatLng();
                        isLocationRvClick = true;
                        isSettingLocationClick = true;
                        setLocationText(String.valueOf(item.address), latLng,
                                isLocationRvClick, isSettingLocationClick);


                    }).addOnFailureListener((exception) -> {

                        Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();

                    });

                    Log.i("LocationPickActivity", "Clicked: " + item.address);
                    Log.i("LocationPickActivity", "Called getPlaceById to get Place details for " + item.placeId);



//                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
//                    placeResult.setResultCallback(places -> {
//                        if (places.getCount() == 1) {
//                            LatLng latLng = places.get(0).getLatLng();
//                            isLocationRvClick = true;
//                            isSettingLocationClick = true;
//                            setLocationText(String.valueOf(item.address), latLng,
//                                    isLocationRvClick, isSettingLocationClick);
//                            //Toast.makeText(getApplicationContext(), String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
//                        } else
//                            Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
//                    });
//
//                    Log.i("LocationPickActivity", "Clicked: " + item.address);
//                    Log.i("LocationPickActivity", "Called getPlaceById to get Place details for " + item.placeId);
                })
        );


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            actionName = bundle.getString("actionName",Constants.LocationActions.SELECT_SOURCE);

            if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_SOURCE)) {
                destination.setCursorVisible(false);
                source.setCursorVisible(true);
                source.requestFocus();
                selectedEditText = source;
            }else if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_DESTINATION)){
                source.setCursorVisible(false);
                destination.setCursorVisible(true);
                destination.setText("");
                destination.requestFocus();
                selectedEditText = destination;
            }else if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.CHANGE_DESTINATION)){
                llSource.setVisibility(View.GONE);
                source.setHint(getString(R.string.select_location));
                selectedEditText = destination;
            }else if (!TextUtils.isEmpty(actionName) && (actionName.equals(Constants.LocationActions.SELECT_HOME)|| actionName.equals(Constants.LocationActions.SELECT_WORK))){
                destinationLayout.setVisibility(View.GONE);
                selectedEditText = destination;
                source.setText("");
                source.setHint(getString(R.string.select_location));
            } else{
                destinationLayout.setVisibility(View.VISIBLE);
                llSource.setVisibility(View.VISIBLE);
                source.setHint(getString(R.string.pickup_location));
                selectedEditText = source;
            }

        }

        presenter.address();
    }


    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SRC_ADD, address);
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SRC_LAT, latLng.latitude);
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.SRC_LONG, latLng.longitude);
            }

            if (selectedEditText.getTag().equals("destination")) {
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.DEST_ADD, address);
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.DEST_LAT, latLng.latitude);
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.DEST_LONG, latLng.longitude);

//                if (isLocationRvClick) {
//                    //  Done functionality called...
//                    setResult(Activity.RESULT_OK, new Intent());
//                    finish();
//                }
            }
        } else {
            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.SRC_ADD);
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.SRC_LAT);
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.SRC_LONG);
            }
            if (selectedEditText.getTag().equals("destination")) {
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_ADD);
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_LAT);
                MvpApplication.RIDE_REQUEST.remove(Constants.RIDE_REQUEST.DEST_LONG);
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locationsRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @OnClick({R.id.source, R.id.destination, R.id.reset_source, R.id.reset_destination, R.id.home_address_layout, R.id.work_address_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                break;
            case R.id.destination:
                break;
            case R.id.reset_source:
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.reset_destination:
                destination.requestFocus();
                selectedEditText = destination;
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(),
                            new LatLng(home.getLatitude(), home.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(),
                            new LatLng(work.getLatitude(), work.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                hideKeyboard();
                setLocationText(address, cameraPosition.target, isLocationRvClick, isSettingLocationClick);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        System.out.println("LocationPickActivity.onCameraMove");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        try {
//            //      Google map custom style...
//            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
//        } catch (Resources.NotFoundException e) {
//            Log.d("Map:Style", "Can't find style. Error: ");
//        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
//                if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK)){
//                Intent intent = new Intent();
//                intent.putExtra(Constants.RIDE_REQUEST.SRC_ADD, s_address);
//                intent.putExtra(Constants.RIDE_REQUEST.SRC_LAT, s_latitude);
//                intent.putExtra(Constants.RIDE_REQUEST.SRC_LONG, s_longitude);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            } else {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                //  }
                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
        else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeAddressLayout.setVisibility(View.VISIBLE);
        }

        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
        else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workAddressLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

}
