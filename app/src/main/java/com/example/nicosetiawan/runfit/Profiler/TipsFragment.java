package com.example.nicosetiawan.runfit.Profiler;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.nicosetiawan.runfit.R;

public class TipsFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TipsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);


        // ArrowBack
        ImageView backArrow = view.findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                getActivity().finish();
            }
        });

        return view;
    }
}
