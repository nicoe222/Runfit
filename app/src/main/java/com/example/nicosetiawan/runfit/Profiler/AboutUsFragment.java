package com.example.nicosetiawan.runfit.Profiler;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nicosetiawan.runfit.R;

public class AboutUsFragment extends Fragment {

    private static final String TAG = "AboutUsFragment";
    TextView txt;
    Typeface tp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aboutus, container, false);

        txt = view.findViewById(R.id.runfit1);
        tp = Typeface.createFromAsset(getActivity().getAssets(), "fonts/SarySoft.otf");
        txt.setTypeface(tp,3);


        // ArrowBack
        ImageView backArrow = (ImageView) view.findViewById(R.id.BackMenu);
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
