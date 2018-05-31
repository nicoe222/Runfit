package com.example.nicosetiawan.runfit.Profiler;

import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.Permissions;

public class PhotoFragment extends Fragment {

    private static final String TAG = "PhotoFragment";

    private static  final int PHOTO_FRAGMENT_NUM = 1;
    private static final int CAMERA_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);


        Button btnLaunchCamera = view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((PhotoActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if (((PhotoActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])){
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(getActivity(), PhotoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;
        bitmap = (Bitmap) data.getExtras().get("data");

        if (requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking photo");

            try {
                Intent intent = new Intent(getActivity(), Settings.class);
                intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_account));
                startActivity(intent);
                getActivity().finish();
            } catch (NullPointerException e ){
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }
}
