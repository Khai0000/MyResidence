package com.example.googlemap.Pages;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.Chatroom.ChatRoom;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class ViewProfilePage extends AppCompatActivity {

    private ImageView imageView;
    private TextInputEditText nameInput,emailInput,addressInput,contactInput,bloodTypeInput;
    private Button goBackButton, editProfileButton, chatButton, chatDisableButton;
    private String postAuthorUid;
    private boolean option1Checked, option2Checked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_profile_page);

        imageView= findViewById(R.id.profile_image);
        nameInput= findViewById(R.id.nameInputField);
        emailInput= findViewById(R.id.emailInputField);
        addressInput= findViewById(R.id.addressInputField);
        contactInput= findViewById(R.id.contactInputField);
        bloodTypeInput= findViewById(R.id.bloodTypeInputField);

        Intent intent = getIntent();
        String authorName = intent.getStringExtra("username");
        String authorProfileUrl = intent.getStringExtra("profileUrl");

        Picasso.get().load(authorProfileUrl).fit().into(imageView);

        nameInput.setEnabled(false);
        emailInput.setEnabled(false);
        addressInput.setEnabled(false);
        contactInput.setEnabled(false);
        bloodTypeInput.setEnabled(false);

        postAuthorUid = intent.getStringExtra("postAuthorUid");

        option1Checked = false;
        option2Checked = false;


        goBackButton= findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get privacy settings and setup profile layout
        getPostAuthorPrivacySettings(intent, authorName, authorProfileUrl);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(postAuthorUid.equals(UserProvider.UID))
        {
            Picasso.get().load(UserProvider.profileUrl).fit().into(imageView);

            nameInput.setText(UserProvider.username);
            addressInput.setText(UserProvider.address);
            contactInput.setText(UserProvider.contact);
            bloodTypeInput.setText(UserProvider.bloodType);
        }
    }

    private void setUpProfileLayout(Intent intent, String authorName, String authorProfileUrl) {
        editProfileButton= findViewById(R.id.edit_profile_button);
        if(postAuthorUid.equals(UserProvider.UID))
        {
            //Set text for current logged in user
            nameInput.setText(authorName);
            emailInput.setText(intent.getStringExtra("email"));
            addressInput.setText(intent.getStringExtra("address"));
            contactInput.setText(intent.getStringExtra("contact"));
            bloodTypeInput.setText(intent.getStringExtra("bloodType"));

            editProfileButton.setVisibility(VISIBLE);
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ViewProfilePage.this,EditProfilePage.class));
                }
            });

        }
        else
        {
            setTextInView(option1Checked, authorName, intent);
            setIconVisibilityInView(option2Checked, authorName, authorProfileUrl);
        }
    }

    //Set text in the profile
    private void setTextInView(boolean option1, String authorName, Intent intent) {
        if (option1) {
            nameInput.setText(authorName);
            emailInput.setText("****");
            addressInput.setText("****");
            contactInput.setText("****");
            bloodTypeInput.setText("**");
        }
        else {
            nameInput.setText(authorName);
            emailInput.setText(intent.getStringExtra("email"));
            addressInput.setText(intent.getStringExtra("address"));
            contactInput.setText(intent.getStringExtra("contact"));
            bloodTypeInput.setText(intent.getStringExtra("bloodType"));
        }
    }
    //Set visibility of icons
    private void setIconVisibilityInView(boolean option2, String authorName, String authorProfileUrl) {
        if (option2) {
            chatDisableButton = findViewById(R.id.chat_disabled_button);
            chatDisableButton.setVisibility(VISIBLE);
        }
        else {
            chatButton = findViewById(R.id.chat_button);
            chatButton.setVisibility(VISIBLE);
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(ViewProfilePage.this, ChatRoom.class);
                    newIntent.putExtra("username",authorName);
                    newIntent.putExtra("profileUrl",authorProfileUrl);
                    newIntent.putExtra("userUid",postAuthorUid);
                    startActivity(newIntent);
                }
            });
        }
    }

    //get user privacy and checks if a user has which privacy settings turned on
    private void getPostAuthorPrivacySettings(Intent intent, String authorName, String authorProfileUrl) {
        MyFireStore.FIRESTORE.collection("users")
                .document(postAuthorUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                List<Map<String, Object>> privacyList = (List<Map<String, Object>>) snapshot.get("privacy");

                                if (privacyList != null && !privacyList.isEmpty()) {
                                    for (Map<String, Object> privacyOptions : privacyList) {
                                        if (privacyOptions.containsKey("option1")) {
                                            option1Checked = true;
                                        }

                                        if (privacyOptions.containsKey("option2") ) {
                                            option2Checked = true;
                                        }
                                    }
                                }
                                //Setup layout after getting privacy settings of author
                                setUpProfileLayout(intent, authorName, authorProfileUrl);
                            }
                        }
                    }
                });
    }
}
