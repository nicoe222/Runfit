package com.example.nicosetiawan.runfit.History;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = HistoryActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Log.d(TAG, "onCreate: Started.");

        SetupBottomNavigationView();
    }

    private void SetupBottomNavigationView(){
        Log.d(TAG, "SetupBottomNavigationView: Setting Up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.SetupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
