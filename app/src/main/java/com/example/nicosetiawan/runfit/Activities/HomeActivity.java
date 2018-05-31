package com.example.nicosetiawan.runfit.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nicosetiawan.runfit.Login.FrontActivity;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.example.nicosetiawan.runfit.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext = HomeActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Starting");

        SetupBottomNavigationView();
        initImageLoader();

        // FIREBASE
        setupFirebaseAuth();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void SetupBottomNavigationView() {
        Log.d(TAG, "SetupBottomNavigationView: Setting Up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.SetupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /**
     * -------------------------------------- FIREBASE ------------------------------------------------------------
     */

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: Check Current User");
        if (user == null) {
            Intent intent = new Intent(mContext, FrontActivity.class);
            startActivity(intent);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Starting Firebase");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                checkCurrentUser(user);
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
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



//END
}
