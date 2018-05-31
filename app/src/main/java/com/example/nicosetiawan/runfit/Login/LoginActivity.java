package com.example.nicosetiawan.runfit.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicosetiawan.runfit.Activities.HomeActivity;
import com.example.nicosetiawan.runfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


    private EditText mEmail,mPassword;
    TextView txt;
    Typeface tp;
    private TextView wait;
    private ProgressBar mProgressbar;
    private Context mContext;

    //FIREBASE DECLARATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );


        txt = findViewById(R.id.logo1);
        tp = Typeface.createFromAsset(this.getAssets(), "fonts/airstrikehalf.ttf");
        txt.setTypeface(tp);
        mEmail =  findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mProgressbar = findViewById(R.id.progressbar);
        mProgressbar.setVisibility(View.GONE);
        wait = findViewById(R.id.wait);
        wait.setVisibility(View.GONE);
        mContext = LoginActivity.this;

        setupFirebaseAuth();
        init();
    }

    /**
     * -------------------------------------- FIREBASE ------------------------------------------------------------
     */

    private void init(){
        Button btnlogin = findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)){

                    Toast.makeText(mContext, "Please Fill Username And Password", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressbar.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        try {

                                            if (user.isEmailVerified()) {
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(mContext, "Verified Your Email", Toast.LENGTH_SHORT).show();
                                                mProgressbar.setVisibility(View.GONE);
                                                wait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        } catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException" +e.getMessage() );
                                        }
                                        mProgressbar.setVisibility(View.GONE);
                                        wait.setVisibility(View.GONE);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressbar.setVisibility(View.GONE);
                                        wait.setVisibility(View.GONE);

                                    }

                                }
                            });
                }
            }
        });

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
             startActivity(intent);
             finish();
        }

    }


    private boolean isStringNull(String string){

        if (string.equals("")){
            return true;
        } else {
            return false;
        }
    }

    /**
     * ----------------------------------- FIREBASE BASIC METHOD ------------------------------------------------------
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Starting Firebase");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: Sign In");
                } else {
                    Log.d(TAG, "onAuthStateChanged: Sign Out");
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    
}
