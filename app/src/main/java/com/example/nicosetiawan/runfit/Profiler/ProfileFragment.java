package com.example.nicosetiawan.runfit.Profiler;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.example.nicosetiawan.runfit.Models.UserAccountSettings;
import com.example.nicosetiawan.runfit.Models.UserSettings;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.example.nicosetiawan.runfit.Utils.FirebaseMethods;
import com.example.nicosetiawan.runfit.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private static final int ACTIVITY_NUM = 4;

    private Context mContext;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private CircleImageView mProfilePhoto;
    private TextView mFullname , mHeight , mWeight , mGender;

    // FIREBASE METHOD
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "onCreateView: Started");
       
       bottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);
       mContext = getActivity();
       toolbar = view.findViewById(R.id.profileToolBar);
       profileMenu = view.findViewById(R.id.profileMenuImage);
       mFirebaseMethods = new FirebaseMethods(getActivity());
       mFullname = view.findViewById(R.id.fullname);
       mWeight = view.findViewById(R.id.weight);
       mHeight = view.findViewById(R.id.height);
       mGender = view.findViewById(R.id.gender);
       mProfilePhoto = view.findViewById(R.id.CircleImage);

        setupToolbar();
       SetupBottomNavigationView();
        setupFirebaseAuth();
       return view;
    }


    private void setProfileWidget(UserSettings userSettings){

        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mFullname.setText(settings.getFullname());
        mGender.setText(settings.getGender());
        mHeight.setText(String.valueOf(settings.getHeight()));
        mWeight.setText(String.valueOf(settings.getWeight()));
    }


    private void setupToolbar(){
        ((ProfilerActivity)getActivity()).setSupportActionBar(toolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigation");
                Intent intent = new Intent(mContext, Settings.class);
                startActivity(intent);
            }
        });
    }

    private void SetupBottomNavigationView(){
        Log.d(TAG, "SetupBottomNavigationView: Setting Up BottomNavigationView");
        BottomNavigationViewHelper.SetupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /**
     * --------------------------------------- FIREBASE METHOD ------------------------------------
     */

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Starting Firebase");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // RETRIEVE USER INFORMATION DATA FROM DATABASE
                setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
