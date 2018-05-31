package com.example.nicosetiawan.runfit.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicosetiawan.runfit.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.support.constraint.Constraints.TAG;

public class ConfirmPasswordDialog extends DialogFragment {


    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);

    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;

    TextView mPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_confirm_password, container , false);
        mPassword = view.findViewById(R.id.confirm_password);

        TextView cancelDialog = view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        TextView confirmDialog = view.findViewById(R.id.dialogConfirml);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Get Password For Confirm");
                String password = mPassword.getText().toString();

                if (!password.equals("")){
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter a password", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



        try {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException" +e.getMessage());
        }
    }
}
