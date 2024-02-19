package com.example.googlemap.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.googlemap.Pages.EditProfilePage;
import com.example.googlemap.R;
import com.example.googlemap.Provider.UserProvider;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ProfilePageFragment extends Fragment {

    private ImageView imageView;
    private TextInputEditText nameInputField, emailInputField, addressInputField, contactInputField, bloodTypeInputField;
    private Button editProfileButton;
    private static final int EDIT_PROFILE_REQUEST_CODE=69;

    private ActivityResultLauncher<Intent> editProfileLauncher;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_page_fragment, container, false);

        editProfileButton= view.findViewById(R.id.edit_profile_button);
        imageView = view.findViewById(R.id.profile_image);
        nameInputField = view.findViewById(R.id.nameInputField);
        emailInputField = view.findViewById(R.id.emailInputField);
        addressInputField = view.findViewById(R.id.addressInputField);
        contactInputField = view.findViewById(R.id.contactInputField);
        bloodTypeInputField = view.findViewById(R.id.bloodTypeInputField);

        nameInputField.setText(UserProvider.username);
        emailInputField.setText(UserProvider.email);
        addressInputField.setText(UserProvider.address);
        contactInputField.setText(UserProvider.contact);
        bloodTypeInputField.setText(UserProvider.bloodType);

        nameInputField.setEnabled(false);
        emailInputField.setEnabled(false);
        addressInputField.setEnabled(false);
        contactInputField.setEnabled(false);
        bloodTypeInputField.setEnabled(false);

        Picasso.get().load(UserProvider.profileUrl).fit().into(imageView);

        setUpEditButton();
        setUpStartActivityForResult();


        return view;
    }

    private void setUpEditButton(){
        editProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editProfileLauncher.launch(new Intent(getActivity(), EditProfilePage.class));
            }
        });
    }

    private void setUpStartActivityForResult(){
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        int resultCode = result.getData().getIntExtra("result_code",-1);
                        if (resultCode == EDIT_PROFILE_REQUEST_CODE) {

                            nameInputField.setText(UserProvider.username);
                            emailInputField.setText(UserProvider.email);
                            addressInputField.setText(UserProvider.address);
                            contactInputField.setText(UserProvider.contact);
                            bloodTypeInputField.setText(UserProvider.bloodType);
                            Picasso.get().load(UserProvider.profileUrl).fit().into(imageView);
                        }
                    }
                }
        );
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);

        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();

        dialog.show();

    }


}
