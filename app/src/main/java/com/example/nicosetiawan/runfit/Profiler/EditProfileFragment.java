package com.example.nicosetiawan.runfit.Profiler;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nicosetiawan.runfit.Dialogs.ConfirmPasswordDialog;
import com.example.nicosetiawan.runfit.Login.RegistrationActivity;
import com.example.nicosetiawan.runfit.Models.User;
import com.example.nicosetiawan.runfit.Models.UserAccountSettings;
import com.example.nicosetiawan.runfit.Models.UserSettings;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.FirebaseMethods;
import com.example.nicosetiawan.runfit.Utils.Permissions;
import com.example.nicosetiawan.runfit.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawnlin.numberpicker.NumberPicker;


import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: Catch The Password");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // CHECK IF EMAIL ISN'T ALREADY USE
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {


                                        if (task.getResult().getProviders().size() == 1){
                                            Log.d(TAG, "onComplete: that email already use");
                                            Toast.makeText(getActivity(), "Email Already In Use", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "onComplete: User Email is available");
                                            mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()){
                                                                 Log.d(TAG, "onComplete: User Email is updated  ");
                                                                 Toast.makeText(getActivity(), "Email Updated", Toast.LENGTH_SHORT).show();
                                                                 mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                             }              
                                                        }
                                                    });
                                            }
                                        } catch (NullPointerException e ) {
                                            Log.d(TAG, "onComplete: " +e.getMessage());
                                        }
                                    }
                                }
                            });

                        } // end if
                            else {
                            Log.d(TAG, "onComplete: Failed");
                        }
                    }
                });


    }

    private static final String TAG = "EditProfileFragment";

    //private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    // EDIT PROFILE WIDGET
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText mFullname , mEmail;
    private TextView mWeight, mHeight , mBirthdate , mclikbirthdateedit, mclickweightedit, mclickheightedit,mChangeImageProfile;

    private CircleImageView mProfilePhoto;

    // FIREBASE METHOD
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext;
    private UserSettings mUserSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        // EDIT PROFILE WIDGET
        mContext = getActivity();
        mChangeImageProfile = view.findViewById(R.id.ChangeImageProfile);
        mProfilePhoto = view.findViewById(R.id.ProfileImage);
        mEmail = view.findViewById(R.id.input_email);
        mFullname = view.findViewById(R.id.input_name);
        mBirthdate = view.findViewById(R.id.birthdateedit);
        mHeight = view.findViewById(R.id.height);
        mWeight = view.findViewById(R.id.weight);
        mclikbirthdateedit = view.findViewById(R.id.clickbirthdateedit);
        mclickheightedit = view.findViewById(R.id.clickheightedit);
        mclickweightedit = view.findViewById(R.id.clickweightedit);
        mFirebaseMethods = new FirebaseMethods(getActivity());

        calendar();
        height();
        weight();
        setupFirebaseAuth();
       //  setProfileImage();
       //Object Declaration OnCreate
        ImageView backArrow =  view.findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                getActivity().finish();
            }
        });

        ImageView confirmChanage = view.findViewById(R.id.SaveChange);
        confirmChanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });

        mChangeImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }




    /**
     * ------------------------------------- WIDGET ------------------------------------------------
     */
    private void calendar(){
        mclikbirthdateedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;

                String date = dayOfMonth + "/" + month  + "/" + year;
                mBirthdate.setText(date);
            }
        };
    }

    private void height(){
        mclickheightedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.oke, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                View mView = getLayoutInflater().inflate(R.layout.layout_height, null,false);
                final NumberPicker numberPicker = mView.findViewById(R.id.number_picker);
                // Set divider color
                numberPicker.setDividerColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                numberPicker.setDividerColorResource(R.color.colorPrimary);

                // Set formatter
                numberPicker.setFormatter(getString(R.string.number_picker_formatter));
                numberPicker.setFormatter(R.string.number_picker_formatter);

                // Set selected text color
                numberPicker.setSelectedTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                numberPicker.setSelectedTextColorResource(R.color.colorPrimary);

                // Set selected text size
                numberPicker.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
                numberPicker.setSelectedTextSize(R.dimen.selected_text_size);

                // Set text color
                numberPicker.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_grey));
                numberPicker.setTextColorResource(R.color.dark_grey);

                // Set text size
                numberPicker.setTextSize(getResources().getDimension(R.dimen.text_size));
                numberPicker.setTextSize(R.dimen.text_size);

                // Set typeface
                numberPicker.setTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));
                numberPicker.setTypeface(getString(R.string.roboto_light), Typeface.NORMAL);
                numberPicker.setTypeface(getString(R.string.roboto_light));
                numberPicker.setTypeface(R.string.roboto_light, Typeface.NORMAL);
                numberPicker.setTypeface(R.string.roboto_light);

                // Set value
                numberPicker.setMaxValue(220);
                numberPicker.setMinValue(120);
                //numberPicker.setValue(165);

                // Set fading edge enabled
                numberPicker.setFadingEdgeEnabled(true);

                // Set scroller enabled
                numberPicker.setScrollerEnabled(true);

                // Set wrap selector wheel
                numberPicker.setWrapSelectorWheel(true);
                numberPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Click on current value");

                    }
                });
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        Log.d(TAG, "onValueChange: " +oldVal +newVal);
                        mHeight.setText(""+newVal);
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void weight(){
        mclickweightedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity())
                        .setPositiveButton(R.string.oke, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                View mView = getLayoutInflater().inflate(R.layout.layout_weight, null);
                final NumberPicker numberPicker1 = mView.findViewById(R.id.number_picker1);

                // Set divider color
                numberPicker1.setDividerColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                numberPicker1.setDividerColorResource(R.color.colorPrimary);

                // Set formatter
                numberPicker1.setFormatter(getString(R.string.number_picker_formatter));
                numberPicker1.setFormatter(R.string.number_picker_formatter);

                // Set selected text color
                numberPicker1.setSelectedTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                numberPicker1.setSelectedTextColorResource(R.color.colorPrimary);

                // Set selected text size
                numberPicker1.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
                numberPicker1.setSelectedTextSize(R.dimen.selected_text_size);

                // Set text color
                numberPicker1.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_grey));
                numberPicker1.setTextColorResource(R.color.dark_grey);

                // Set text size
                numberPicker1.setTextSize(getResources().getDimension(R.dimen.text_size));
                numberPicker1.setTextSize(R.dimen.text_size);

                // Set typeface
                numberPicker1.setTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));
                numberPicker1.setTypeface(getString(R.string.roboto_light), Typeface.NORMAL);
                numberPicker1.setTypeface(getString(R.string.roboto_light));
                numberPicker1.setTypeface(R.string.roboto_light, Typeface.NORMAL);
                numberPicker1.setTypeface(R.string.roboto_light);

                // Set fading edge enabled
                numberPicker1.setFadingEdgeEnabled(true);

                // Set scroller enabled
                numberPicker1.setScrollerEnabled(true);

                // Set wrap selector wheel
                numberPicker1.setWrapSelectorWheel(true);
                numberPicker1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Click on current value");

                    }
                });

                numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        Log.d(TAG, "onValueChange: " +oldVal +newVal);
                        mWeight.setText(""+newVal);
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();


            }
        });

    }

    /**
     * ---------------------------------------- FIREBASE METHOD ------------------------------------
     */

    private void saveProfileSettings() {
        final String fullname = mFullname.getText().toString();
        final String email = mEmail.getText().toString();
        final String birthdate = mBirthdate.getText().toString();
        final long height = Long.parseLong(mHeight.getText().toString());
        final long weight = Long.parseLong(mWeight.getText().toString());

        if (!mUserSettings.getUser().getEmail().equals(email)) {
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this,1);
        }

        if (!mUserSettings.getSettings().getFullname().equals(fullname)){
            Log.d(TAG, "saveProfileSettings: UPDATED FULLNAME");
            mFirebaseMethods.updateUserAccountSettings(fullname,null,0,0);
        }
        if (!mUserSettings.getSettings().getBirthdate().equals(birthdate)){
            Log.d(TAG, "saveProfileSettings: UPDATED BIRTHDATE");
            mFirebaseMethods.updateUserAccountSettings(null,birthdate,0,0);
        }
        if ( mUserSettings.getSettings().getHeight() != height){
            Log.d(TAG, "saveProfileSettings: UPDATED HEIGHT");
            mFirebaseMethods.updateUserAccountSettings(null,null,height,0);
        }
        if ( mUserSettings.getSettings().getHeight() != weight){
            Log.d(TAG, "saveProfileSettings: UPDATED WEIGHT");
            mFirebaseMethods.updateUserAccountSettings(null,null,0,weight);
        }
    }


    private void setProfileWidget(UserSettings userSettings){
        mUserSettings = userSettings;

        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mFullname.setText(settings.getFullname());
        //mGender.setText(settings.getGender());
        mBirthdate.setText(settings.getBirthdate());
        mHeight.setText(String.valueOf(settings.getHeight()));
        mWeight.setText(String.valueOf(settings.getWeight()));
        mEmail.setText(userSettings.getUser().getEmail());


    }
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
