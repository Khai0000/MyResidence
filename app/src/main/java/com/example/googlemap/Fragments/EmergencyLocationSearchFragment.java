package com.example.googlemap.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.CommentRecyclerView.VerticalMarginItemDecoration;
import com.example.googlemap.EmergencyLocationRecyclerView.EmergencyLocation;
import com.example.googlemap.EmergencyLocationRecyclerView.EmergencyLocationAdapter;
import com.example.googlemap.HttpRequest;
import com.example.googlemap.R;
import com.example.googlemap.dialog.MyCustomDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class EmergencyLocationSearchFragment extends Fragment {

    private Spinner locationTypeSpinner;
    private Button searchButton;

    private RecyclerView locationRecyclerView;

    private ArrayList<EmergencyLocation> locationArrayList = new ArrayList<>();

    private String locationType;

    private ProgressBar progressBar;

    private TextView generalTextView, resultsTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_location_search_fragment, container, false);

        locationTypeSpinner = view.findViewById(R.id.emergency_location_selectors);
        searchButton = view.findViewById(R.id.search_location_button);
        locationRecyclerView = view.findViewById(R.id.emergency_location_recycler_view);
        progressBar = view.findViewById(R.id.progress_indicator);
        generalTextView = view.findViewById(R.id.general_text_holder);
        resultsTextView = view.findViewById(R.id.result_text_holder);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpLocationTypeSpinner();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationTypeSpinner.getSelectedItem().toString().equals("Select a type of location")) {
                    MyCustomDialog.showDialog("Invalid location type", "Please select a type of location to search", getActivity());
                    return;
                }
                generalTextView.setVisibility(View.GONE);
                resultsTextView.setVisibility(View.INVISIBLE);
                locationArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);
                LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null) {
                        double userLatitude = lastKnownLocation.getLatitude();
                        double userLongitude = lastKnownLocation.getLongitude();
                        locationType = locationTypeSpinner.getSelectedItem().toString();

                        Future<List<Map<String, Object>>> future = new HttpRequest().getEmergencyLocations(getContext(),locationType,userLatitude, userLongitude);

                        try {
                            List<Map<String, Object>> locationList = future.get();
                            for (Map<String, Object> location : locationList) {

                                String locationName = (String) location.get("name");
                                String icon = (String) location.get("icon");
                                String locationImage = (String) location.get("locationImage");
                                String address = (String) location.get("address");
                                double rating = (double) location.get("rating");
                                int totalUserReview = (int) location.get("totalUserReview");
                                double locationLatitude = (double) location.get("latitude");
                                double locationLongitude = (double) location.get("longitude");

                                Location destinationLocation = new Location("Destination");
                                destinationLocation.setLatitude(locationLatitude);
                                destinationLocation.setLongitude(locationLongitude);

                                Location userLocation = new Location("User");
                                userLocation.setLatitude(userLatitude);
                                userLocation.setLongitude(userLongitude);

                                float distance = userLocation.distanceTo(destinationLocation) / 1000;

                                String openNow = (String) location.get("openNow");

                                EmergencyLocation emergencyLocation = new EmergencyLocation(icon, locationName, address, rating, totalUserReview, locationLatitude, locationLongitude, distance, locationImage, openNow,userLatitude,userLongitude);
                                locationArrayList.add(emergencyLocation);
                            }
                        } catch (Exception e) {
                            MyCustomDialog.showDialog("Places API Error", e.getLocalizedMessage(),getActivity());
                        }
                    }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (locationArrayList.size() == 0)
                            resultsTextView.setVisibility(View.VISIBLE);
                        else
                        {
                            Collections.sort(locationArrayList);
                            locationRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 300);

                }

            }
        });

        locationRecyclerView.setAdapter(new EmergencyLocationAdapter(getContext(), locationArrayList));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        locationRecyclerView.setLayoutManager(layoutManager);
        locationRecyclerView.addItemDecoration(new VerticalMarginItemDecoration(18));
    }


    private void setUpLocationTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.emergency_location_categories, R.layout.location_search_spinner_item_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationTypeSpinner.setAdapter(adapter);
        locationTypeSpinner.setSelection(0);
    }
}
