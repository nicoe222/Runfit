package com.example.nicosetiawan.runfit.Profiler;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nicosetiawan.runfit.Activities.HomeActivity;
import com.example.nicosetiawan.runfit.Login.FrontActivity;
import com.example.nicosetiawan.runfit.Login.LoginActivity;
import com.example.nicosetiawan.runfit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);


        setupFirebaseAuth();


        Button btnsignout = (Button) view.findViewById(R.id.btn_logout);
        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().finish();
            }
        });

        return view;
    }


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

                    Intent intent = new Intent(getActivity(), FrontActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
