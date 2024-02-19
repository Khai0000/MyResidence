
package com.example.googlemap.Pages;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.googlemap.Fragments.EmergencyFragment;
import com.example.googlemap.Fragments.EmergencyLocationSearchFragment;
import com.example.googlemap.Fragments.MyMapFragment;
import com.example.googlemap.Fragments.ProfilePageFragment;
import com.example.googlemap.Fragments.RecentChatFragment;
import com.example.googlemap.MainActivity;
import com.example.googlemap.MyNavigator.CustomViewPager2Adapter;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private BottomNavigationView bottomNavigationView;

    private ViewPager2 viewPager2;

    private CustomViewPager2Adapter customViewPager2Adapter;

    private MaterialToolbar toolbar;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private LinearLayout logoutLayout;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
            }
        }, 4000);

        logoutLayout = findViewById(R.id.logout_layout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.app_bar_tool_bar);
        setUpToolBar();

        viewPager2 = findViewById(R.id.view_pager2);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new EmergencyFragment(viewPager2));
        fragmentArrayList.add(new EmergencyLocationSearchFragment());
        fragmentArrayList.add(new MyMapFragment());
        fragmentArrayList.add(new RecentChatFragment());
        fragmentArrayList.add(new ProfilePageFragment());

        customViewPager2Adapter = new CustomViewPager2Adapter(this, fragmentArrayList);
        viewPager2.setAdapter(customViewPager2Adapter);

        viewPager2.setCurrentItem(2);
        bottomNavigationView.setSelectedItemId(R.id.map_page);
        viewPager2.setUserInputEnabled(false);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.emergency_page);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.emergency_location_page);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.map_page);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.recent_chat_page);
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.profile_page);
                        break;

                }
                super.onPageSelected(position);
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.emergency_page) {
                    viewPager2.setCurrentItem(0);
                    return true;
                }
                if (itemId == R.id.emergency_location_page) {
                    viewPager2.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.map_page) {
                    viewPager2.setCurrentItem(2);
                    return true;
                } else if (itemId == R.id.recent_chat_page) {
                    viewPager2.setCurrentItem(3);
                    return true;
                } else if (itemId == R.id.profile_page) {
                    viewPager2.setCurrentItem(4);
                    return true;
                }


                return false;
            }
        });

        getUserData();

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
            }
        };


        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(backPressedCallback);
    }


    private void getUserData() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    UserProvider.username = snapshot.get("username").toString();
                    UserProvider.email = snapshot.get("email").toString();
                    UserProvider.address = snapshot.get("address").toString();
                    UserProvider.bloodType = snapshot.get("bloodType").toString();
                    UserProvider.contact = snapshot.get("contact").toString();
                    UserProvider.profileUrl = snapshot.get("profileUrl").toString();
                    UserProvider.UID = user.getUid();

                } else {
                    showDialog("Failed to load profile", "Please restart the application");
                }

            }
        });

    }

    private void setUpToolBar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START, true);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(Home.this, Sos.class));
                return true;
            }
        });

        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getTitle().equals("About Us")) {
                    startActivity(new Intent(Home.this, AboutUs.class));
                    return true;
                } else if (item.getTitle().equals("Privacy Settings")) {
                    startActivity(new Intent(Home.this, PrivacySettings.class));
                    return true;
                } else if (item.getTitle().equals("Delete Account")) {
                    showDeleteAccountDialog();
                    return true;
                }

                return false;
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();
            }
        });
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void showDeleteAccountDialog() {

        SpannableString bodyMessage = new SpannableString("Are you sure you want to delete your account?");
        bodyMessage.setSpan(new ForegroundColorSpan(Color.WHITE), 0, bodyMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString yesString = new SpannableString("Yes");
        yesString.setSpan(new ForegroundColorSpan(Color.RED), 0, yesString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account")
                .setMessage(bodyMessage)
                .setNegativeButton(yesString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Home.this, MainActivity.class));
                                        finish();
                                    } else {
                                        System.out.println(task.getException().getMessage().toString());
                                        // Handle the error, e.g., display a message to the user
                                        Toast.makeText(getApplicationContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing or handle accordingly
                        dialog.dismiss();
                    }
                })
                .create();
        AlertDialog dialog = builder.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2d2d2d")));
    }


    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,R.style.FullScreenDialog);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.loading_page);
        loadingDialog.setCancelable(false);
        Window window = loadingDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


}
