package com.example.googlemap.firebase;

public interface ImageUploadCallback {

    void onImageUploaded(String imageUrl);
    void onImageUploadFailed(String errorMessage);
}
