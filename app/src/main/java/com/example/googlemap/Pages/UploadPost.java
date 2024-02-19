package com.example.googlemap.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.dialog.MyCustomDialog;
import com.example.googlemap.firebase.ImageUploadCallback;
import com.example.googlemap.firebase.MyFireStorage;
import com.example.googlemap.firebase.MyFireStore;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPost extends AppCompatActivity {

    private Button uploadImageButton, uploadPostButton, goBackButton;

    private ImageView imageView;

    private Spinner hazardSpinner;

    private TextInputEditText postTitleTextInput, postCaptionTextInput;
    private Uri postUri;


    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_post);

        uploadImageButton = findViewById(R.id.upload_image_button);
        uploadPostButton = findViewById(R.id.upload_post_button);
        goBackButton = findViewById(R.id.go_back_button);
        imageView = findViewById(R.id.post_image);
        progressBar = findViewById(R.id.progress_bar);

        postTitleTextInput = findViewById(R.id.postTitleInputField);
        postCaptionTextInput = findViewById(R.id.postCaptionInputField);
        setUpInputField(postTitleTextInput, 50,"Post Title");
        setUpInputField(postCaptionTextInput, 300,"Post Descriptions");


            hazardSpinner = findViewById(R.id.category_selectors);
        setUpSpinner();


        setUploadImageButton();
        setGoBackButton();
        setUploadPostButton();
        setUpSpinner();
    }


    private void setUpSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_items, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardSpinner.setAdapter(adapter);
        hazardSpinner.setSelection(0);
    }

    private void setUploadPostButton() {
        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canUpload = true;

                if (postTitleTextInput.getText().toString().trim().equals("")) {
                    postTitleTextInput.setError("Post title can't be empty");
                    canUpload = false;
                }
                if (postCaptionTextInput.getText().toString().trim().equals("")) {
                    postCaptionTextInput.setError("Post description can't be empty");
                    canUpload = false;
                }
                if (postUri == null) {
                    MyCustomDialog.showDialog("Image can't be empty", "Please upload an image", UploadPost.this);
                    canUpload = false;
                }
                if (hazardSpinner.getSelectedItem().toString().equals("Select a category here")) {
                    MyCustomDialog.showDialog("Invalid categories", "Please select a specific type of hazard", UploadPost.this);
                    canUpload = false;
                }

                if (canUpload) {

                    uploadImageButton.setVisibility(View.GONE);
                    uploadPostButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    UUID uuid = UUID.randomUUID();
                    LocationManager locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(UploadPost.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(UploadPost.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        MyCustomDialog.showDialog("Couldn't access current location", "Please enable location service permission", UploadPost.this, true);
                        return;
                    }

                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    MyFireStorage.uploadImage("posts", String.valueOf(uuid), true, postUri, UploadPost.this, new ImageUploadCallback() {
                        @Override
                        public void onImageUploaded(String imageUrl) {
                            Map<String, Object> postData = new HashMap<>();
                            postData.put("userUid", UserProvider.UID);
                            postData.put("username", UserProvider.username);
                            postData.put("title", postTitleTextInput.getText().toString().trim());
                            postData.put("description", postCaptionTextInput.getText().toString().trim());
                            postData.put("latitude", location.getLatitude());
                            postData.put("longitude", location.getLongitude());
                            postData.put("postId", uuid);
                            postData.put("postUrl", imageUrl);
                            postData.put("category", hazardSpinner.getSelectedItem().toString());
                            postData.put("comments", new ArrayList<Map<String, Object>>());
                            MyFireStore.uploadData("posts", uuid.toString(), postData, UploadPost.this);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadPostButton.setVisibility(View.VISIBLE);
                                    uploadImageButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 300);
                        }

                        @Override
                        public void onImageUploadFailed(String errorMessage) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadPostButton.setVisibility(View.VISIBLE);
                                    uploadImageButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 300);
                        }
                    });
                }
            }
        });

    }


    private void setUploadImageButton() {
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UploadPost.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void setUpInputField(TextInputEditText inputField, int wordLimit,String hintTitle) {
        InputFilter filters[] = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(wordLimit);

        SpannableString hintString = new SpannableString(hintTitle);
        int textColor = Color.parseColor("#343434");
        hintString.setSpan(new ForegroundColorSpan(textColor), 0, hintString.length(), 0);

        inputField.setFilters(filters);

        inputField.setHint(hintString);
        inputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If the EditText is focused, set the hint to an empty string
                if (hasFocus) {
                    inputField.setHint("");
                } else {
                    // If not focused, set the hint to the original hint
                    inputField.setHint(hintString);
                }
            }
        });
    }

    private void setGoBackButton() {
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageView.setImageURI(uri);
        postUri = uri;
    }

}
