package com.example.googlemap.Pages;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrivacySettings extends AppCompatActivity {

    private Toolbar toolbar;
    private ToggleButton toggleButton1, toggleButton2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_settings);

        toggleButton1 = findViewById(R.id.toggleButton1);
        toggleButton2 = findViewById(R.id.toggleButton2);

        toolbar = findViewById(R.id.about_us_tool_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserPrivacySettings();
        setUpToggleButton1();
        setUpToggleButton2();
    }

    private void getUserPrivacySettings() {
        MyFireStore.FIRESTORE.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                            toggleButton1.setChecked(true);
                                        }

                                        if (privacyOptions.containsKey("option2") ) {
                                            toggleButton2.setChecked(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }
    private void setUpToggleButton1() {
        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = toggleButton1.isChecked();
                System.out.println(isChecked);
                if (isChecked) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("option1", true);
                    MyFireStore.FIRESTORE.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                            "privacy", FieldValue.arrayUnion(dataMap)
                    );

                } else {
                    final DocumentReference userDocRef = MyFireStore.FIRESTORE.collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            List<Map<String, Object>> privacyList = (List<Map<String, Object>>) task.getResult().get("privacy");

                            if (privacyList == null) {
                                return; // Nothing to remove if the list is null
                            }

                            // Using an iterator to avoid ConcurrentModificationException
                            Iterator<Map<String, Object>> iterator = privacyList.iterator();
                            while (iterator.hasNext()) {
                                Map<String, Object> privacyOptions = iterator.next();
                                if (privacyOptions.containsKey("option1")) {
                                    iterator.remove();
                                }
                            }

                            userDocRef.update("privacy", privacyList);
                        }
                    });
                }
            }
        });
    }

    private void setUpToggleButton2() {
        toggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = toggleButton2.isChecked();
                System.out.println(isChecked);
                if (isChecked) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("option2", true);
                    MyFireStore.FIRESTORE.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(
                            "privacy", FieldValue.arrayUnion(dataMap)
                    );

                } else {
                    final DocumentReference userDocRef = MyFireStore.FIRESTORE.collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            List<Map<String, Object>> privacyList = (List<Map<String, Object>>) task.getResult().get("privacy");

                            if (privacyList == null) {
                                return; // Nothing to remove if the list is null
                            }

                            // Using an iterator to avoid ConcurrentModificationException
                            Iterator<Map<String, Object>> iterator = privacyList.iterator();
                            while (iterator.hasNext()) {
                                Map<String, Object> privacyOptions = iterator.next();
                                if (privacyOptions.containsKey("option2")) {
                                    iterator.remove();
                                }
                            }

                            userDocRef.update("privacy", privacyList);
                        }
                    });
                }
            }
        });
    }

}
