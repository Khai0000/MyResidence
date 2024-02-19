package com.example.googlemap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.googlemap.Pages.Home;
import com.example.googlemap.Pages.LoadingPage;
import com.example.googlemap.Pages.Login;
import com.example.googlemap.Pages.UpdateProfile;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    boolean backFromLocationSettings = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        if (checkLocationPermission()) {
            proceedAfterLocationPermission();
        } else {
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED || coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermission()) {
                proceedAfterLocationPermission();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void proceedAfterLocationPermission() {
        backFromLocationSettings=false;
        ImageView imageView = findViewById(R.id.loading_image_view);
        imageView.setBackgroundResource(R.drawable.logo_loading_animation);
        AnimationDrawable logoAnimation = (AnimationDrawable) imageView.getBackground();
        logoAnimation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                } else {
                    MyFireStore.FIRESTORE.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (task.getResult().get("email") == null) {
                                    startActivity(new Intent(MainActivity.this, UpdateProfile.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, Home.class));
                                }
                            }
                        }
                    });
                }
            }
        }, 2300);
    }

    private void showPermissionDeniedDialog() {
        backFromLocationSettings=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SpannableString titleString = new SpannableString("Permission Denied");
        titleString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString cancelString = new SpannableString("Cancel");
        cancelString.setSpan(new ForegroundColorSpan(Color.RED), 0, cancelString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString settingString = new SpannableString("Settings");
        settingString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, settingString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(titleString);
        builder.setMessage("Location permission is required to use this app. Please enable it in the app settings.");
        builder.setPositiveButton(settingString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        android.net.Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                backFromLocationSettings=true;
            }
        });
        builder.setNegativeButton(cancelString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(backFromLocationSettings)
        {
            if(checkLocationPermission())
                proceedAfterLocationPermission();
            else
                showPermissionDeniedDialog();
        }
    }
}
