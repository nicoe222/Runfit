package com.example.nicosetiawan.runfit.Profiler;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.example.nicosetiawan.runfit.R;

public class ProfilerActivity extends AppCompatActivity {
    private static final String TAG = "ProfilerActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext = ProfilerActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: Started.");
        init();

    }

    private void init(){
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfilerActivity.this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


}
