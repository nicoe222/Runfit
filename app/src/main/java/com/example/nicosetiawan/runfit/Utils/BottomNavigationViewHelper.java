package com.example.nicosetiawan.runfit.Utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.nicosetiawan.runfit.History.HistoryActivity;
import com.example.nicosetiawan.runfit.Activities.HomeActivity;
import com.example.nicosetiawan.runfit.News.NewsActivity;
import com.example.nicosetiawan.runfit.Profiler.ProfilerActivity;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Weather.WeatherActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void SetupBottomNavigationView(BottomNavigationViewEx BottomNavigationViewEx){
        Log.d(TAG, "SetupBottomNavigationView: Setting Up Bottom Navigation Bar");
        BottomNavigationViewEx.enableAnimation(false);
        BottomNavigationViewEx.enableItemShiftingMode(false);
        BottomNavigationViewEx.enableShiftingMode(false);
        BottomNavigationViewEx.setTextVisibility(true);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_news:
                        Intent intent1 = new Intent(context, NewsActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent1);
                        ((Activity) context).finish();
                        break;
                    case R.id.ic_history:
                        Intent intent2 = new Intent(context, HistoryActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent2);
                        //((Activity) context).finish();
                        break;
                    case R.id.ic_home:
                        Intent intent3 = new Intent(context, HomeActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent3);
                        //((Activity) context).finish();
                        break;
                    case R.id.ic_weather:
                        Intent intent4 = new Intent(context, WeatherActivity.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent4);
                        //((Activity) context).finish();
                        break;
                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfilerActivity.class);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent5);
                        //((Activity) context).finish();
                        break;
                }
                return false;
            }
        });
    }

}
