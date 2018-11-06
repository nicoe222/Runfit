package com.example.nicosetiawan.runfit.Login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicosetiawan.runfit.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.nicosetiawan.runfit.R;
import com.irozon.sneaker.Sneaker;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private Context mContext;
    String email,fullname,password,birthdate,gender;
    long height,weight;

    private EditText mEmail, mFullname, mPassword;
    private TextView wait, mclick , mBirthdate,mHeight,mWeight,mclickweight,mclickheight;
    private Button mButtonRegister;
    private ProgressBar mProgressbar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int finishtime = 3;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    //FIREBASE DECLARATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    /**
     * --------------------------------------------- ON CREATED METHOD --------------------------------------------------------
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegistrationActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);

        initWidget();
        height();
        weight();
        calendar();
        setupFirebaseAuth();
        register();

        ImageView backArrow = findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                finish();
            }
        });
    }

    private void closeafter(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },finishtime * 1000);
    }

    private void weight(){
        mclickweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this)
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
                numberPicker1.setDividerColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                numberPicker1.setDividerColorResource(R.color.colorPrimary);

                // Set formatter
                numberPicker1.setFormatter(getString(R.string.number_picker_formatter));
                numberPicker1.setFormatter(R.string.number_picker_formatter);

                // Set selected text color
                numberPicker1.setSelectedTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                numberPicker1.setSelectedTextColorResource(R.color.colorPrimary);

                // Set selected text size
                numberPicker1.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
                numberPicker1.setSelectedTextSize(R.dimen.selected_text_size);

                // Set text color
                numberPicker1.setTextColor(ContextCompat.getColor(mContext, R.color.dark_grey));
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

    private void height(){
        mclickheight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this)
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
                numberPicker.setDividerColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                numberPicker.setDividerColorResource(R.color.colorPrimary);

                // Set formatter
                numberPicker.setFormatter(getString(R.string.number_picker_formatter));
                numberPicker.setFormatter(R.string.number_picker_formatter);

                // Set selected text color
                numberPicker.setSelectedTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                numberPicker.setSelectedTextColorResource(R.color.colorPrimary);

                // Set selected text size
                numberPicker.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
                numberPicker.setSelectedTextSize(R.dimen.selected_text_size);

                // Set text color
                numberPicker.setTextColor(ContextCompat.getColor(mContext, R.color.dark_grey));
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

    private void calendar(){
        mclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistrationActivity.this,
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

    private void register(){

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int SelectID = mRadioGroup.getCheckedRadioButtonId();
                mRadioButton = findViewById(SelectID);
                email = mEmail.getText().toString();
                fullname = mFullname.getText().toString();
                password = mPassword.getText().toString();
                birthdate = mBirthdate.getText().toString();
                gender = mRadioButton.getText().toString();
                height = Long.valueOf(mHeight.getText().toString());
                weight = Long.valueOf(mWeight.getText().toString());
                if (checkInputs(email,fullname,password,birthdate)){
                    mProgressbar.setVisibility(View.VISIBLE);
                    wait.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(email,password,fullname);
                }
            }
        });
    }

    private boolean checkInputs(String email, String fullname, String password, String birthdate){
        if (email.equals("") || fullname.equals("") || password.equals("") || birthdate.equals("")){
            Toast.makeText(mContext, "The Field Must Be Filled Out", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initWidget(){
        mclick = findViewById(R.id.clickbirthdate);
        mclickheight = findViewById(R.id.clickheight);
        mclickweight = findViewById(R.id.clickweight);
        mBirthdate = findViewById(R.id.birthdate);
        mEmail = findViewById(R.id.input_email);
        mFullname =  findViewById(R.id.input_name);
        mPassword = findViewById(R.id.input_password);
        mHeight = findViewById(R.id.height);
        mWeight = findViewById(R.id.weight);
        mProgressbar =  findViewById(R.id.progressbar);
        wait = findViewById(R.id.wait);
        mProgressbar.setVisibility(View.GONE);
        wait.setVisibility(View.GONE);
        mContext = RegistrationActivity.this;
        mButtonRegister = findViewById(R.id.btn_register);
        mRadioGroup = findViewById(R.id.genderradio);
    }

    private boolean isStringNull(String string){
        if (string.equals("")){
            return true;
        } else {
            return false;
        }
    }

    /**
     * -------------------------------------- FIREBASE CONFIGURATION------------------------------------------------------------
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
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            firebaseMethods.addNewUser(email,fullname,birthdate,gender,height,"",weight);
                           // Toast.makeText(mContext, "Check Your Email To Confirmation...", Toast.LENGTH_LONG).show();
                            closeafter();
                            Sneaker.with(RegistrationActivity.this)
                                    .setTitle("Registration Succes")
                                    .setMessage("Check Your Email Inbox Or Spam To Confirmation")
                                    .setDuration(5000) // 5 second
                                    .autoHide(true)
                                    .setOnSneakerClickListener(new Sneaker.OnSneakerClickListener() {
                                        @Override
                                        public void onSneakerClick(View view) {
                                            finish();
                                        }
                                    })
                                    .sneak(R.color.colorPrimaryDark);
                            // user sign out after succes register
                            mAuth.signOut();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // back to prev activity
                    //finish();
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

//END
}