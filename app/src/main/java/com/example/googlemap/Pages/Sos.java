package com.example.googlemap.Pages;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.googlemap.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Sos extends AppCompatActivity {

    private Toolbar toolbar;
    private Handler handlerAnimation = new Handler();
    private Button sosButton, policeButton, ambulanceButton, fireButton;
    private ImageView imgAnimation1,imgAnimation2, arrowAnimationRight, arrowAnimationLeft, arrowAnimationTop;
    float initialX, initialY;
    private int departmentNum = 0;
    private TextInputEditText sosLocationInput;
    private static final float MIN_SWIPE_DISTANCE = 230;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_page);

        sosLocationInput = findViewById(R.id.current_location_input_text);
        sosButton = findViewById(R.id.sos_button);
        imgAnimation1 = findViewById(R.id.pulse_animation_image_view1);
        imgAnimation2 = findViewById(R.id.pulse_animation_image_view2);
        arrowAnimationRight = findViewById(R.id.arrow_animation_right_image_view);
        arrowAnimationLeft = findViewById(R.id.arrow_animation_left_image_view);
        arrowAnimationTop = findViewById(R.id.arrow_animation_top_image_view);

        policeButton = findViewById(R.id.police_button);
        fireButton = findViewById(R.id.fire_fighters_button);
        ambulanceButton = findViewById(R.id.ambulance_button);

        toolbar = findViewById(R.id.sos_tool_bar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCurrentLocationAndSetAddress();
        startPulse();
        sosButton.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                stopPulse();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        initialX = v.getX();
                        initialY = v.getY();

                        animateButtonScale(v, 0.4f);
                        animateButtonScale(imgAnimation1, 0.4f);
                        animateButtonScale(imgAnimation2, 0.4f);
                        showAdditionalButtons();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float diffX = event.getRawX() - startX;
                        float diffY = event.getRawY() - startY;

                        imgAnimation1.setX(initialX + diffX);
                        imgAnimation1.setY(initialY + diffY);
                        imgAnimation2.setX(initialX + diffX);
                        imgAnimation2.setY(initialY + diffY);
                        v.setX(initialX + diffX);
                        v.setY(initialY + diffY);
                        break;

                    case MotionEvent.ACTION_UP:
                        startPulse();
                        animateButtonScale(v, 1.0f);
                        animateButtonScale(imgAnimation1, 1.0f);
                        animateButtonScale(imgAnimation2, 1.0f);
                        hideAdditionalButtons();

                        float finalX = event.getRawX();
                        float finalY = event.getRawY();
                        float diffFinalX = finalX - startX;
                        float diffFinalY = finalY - startY;

                        if (Math.abs(diffFinalX) > Math.abs(diffFinalY)) {
                            if (diffFinalX > 0) {
                                handleSwipeRight(diffFinalX);
                            } else {
                                handleSwipeLeft(diffFinalX);
                            }
                        } else {
                            // Vertical swipe
                            if (diffFinalY < 0) {
                                handleSwipeUp(diffFinalY);
                            }
                        }

                        v.setX(initialX);
                        v.setY(initialY);
                        imgAnimation1.setX(initialX);
                        imgAnimation1.setY(initialY);

                        imgAnimation2.setX(initialX);
                        imgAnimation2.setY(initialY);
                        break;
                }
                return true;
            }
        });
    }


    private void handleSwipeRight(float diffFinalX) {
        if (Math.abs(diffFinalX) > MIN_SWIPE_DISTANCE) {
            departmentNum = 3;
            checkAndRequestPermission();
        }
    }

    private void handleSwipeLeft(float diffFinalX) {
        if (Math.abs(diffFinalX) > MIN_SWIPE_DISTANCE) {
            departmentNum = 1;
            checkAndRequestPermission();
        }
    }

    private void handleSwipeUp(float diffFinalY) {
        if (Math.abs(diffFinalY) > MIN_SWIPE_DISTANCE) {
            departmentNum = 2;
            checkAndRequestPermission();
        }
    }

    private void startPulse() {
        handlerAnimation.post(runnable);
    }

    private void stopPulse() {
        handlerAnimation.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            imgAnimation1.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imgAnimation1.setScaleX(0.5f);
                            imgAnimation1.setScaleY(0.5f);
                            imgAnimation1.setAlpha(0.5f);
                        }
                    });

            imgAnimation2.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(700)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imgAnimation2.setScaleX(0.5f);
                            imgAnimation2.setScaleY(0.5f);
                            imgAnimation2.setAlpha(0.5f);
                        }
                    });

            handlerAnimation.postDelayed(this, 1500);
        }
    };

    private void checkAndRequestPermission() {
        if (checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        } else
            makeCall(departmentNum);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(departmentNum);
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CALL_PHONE)) {
                    showPermissionExplanationDialog();
                } else {
                    showAppSettingsDialog();
                }
            }
        }
    }

    private void makeCall(int departmentNumber) {
        //Default emergency response services number
        String phoneNumber;
        switch (departmentNumber){
            //Call police station
            case 1:
                phoneNumber = "tel:" + "999";
                break;
            //Call ambulance
            case 2:
                phoneNumber = "tel:" + "991";
                break;
            //Call fire department
            case 3:
                phoneNumber = "tel:" + "994";
                break;
            default:
                phoneNumber = "tel:" + "999";
        }
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        if (checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(dialIntent);
        }
    }

    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires the CALL_PHONE permission to call an ambulance.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAppSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires the CALL_PHONE permission to call an ambulance. " +
                        "Please go to the app settings and enable the permission.")
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void animateButtonScale(View view, float scale) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);

        AnimatorSet scaleAnimation = new AnimatorSet();
        scaleAnimation.playTogether(scaleX, scaleY);
        scaleAnimation.setDuration(200);
        scaleAnimation.start();
    }

    private void hideAdditionalButtons() {
        policeButton.setVisibility(View.INVISIBLE);
        fireButton.setVisibility(View.INVISIBLE);
        ambulanceButton.setVisibility(View.INVISIBLE);
        arrowAnimationRight.setVisibility(View.INVISIBLE);
        arrowAnimationLeft.setVisibility(View.INVISIBLE);
        arrowAnimationTop.setVisibility(View.INVISIBLE);
    }

    private void showAdditionalButtons() {
        policeButton.setVisibility(View.VISIBLE);
        fireButton.setVisibility(View.VISIBLE);
        ambulanceButton.setVisibility(View.VISIBLE);
        arrowAnimationRight.setVisibility(View.VISIBLE);
        arrowAnimationLeft.setVisibility(View.VISIBLE);
        arrowAnimationTop.setVisibility(View.VISIBLE);
    }

    private void getCurrentLocationAndSetAddress() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                String address = getAddressFromLocation(latitude, longitude);

                if(address.equals(""))
                    sosLocationInput.setText("404 Address Not Found");
                else
                    sosLocationInput.setText(address);
            }
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String addressString = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            for(Address address:addresses)
                System.out.println(address);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressString = address.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("Sos", "Error getting address from location", e);
        }

        return addressString;
    }


}
