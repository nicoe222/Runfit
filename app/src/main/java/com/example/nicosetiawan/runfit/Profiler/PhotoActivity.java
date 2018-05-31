package com.example.nicosetiawan.runfit.Profiler;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.Permissions;
import com.example.nicosetiawan.runfit.Utils.SectionPageAdapter;


public class PhotoActivity extends AppCompatActivity {

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        if (checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }


    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();


    }

    private void setupViewPager(){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addfragment(new GalleryFragment());
        adapter.addfragment(new PhotoFragment());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }


    public void verifyPermissions(String[] permissions){
        ActivityCompat.requestPermissions(
                PhotoActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    public boolean checkPermissionsArray(String[] permissions){

        for (int i = 0; i< permissions.length; i++){
            String check = permissions[i];

            if (!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permissions){

        int permissionsRequest = ActivityCompat.checkSelfPermission(PhotoActivity.this, permissions);

        if (permissionsRequest != PackageManager.PERMISSION_GRANTED){
            return false;
        } else {
            return true;
        }

    }
}
