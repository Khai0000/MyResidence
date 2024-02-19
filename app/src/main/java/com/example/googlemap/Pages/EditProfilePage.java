package com.example.googlemap.Pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.ImageUploadCallback;
import com.example.googlemap.firebase.MyFireStorage;
import com.example.googlemap.firebase.MyFireStore;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfilePage extends AppCompatActivity {


    private TextInputEditText nameInput, emailInput, addressInput, contactInput, bloodTypeInput;
    private ImageView profileImageView;

    private Button confirmButton, uploadImageButton,goBackButton;

    private Uri profileUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        setUpInputField();
        setUpUploadButton();
        setUpConfirmButton();

        goBackButton= findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void findAllInputField() {
        nameInput = findViewById(R.id.nameInputField);
        emailInput = findViewById(R.id.emailInputField);
        addressInput = findViewById(R.id.addressInputField);
        contactInput = findViewById(R.id.contactInputField);
        bloodTypeInput = findViewById(R.id.bloodTypeInputField);
        profileImageView = findViewById(R.id.profile_image);
    }

    private void setUpInputField() {

        findAllInputField();

        nameInput.setText(UserProvider.username);
        emailInput.setText(UserProvider.email);
        addressInput.setText(UserProvider.address);
        contactInput.setText(UserProvider.contact);
        bloodTypeInput.setText(UserProvider.bloodType);
        Picasso.get().load(UserProvider.profileUrl).into(profileImageView);

        emailInput.setEnabled(false);
        setUpInputFilters(nameInput, 30);
        setUpInputFilters(addressInput, 100);
        setUpInputFilters(contactInput, 11);
        setUpInputFilters(bloodTypeInput, 2);
    }

    private void setUpInputFilters(TextInputEditText inputField, int wordLimit) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(wordLimit);

        inputField.setFilters(filters);
    }

    private void setUpUploadButton() {
        uploadImageButton = findViewById(R.id.upload_image_button);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfilePage.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            profileImageView.setImageURI(uri);
            profileUri = uri;
        }
    }

    private void setUpConfirmButton() {
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameInput.getText().toString();
                String address = addressInput.getText().toString();
                String contact = contactInput.getText().toString();
                String bloodType = bloodTypeInput.getText().toString();


                if (inputValidation(name, address, contact, bloodType)) {
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("username", name);
                    newData.put("address", address);
                    newData.put("contact", contact);
                    newData.put("bloodType", bloodType);

                    UserProvider.username = name;
                    UserProvider.address = address;
                    UserProvider.bloodType = bloodType;
                    UserProvider.contact= contact;

                    if (profileUri != null) {
                        MyFireStorage.uploadImage("profilePics", UserProvider.UID, false, profileUri, EditProfilePage.this, new ImageUploadCallback() {
                            @Override
                            public void onImageUploaded(String imageUrl) {
                                newData.put("profileUrl", imageUrl);
                                UserProvider.profileUrl = imageUrl;

                                MyFireStore.updateData("users", UserProvider.UID, newData, EditProfilePage.this);
                                finishAndReturnUpdatedData();
                            }

                            @Override
                            public void onImageUploadFailed(String errorMessage) {

                            }
                        });
                    }
                    else {
                        MyFireStore.updateData("users", UserProvider.UID, newData, EditProfilePage.this);
                        finishAndReturnUpdatedData();
                    }
                }
            }
        });
    }

    private boolean inputValidation(String name, String address, String contact, String bloodType) {
        boolean isValid = true;

        if (name.equals("")) {
            nameInput.setError("Please enter a name");
            isValid = false;
        }

        if (address.equals("")) {
            addressInput.setError("Please enter an address");
            isValid = false;
        }
        if (contact.equals("")) {
            contactInput.setError("Please enter a phone contact");
            isValid = false;
        }
        if (bloodType.equals("")) {
            bloodTypeInput.setError("Please enter a name");
            isValid = false;
        }

        return isValid;
    }

    private void finishAndReturnUpdatedData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result_code", 69);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
