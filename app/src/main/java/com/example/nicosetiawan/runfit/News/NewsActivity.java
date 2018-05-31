package com.example.nicosetiawan.runfit.News;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.nicosetiawan.runfit.Profiler.ProfileFragment;
import com.example.nicosetiawan.runfit.Profiler.ProfilerActivity;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NewsActivity extends AppCompatActivity {
    private static final String TAG = "NewsActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = NewsActivity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        init();

    }


    private void init(){
        NewsFragment fragment = new NewsFragment();
        FragmentTransaction transaction = NewsActivity.this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.news_fragment));
        transaction.commit();
    }

}
