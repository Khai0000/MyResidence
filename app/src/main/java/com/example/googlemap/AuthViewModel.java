package com.example.googlemap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> userLiveData;

    public LiveData<FirebaseUser> getUserLiveData(){
        if(userLiveData==null)
            userLiveData=new MutableLiveData<>();

        return userLiveData;
    }

    public void setUser(FirebaseUser user) {
        if (userLiveData != null) {
            userLiveData.setValue(user);
        }
    }
}
