package com.example.nicosetiawan.runfit.Profiler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.nicosetiawan.runfit.R;

import com.example.nicosetiawan.runfit.Utils.FirebaseMethods;
import com.example.nicosetiawan.runfit.Utils.SectionStatePageAdapter;


import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";
    private Context mContext;
    public SectionStatePageAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = Settings.this;
        Log.d(TAG, "onCreate: Started");
        mViewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.settings);

        setupSettingList();
        setupFragments();
        getIncomingIntent();
        // ArrowBack
        ImageView backArrow = findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                finish();
            }
        });

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))) {

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_account))) {

                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(Settings.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra(getString(R.string.selected_image)), null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(Settings.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }

            }

        }

        if (intent.hasExtra(getString(R.string.calling_activity))) {

            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_account)));
        }
    }


    private void setupFragments(){
        pagerAdapter = new SectionStatePageAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_account)); //Fragment 0
        pagerAdapter.addFragment(new TipsFragment(), getString(R.string.tips_page)); //Fragment 1
        pagerAdapter.addFragment(new AboutUsFragment(), getString(R.string.abaout_us)); //Fragment 2
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.logout_account)); //Fragment 2
    }

    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: Navigating To Fragment");
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingList(){
        Log.d(TAG, "setupSettingList: Setting Up");
        ListView listView = findViewById(R.id.LvPersonalSetting);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_account)); //Fragment 0
        options.add(getString(R.string.tips_page)); //fragment 1
        options.add(getString(R.string.abaout_us)); //Fragment 2
        options.add(getString(R.string.logout_account)); //Fragment 3

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setDivider(null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: Navigation To Fragment");
                setViewPager(position);
            }
        });
    }




}
