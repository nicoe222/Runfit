package com.example.nicosetiawan.runfit.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.example.nicosetiawan.runfit.Models.User;
import com.example.nicosetiawan.runfit.Models.UserAccountSettings;
import com.example.nicosetiawan.runfit.Models.UserSettings;
import com.example.nicosetiawan.runfit.Profiler.Settings;
import com.example.nicosetiawan.runfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //FIREBASE DECLARATION
    private FirebaseAuth mAuth;
   // private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext;
    private StorageReference mStorageReference;

    private double mPhotoUploadProgress = 0;

    //CONSTRUCTOR
    public FirebaseMethods (Context context){

        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;
        if (mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public void uploadNewPhoto(String photoType, final String caption,final int count, final String imgUrl,
                               Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        ((Settings)mContext).setViewPager(
                ((Settings)mContext).pagerAdapter
                        .getFragmentNumber(mContext.getString(R.string.edit_account))
        );

        FilePaths filePaths = new FilePaths();

        if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    public void updateUserAccountSettings(String fullname, String birthdate, long height , long weight){

        if (fullname != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_fullname))
                    .setValue(fullname);
        }

        if (birthdate != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_birthdate))
                    .setValue(birthdate);
        }

        if (height != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_height))
                    .setValue(height);
        }

        if (weight != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_weight))
                    .setValue(weight);
        }
    }




    public void updateEmail (String email){
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }


    public void registerNewEmail(final String email, String password, String fullname){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            // Send Verification Email
                            try {
                                sendVerificationEmail();
                                Log.d(TAG, "createUserWithEmail:success");
                                userID = mAuth.getCurrentUser().getUid();
                                //Toast.makeText(mContext, "Check Your Email To Confirmation", Toast.LENGTH_SHORT).show();
                            } catch (NullPointerException e){
                                Log.d(TAG, "onComplete: " +e.getMessage());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           //
                        }
                    }
                });
    }


    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: ");
                            } else {
                                Toast.makeText(mContext, "Couldn't Send Email Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add Information To User and User Account Settings

     */
    public void addNewUser(String email, String fullname, String birthdate, String gender, long height, String profile_photo, long weight){
        User user = new User(email,fullname,userID);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                birthdate,fullname,gender,height,"",weight
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * RETRIEVE DATA FROM DATABASE
     * DATABASE : user_account_settings
     */

    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "settings: retrieve data from database");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            // user_account_settings data
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings ))){
                Log.d(TAG, "settings: datasnapshot" + ds);
                try {
                    settings.setFullname(
                            ds.child(userID)
                            .getValue(UserAccountSettings.class)
                            .getFullname()
                    );
                    settings.setBirthdate(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getBirthdate()
                    );
                    settings.setGender(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getGender()
                    );
                    settings.setHeight(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getHeight()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setWeight(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWeight()
                    );
                    Log.d(TAG, "settings: " + settings.toString());
                } catch (NullPointerException e){
                    Log.d(TAG, "settings: NullPointerException" +e.getMessage());
                }
            }// end if for user account settings

            // user_account_settings data
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "settings: datasnapshot" + ds);

                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setFullname(
                        ds.child(userID)
                                .getValue(User.class)
                                .getFullname()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "settings: " + user.toString());
            } //end if for user
        } // end for

    return new UserSettings(user,settings);
    }

}
