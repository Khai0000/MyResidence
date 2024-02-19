package com.example.googlemap.Pages;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    private Button uploadImageButton;
    private ImageView imageView;

    private TextInputEditText nameInputField,emailInputField,addressInputField,contactInputField,bloodTypeInputField;
    private Button confirmButton;

    private FirebaseAuth fireAuth;
    private FirebaseStorage fireStorage;

    private FirebaseFirestore firestore;
    private Uri profileUrI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        imageView = findViewById(R.id.profile_image);

        nameInputField = findViewById(R.id.nameInputField);
        emailInputField = findViewById(R.id.emailInputField);
        addressInputField = findViewById(R.id.addressInputField);
        contactInputField = findViewById(R.id.contactInputField);
        bloodTypeInputField = findViewById(R.id.bloodTypeInputField);

        fireStorage= FirebaseStorage.getInstance();
        fireAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();

        emailInputField.setText(fireAuth.getCurrentUser().getEmail());
        emailInputField.setEnabled(false);

        setUpTextInputField(nameInputField,30);
        setUpTextInputField(emailInputField,50);
        setUpTextInputField(addressInputField,100);
        setUpTextInputField(contactInputField,11);
        setUpTextInputField(bloodTypeInputField,2);

        confirmButton= findViewById(R.id.confirmButton);
        setUpConfirmButton(confirmButton);

        uploadImageButton = findViewById(R.id.upload_image_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UpdateProfile.this)
                        .crop()	 //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
            }
        };

        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, backPressedCallback);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageView.setImageURI(uri);
        profileUrI=uri;
    }


    private void setUpTextInputField(TextInputEditText inputField, int wordLimit) {
        // Create an InputFilter to limit the input
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(wordLimit); // Set the character limit

        // Apply the InputFilter to the TextInputEditText
        inputField.setFilters(filters);
    }


    private void setUpConfirmButton(Button confirmButton)
    {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputValidation())
                {
                    String userUid = fireAuth.getCurrentUser().getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put("profileUrl", profileUrI);


                    try{

                        StorageReference reference = fireStorage.getReference().child("profilePics").child(userUid);
                        UploadTask uploadTask = reference.putFile(profileUrI);

                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String uid = fireAuth.getCurrentUser().getUid();
                                            Map<String,Object> userData = new HashMap<>();
                                            userData.put("username",nameInputField.getText().toString().trim());
                                            userData.put("email",emailInputField.getText().toString().trim());
                                            userData.put("address",addressInputField.getText().toString().trim());
                                            userData.put("contact",contactInputField.getText().toString().trim());
                                            userData.put("bloodType",bloodTypeInputField.getText().toString().trim());
                                            userData.put("profileUrl",task.getResult().toString());

                                            firestore.collection("users").document(uid).set(
                                                    userData
                                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        showDialog("Update successfully","Please proceed with home page.",true);
                                                    }
                                                    else
                                                    {
                                                        showDialog("Failed to update profile","Please try again later.",false);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                                else
                                {
                                    showDialog("Upload Image failed","Please try again later.",false);
                                }
                            }
                        });
                    }
                    catch(Exception e)
                    {
                        showDialog("Exception Occurred",e.getMessage().toString(),false);
                    }
                }
            }
        });
    }

    private void showDialog(String title,String message,boolean success){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);

        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(success)
                    {
                        startActivity(new Intent(UpdateProfile.this, LoadingPage.class));
                        finish();
                    }
                }
            });

        dialog.show();

    }

    private boolean inputValidation(){
        boolean isValid = true;

        if(nameInputField.getText().toString().trim().equals(""))
        {
            nameInputField.setError("Please enter a name");
            isValid=false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailInputField.getText().toString().trim()).matches())
        {
            emailInputField.setError("Please enter a valid email");
            isValid=false;

        }
        if(addressInputField.getText().toString().trim().equals(""))
        {
            addressInputField.setError("Please enter an address");
            isValid=false;
        }
        if(contactInputField.getText().toString().trim().equals(""))
        {
            contactInputField.setError("Please enter a phone contact");
            isValid=false;
        }
        if(bloodTypeInputField.getText().toString().trim().equals(""))
        {
            bloodTypeInputField.setError("Please enter a name");
            isValid=false;
        }
        if(profileUrI==null)
        {
            showDialog("Please pick a photo","The photo must show your face",false);
        }
        return isValid;
    }

}
