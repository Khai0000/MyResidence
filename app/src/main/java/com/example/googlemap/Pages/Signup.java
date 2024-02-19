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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Timer;
import java.util.TimerTask;

public class Signup extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private static int MIN_DISTANCE=200;
    private GestureDetector gestureDetector;
    private FirebaseAuth firebaseAuth;
    private Button signupButton;
    private TextInputEditText emailEditText,passwordEditText;
    private PasswordValidator passwordValidator;

    private Timer verificationTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        gestureDetector=new GestureDetector(this,this);
        firebaseAuth= FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailInputField);
        passwordEditText = findViewById(R.id.passwordInputField);
        signupButton= findViewById(R.id.signupButton);
        passwordValidator = setupPasswordValidator();
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


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                RuleResult result = passwordValidator.validate(new PasswordData(new String(password)));
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()&&result.isValid())
                {
                    passwordEditText.setError(null);
                    emailEditText.setError(null);

                    try {
                        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    sendEmailVerification(); // Send email verification
                                }
                                else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        signInAndCheckEmailVerification(email, password);
                                    } else {
                                        showDialog("Signup Failed", task.getException().getMessage(), false);
                                    }
                                }
                            }
                        });
                    }
                    catch(Exception e)
                    {
                        showDialog("Exception occured","Email and password can't be empty",false);
                    }
                }
                else
                {
                    if(!result.isValid())
                    {
                        passwordEditText.setError(passwordValidator.getMessages(result).toString());
                    }

                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        emailEditText.setError("Please enter a valid email address");
                    }
                }

            }
        });

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
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
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        // Calculate the difference in Y coordinates
        float deltaY = e1.getY() - e2.getY();

        // Check if the swipe is in the upward direction and its distance is greater than the defined minimum distance
        if (Math.abs(deltaY) > MIN_DISTANCE && deltaY < 0) {
            startActivity(new Intent(Signup.this, Login.class));
            finish();

            return true;
        }

        return false;
    }

    private PasswordValidator setupPasswordValidator(){

        return new PasswordValidator(
                // length between 8 and 16 characters
                new LengthRule(8, 16),

                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),

                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // no whitespace
                new WhitespaceRule());
    }

    private void showDialog(String title,String message, boolean isSignup){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);

        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();

        if(isSignup)
        {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    startActivity(new Intent(Signup.this, UpdateProfile.class));
                    finish();
                }
            });
        }
        dialog.show();

    }

    private void setUpInputField(TextInputEditText inputField,int wordLimit)
    {
        InputFilter filters[] = new InputFilter[1];
        filters[0]= new InputFilter.LengthFilter(wordLimit);

        inputField.setFilters(filters);
    }


    private void sendEmailVerification() {
        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showVerificationDialog();
                            startEmailVerificationCheck();
                        } else {
                            showDialog("Email Verification Failed", task.getException().getMessage(), false);
                        }
                    }
                });
    }

    private void checkEmailVerificationStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(Signup.this, UpdateProfile.class));
                        finish();
                    } else {
                        sendEmailVerification();
                    }
                }
            });
        }
    }
    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Verification Email Sent")
                .setMessage("A verification email has been sent to your email address. Please check your email to verify your account.")
                .setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Resend verification email
                        sendEmailVerification();
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startEmailVerificationCheck() {
        if(verificationTimer==null)
        {
            verificationTimer = new Timer();
            verificationTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(user.isEmailVerified())
                                {
                                    if (verificationTimer != null) {
                                        verificationTimer.cancel();
                                        verificationTimer.purge();
                                    }
                                    startActivity(new Intent(Signup.this, UpdateProfile.class));
                                    finish();

                                }
                            }
                        });

                    }
                }
            }, 0, 3000);
        }
    }



    private void signInAndCheckEmailVerification(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkEmailVerificationStatus();
                } else {
                    showDialog("Sign In Failed", task.getException().getMessage(), false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationTimer != null) {
            verificationTimer.cancel();
            verificationTimer.purge();
        }
    }
}
