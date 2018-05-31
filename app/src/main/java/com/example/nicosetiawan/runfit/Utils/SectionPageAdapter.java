package com.example.nicosetiawan.runfit.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = "SectionPageAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public SectionPageAdapter(FragmentManager fm) { super(fm);}

    @Override
    public Fragment getItem(int position) {return mFragmentList.get(position);}

    @Override
    public int getCount(){ return mFragmentList.size();}

    public void addfragment(Fragment fragment){mFragmentList.add(fragment);}

}
