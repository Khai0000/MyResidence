package com.example.googlemap.EmergencyLocationRecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;
import com.squareup.picasso.Picasso;


public class EmergencyLocationDetails extends AppCompatActivity {

    private Button goBackButton;
    private ImageButton navigationButton;

    private RatingBar ratingBar;

    private TextView locationTextView, addressTextView, distanceTextView, reviewTextView, operatingStatusTextHolder;

    private ImageView locationImageView;

    private double destinationLatitude, destinationLongitude, userLatitude, userLongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_location_details);
        Intent intent = getIntent();
        String locationName = intent.getStringExtra("locationName");
        String locationAddress = intent.getStringExtra("address");
        double rating = intent.getDoubleExtra("rating", 0.0);
        int totalUserReview = intent.getIntExtra("totalUserReview", 0);
        float destinationDistance = intent.getFloatExtra("destinationDistance", 0.0f);

        String locationImage = intent.getStringExtra("locationImage");
        String openNow = intent.getStringExtra("openNow");

        setUpGoBackButton();
        setUpNavigationButton();
        setUpLocationTextView(locationName);
        setUpAddressTextView(locationAddress);
        setUpRatingBar(rating);
        setUpReviewTextView(totalUserReview);
        setUpDistanceTextView(destinationDistance);
        setUpLocationImageView(locationImage);
        setUpOperatingStatusTextHolder(openNow);
        destinationLatitude = intent.getDoubleExtra("destinationLatitude", 0.0);
        destinationLongitude = intent.getDoubleExtra("destinationLongitude", 0.0);
        userLatitude = intent.getDoubleExtra("userLatitude",0.0);
        userLongitude = intent.getDoubleExtra("userLongitude",0.0);

    }

    private void setUpOperatingStatusTextHolder(String openNow) {
        operatingStatusTextHolder = findViewById(R.id.operating_hours_text_holder);

        if (openNow == null) {
            operatingStatusTextHolder.setText("Unknown");
            operatingStatusTextHolder.setTextColor(Color.parseColor("#FF5252"));
        } else if (openNow.equals("true")) {
            operatingStatusTextHolder.setText("Open Now");
            operatingStatusTextHolder.setTextColor(Color.parseColor("#4CAF50"));
        } else if(openNow.equals("false")){
            operatingStatusTextHolder.setText("Closed");
            operatingStatusTextHolder.setTextColor(Color.parseColor("#FF5252"));
        }else{
            operatingStatusTextHolder.setText(openNow);
            operatingStatusTextHolder.setTextColor(Color.parseColor("#FF5252"));
        }
    }


    private void setUpLocationImageView(String locationImage) {

        locationImageView = findViewById(R.id.emergency_location_image);
        if (locationImage == null)
            Picasso.get().load(R.drawable.no_image_background).fit().into(locationImageView);
        else
            Picasso.get().load(locationImage).fit().into(locationImageView);

    }

    private void setUpReviewTextView(int totalUserReview) {
        reviewTextView = findViewById(R.id.total_user_review_text_holder);
        reviewTextView.setText("out of " + String.valueOf(totalUserReview) + " reviews");
    }

    private void setUpDistanceTextView(float destinationDistance) {
        distanceTextView = findViewById(R.id.distance_text_holder);
        distanceTextView.setText("Distance: " + String.format("%.2f", destinationDistance) + " km away");
    }

    private void setUpAddressTextView(String locationAddress) {
        addressTextView = findViewById(R.id.location_address_text_holder);
        addressTextView.setText("Address: " + locationAddress);
    }

    private void setUpLocationTextView(String locationName) {

        locationTextView = findViewById(R.id.location_name_text_holder);
        locationTextView.setText("Name: " + locationName);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUpRatingBar(double rating) {
        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating((float) rating);
    }

    private void setUpGoBackButton() {
        goBackButton = findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpNavigationButton() {
        navigationButton = findViewById(R.id.location_navigation_button);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }


}
