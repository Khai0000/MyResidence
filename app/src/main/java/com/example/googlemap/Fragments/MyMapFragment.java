package com.example.googlemap.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.googlemap.HazardStories.HazardStory;
import com.example.googlemap.HazardStories.StoryAdapter;
import com.example.googlemap.HttpRequest;
import com.example.googlemap.Pages.PostDetails;
import com.example.googlemap.R;
import com.example.googlemap.Pages.UploadPost;
import com.example.googlemap.dialog.MyCustomDialog;
import com.example.googlemap.firebase.MyFireStorage;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;

    private SupportMapFragment mapFragment;

    private Button uploadPostButton, refreshButton;

    public static StoryAdapter storyAdapter;
    public static HashMap<Marker, Map<String, Object>> markerMap = new HashMap<>();
    public static ArrayList<HazardStory> storyList = new ArrayList<>();

    private RecyclerView storiesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map, container, false);

        uploadPostButton = view.findViewById(R.id.map_upload_post_button);
        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UploadPost.class));
            }
        });

        refreshButton = view.findViewById(R.id.map_refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.setEnabled(false);
                mMap.clear();
                storyList.clear();
                addAllPostsMarkerToMap();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshButton.setEnabled(true);
                    }
                }, 5000);
            }
        });


        storiesRecyclerView = view.findViewById(R.id.recycler_view);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        storyAdapter = new StoryAdapter(getContext(), storyList);
        storiesRecyclerView.setAdapter(storyAdapter);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableLocation();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setMinZoomPreference(12);

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    handleLocation(location);
                    locManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);

        }

        listenToPostsChanges();
        setMarkerOnClickListener();
        setUpMap();
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        return false;
    }

    public void listenToPostsChanges() {

        CollectionReference postsReference = MyFireStore.FIRESTORE.collection("posts");
        postsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override

            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    MyCustomDialog.showDialog("Error Occurred", "Failed to fetch hazards surrounding you. Please try again later", getActivity());
                    return;
                }

                if (!value.isEmpty()) {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        switch (documentChange.getType()) {
                            case ADDED:
                                addOnePostMarketToMap(documentChange);
                                addNewStory(documentChange);
                                break;
                            case REMOVED:
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    public void addAllPostsMarkerToMap() {

        MyFireStore.FIRESTORE.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        double latitude = snapshot.getDouble("latitude");
                        double longitude = snapshot.getDouble("longitude");
                        String category = snapshot.getString("category");
                        String title = snapshot.getString("title");
                        String description = snapshot.getString("description");
                        String postImageUrl = snapshot.getString("postUrl");
                        String userUid = snapshot.getString("userUid");
                        String username = snapshot.getString("username");
                        String postId = snapshot.getId();

                        addMarker(latitude, longitude, category, title, description, postImageUrl, postId, username, userUid);
                        addNewStory(postId, userUid, postImageUrl, title, description, username);
                    }
                } else {
                    Log.e("Map hazards data error", "Failed to get data hazards for the map");
                }
            }
        });
    }

    public void addOnePostMarketToMap(DocumentChange documentChange) {
        QueryDocumentSnapshot snapshot = documentChange.getDocument();

        double latitude = snapshot.getDouble("latitude");
        double longitude = snapshot.getDouble("longitude");
        String category = snapshot.getString("category");
        String title = snapshot.getString("title");
        String description = snapshot.getString("description");
        String postImageUrl = snapshot.getString("postUrl");
        String postId = snapshot.getId();
        String postAuthor = snapshot.getString("username");
        String authorUid = snapshot.getString("userUid");
        addMarker(latitude, longitude, category, title, description, postImageUrl, postId, postAuthor, authorUid);
    }

    private void addMarker(double latitude, double longitude, String category, String title, String description, String postImageUrl, String postId, String postAuthor, String authorUid) {


        StorageReference storageReference = MyFireStorage.firebaseStorage.getReference().child("hazard_icons").child(category);
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    Uri imageUri = Uri.parse(task.getResult().toString());

                    Picasso.get()
                            .load(imageUri)
                            .resize(150, 150)
                            .into(
                                    new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            BitmapDescriptor customMarker = BitmapDescriptorFactory.fromBitmap(bitmap);
                                            Map<String, Object> postData = new HashMap<>();
                                            postData.put("title", title);
                                            postData.put("description", description);
                                            postData.put("postImageUrl", postImageUrl);
                                            postData.put("postId", postId);
                                            postData.put("postAuthor", postAuthor);
                                            postData.put("authorUid", authorUid);

                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(latitude, longitude))
                                                    .title("hazard")
                                                    .icon(customMarker)
                                                    .zIndex(10)
                                                    .flat(true));
                                            markerMap.put(marker, postData);

                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                            Log.e("Map hazards icon error", "Failed to load hazards icons");

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    }
                            );
                } else {
                    Log.e("Map hazards adding error", "Failed to add hazards to the map");
                }
            }
        });


    }

    public void setUpMap() {
        mMap.setOnMyLocationButtonClickListener(this);
    }

    public void setMarkerOnClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                if (!marker.getTitle().equals("hazard"))
                    return false;

                Map<String, Object> markerData = markerMap.get(marker);

                Intent intent = new Intent(getActivity(), PostDetails.class);
                intent.putExtra("postAuthor", markerData.get("postAuthor").toString());
                intent.putExtra("postId", markerData.get("postId").toString());
                intent.putExtra("title", markerData.get("title").toString());
                intent.putExtra("description", markerData.get("description").toString());
                intent.putExtra("postImageUrl", markerData.get("postImageUrl").toString());
                intent.putExtra("authorUid", markerData.get("authorUid").toString());

                startActivity(intent);
                return true;
            }
        });
    }

    private void addNewStory(String postId, String userUid, String postImageUrl, String title, String description, String postAuthor) {

        MyFireStore.FIRESTORE.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    storyList.add(new HazardStory(postId, userUid, postImageUrl, postImageUrl, title, description, task.getResult().getString("username")));
                }
                storiesRecyclerView.getAdapter().notifyDataSetChanged();

            }
        });
    }

    private void addNewStory(DocumentChange documentChange) {
        DocumentSnapshot documentSnapshot = documentChange.getDocument();
        String userUid = documentSnapshot.getString("userUid");
        String postImageUrl = documentSnapshot.getString("postUrl");
        String title = documentSnapshot.getString("title");
        String description = documentSnapshot.getString("description");
        String postId = documentSnapshot.getId();
        MyFireStore.FIRESTORE.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                    storyList.add(new HazardStory(postId, userUid, postImageUrl, postImageUrl, title, description, task.getResult().getString("username")));
                storiesRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void handleLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng userLocation = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapFragment != null) {
            mapFragment.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            mMap.getUiSettings().setAllGesturesEnabled(true);
        }
    }


}