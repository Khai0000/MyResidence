package com.example.googlemap.Pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements GestureDetector.OnGestureListener {


    private static final String TAG ="Swipe Position";
    private static int MIN_DISTANCE=200;
    private GestureDetector gestureDetector;

    private TextInputEditText emailEditText,passwordEditText;

    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    private TextView forgotPasswordLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        firebaseAuth = FirebaseAuth.getInstance();

        forgotPasswordLink = findViewById(R.id.forgot_password_link);
        setUpForgotPasswordLink();

        loginButton= findViewById(R.id.loginButton);

        emailEditText= findViewById(R.id.emailInputField);
        passwordEditText= findViewById(R.id.passwordInputField);
        setUpInputField(emailEditText,50);
        setUpInputField(passwordEditText,16);

        TextInputLayout textInputLayout = findViewById(R.id.passwordInputLayout);
        final boolean[] isPasswordVisible = {false};

        textInputLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible[0] = !isPasswordVisible[0];

                if (isPasswordVisible[0]) {
                    textInputLayout.setStartIconDrawable(R.drawable.eye_open);
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    textInputLayout.setStartIconDrawable(R.drawable.eye_close);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Move the cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        gestureDetector = new GestureDetector(this,this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailEditText.setError("Please enter a valid email address");
                }
                else
                    loginUser(email,password);
            }
        });

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };

        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, backPressedCallback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {

        float deltaY = e1.getY() - e2.getY();

        if (Math.abs(deltaY) > MIN_DISTANCE && deltaY > 0) {
             Intent intent = new Intent(Login.this, Signup.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


            return true;
        }

        return false;
    }

    public void loginUser(String email,String password){
        try {
            Task task = firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        startActivity(new Intent(Login.this,LoadingPage.class));
                        finish();
                    }
                    else
                    {
                        showDialog("Login Failed", "Error: " + task.getException().getMessage());
                    }
                }
            });

        }
        catch(Exception e)
        {
            showDialog("Exception occured","Email and password can't be empty");
        }
    }

    private void showDialog(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);

        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
            }
        };

        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, backPressedCallback);
    }

    private void setUpInputField(TextInputEditText inputField,int wordLimit)
    {
        InputFilter filters[] = new InputFilter[1];
        filters[0]= new InputFilter.LengthFilter(wordLimit);

        inputField.setFilters(filters);
    }

    private void setUpForgotPasswordLink(){
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });
    }
}
