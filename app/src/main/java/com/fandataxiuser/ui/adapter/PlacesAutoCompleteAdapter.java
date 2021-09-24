package com.fandataxiuser.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.maps.model.LatLngBounds;
import com.fandataxiuser.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Objects;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {

    private static final String TAG = "PlacesAutoAdapter";

    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;

    private Context mContext;
    private int layout;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    PlacesClient placesClient =null;


    public PlacesAutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient, LatLngBounds bounds) {
        mContext = context;
        layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);

        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_api_key));
        }

        placesClient = Places.createClient(context);


    }

    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
    }

    private ArrayList getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i("", "Starting autocomplete query for: " + constraint);


            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
// and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
// Create a RectangularBounds object.
//            RectangularBounds bounds = RectangularBounds.newInstance(
//                    new LatLng(-3.24437008301, 4.71046214438),new LatLng( 1.0601216976, 11.0983409693));
// Use the builder to create a FindAutocompletePredictionsRequest.
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
// Call either setLocationBias() OR setLocationRestriction().
//                    .setLocationBias(bounds)
//                    .setLocationRestriction(bounds)
                   // .setCountry()
                    .setCountry("PK")
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build();
            ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());


                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL),
                            prediction.getFullText(STYLE_BOLD)));
                    notifyDataSetChanged();

                }
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
//            // contain the results when the query completes.
//            PendingResult<AutocompletePredictionBuffer> results = Places
//                    .GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
//                            mBounds, mPlaceFilter);
//
//            // This method should have been called off the main UI thread. Block and wait for at most 60s
//            // for a result from the API.
//            AutocompletePredictionBuffer autocompletePredictions = results
//                    .await(30, TimeUnit.SECONDS);
//
//            // Confirm that the query completed successfully, otherwise return null
//            final Status status = autocompletePredictions.getStatus();
//            if (!status.isSuccess()) {
//                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
//                autocompletePredictions.release();
//                return null;
//            }
//
//            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
//                    + " predictions.");
//
//            // Copy the results into our own data structure, because we can't hold onto the buffer.
//            // AutocompletePrediction objects encapsulate the API response (place ID and description).
//
//            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
//            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
//            while (iterator.hasNext()) {
//                AutocompletePrediction prediction = iterator.next();
//                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
//                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL),
//                        prediction.getFullText(STYLE_BOLD)));
//            }
//
//            // Release the buffer now that all data has been copied.
//            autocompletePredictions.release();

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = Objects.requireNonNull(layoutInflater).inflate(layout, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.area.setText(mResultList.get(i).area);
        mPredictionHolder.address.setText(mResultList.get(i).address.toString()
                .replace(mResultList.get(i).area + ", ", ""));
    }

    @Override
    public int getItemCount() {
        return (mResultList == null) ? 0 : mResultList.size();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    class PredictionHolder extends RecyclerView.ViewHolder {

        private TextView address, area;

        PredictionHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
        }
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() {
            return area.toString();
        }
    }
}
