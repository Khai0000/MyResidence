package com.example.googlemap.Pages;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;
import com.example.googlemap.dialog.MyCustomDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {


    private Button goBackButton, resetButton;
    private TextInputEditText emailInputField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        goBackButton= findViewById(R.id.go_back_button);
        resetButton= findViewById(R.id.reset_password_button);
        emailInputField=findViewById(R.id.email_input_field);

        setUpGoBackButton();
        setUpResetButton();

    }

    private void setUpGoBackButton(){
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setUpResetButton(){
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(emailInputField.getText().toString().trim()).matches())
                    emailInputField.setError("Please enter valid email address");
                else
                {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailInputField.getText().toString().trim());
                    MyCustomDialog.showDialog("Email has been sent","Please kindly check your email and reset password with the provided link",ForgotPassword.this,true);
                }
            }
        });
    }
}
