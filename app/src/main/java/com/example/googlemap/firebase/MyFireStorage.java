package com.example.googlemap.firebase;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.googlemap.dialog.MyCustomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MyFireStorage implements ImageUploadCallback{

    public static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public static void uploadImage(String collection, String uuid, boolean isPost, Uri picUri, Activity activity, ImageUploadCallback callback){

        StorageReference storageReference = firebaseStorage.getReference().child(collection).child(String.valueOf(uuid));
        storageReference.putFile(picUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                String imageUrl = task.getResult().toString();
                                callback.onImageUploaded(imageUrl);
                            }
                            else
                            {
                                callback.onImageUploadFailed("Failed to get image URL.");
                            }
                        }
                    });
                }
                else
                {
                    MyCustomDialog.showDialog("Error occurred","Couldn't upload photo. Please try again later",activity);

                }
            }
        });

    }

    @Override
    public void onImageUploaded(String imageUrl) {

    }

    @Override
    public void onImageUploadFailed(String errorMessage) {

    }
}
