package com.example.nicosetiawan.runfit.History;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.nicosetiawan.runfit.Activities.HomeActivity;
import com.example.nicosetiawan.runfit.Database.RunPosition;
import com.example.nicosetiawan.runfit.Database.Runit;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "HistoryActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = HistoryDetailActivity.this;
    private ArrayList<RunPosition> runPositions = new ArrayList<RunPosition>();
    private String runId;
    FirebaseFirestore db;

    private TextView textTimer;
    private TextView textDistance;
    private TextView lblDistance;
    private TextView txtKec, lblKec;
    private double lblJarak, lblKecepatan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.homeToolBar);
        setSupportActionBar(toolbar);

        textTimer = findViewById(R.id.duration);
        textDistance = findViewById(R.id.distance);
        lblDistance  = findViewById(R.id.labDistance);
        txtKec       = findViewById(R.id.pace);
        lblKec       = findViewById(R.id.lblKec);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HistoryDetailActivity.this);


        ImageView backArrow = findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_his_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(item.getItemId() == android.R.id.home) {

            return true;
        }else if (id == R.id.actDelete) {
            delete();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final int polylineWidth = 10;

        Runit runit = (Runit) getIntent().getSerializableExtra("runfit");
        double jarak = Double.valueOf( runit.getRunDistance() );
        runId = runit.getRunId();
        textTimer.setText( runit.getRunDuration() );
        String jam = textTimer.getText().toString();
        String[] times = jam.split(":");
        double hour = 0 , minute= 0, sec= 0;
        if (!times[0].equals("00")){
            hour = Double.valueOf(times[0]) * 3600;
        }

        if (!times[1].equals("00")){
            minute = Double.valueOf(times[1]) * 60;
        }
        if (!times[2].equals("00")){
            sec = Double.valueOf(times[2]);
        }
        double t = hour + minute + sec;;
        Log.d("Re Waktu", "" + t);
        if ( jarak > 999 ){
            lblJarak = jarak / 1000;
            lblDistance.setHint("Distance (KM)");
        }else{
            //t = t / 60;
            lblJarak = jarak;
            lblDistance.setHint("Distance (M)");
        }

        if ( hour >  3600 ){
            t = t / 3600;
            lblKec.setText("Avg. Pace (Km/H)");
            if (jarak > 999){
                jarak = jarak / 1000;
            }
        }else{
            lblKec.setText("Avg. Pace (m/s)");
        }

        Log.d("Re Waktu", jarak + " / " + t);

        DecimalFormat df = new DecimalFormat("#.##");


        double kec = Double.valueOf(jarak) / t;

        textDistance.setText( df.format(lblJarak) );
        txtKec.setText( df.format(kec));

        Log.d("Res Jarak", "" + kec);
        try {


            JSONArray myjson = new JSONArray(runit.getRunPos());
            // Toast.makeText(HistoryActivity.this, "New Posistion " + myjson.length(), Toast.LENGTH_SHORT).show();
            //myjson.getJSONArray()
            if ( myjson.length() > 0 ) {
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.parseColor("#017bff")).
                        width(polylineWidth);

                String fLat = "", fLng = "";
                String eLat = "", eLng = "";

                for (int x = 0; x < myjson.length(); x++) {
                    JSONObject obj = myjson.getJSONObject(x);

                    String lat = obj.getString("lat");
                    String lng = obj.getString("lng");

                    LatLng from = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                    //LatLng to = new LatLng( Double.valueOf( toPos.getRunLat() ), Double.valueOf(toPos.getRunLong()));
                    polylineOptions.add(from);
                    if (x == 0) {
                        fLat = lat;
                        fLng = lng;
                    }

                    if (x == myjson.length() - 1) {
                        eLat = lat;
                        eLng = lng;
                    }
                }
                Polyline polyline1 = googleMap.addPolyline(polylineOptions);

                LatLng latLng = new LatLng(Double.valueOf(eLat), Double.valueOf(eLng));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
            }
        } catch (JSONException e1) {
            Toast.makeText(HistoryDetailActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
        }

    }

    public void delete(){
        new MaterialDialog.Builder(HistoryDetailActivity.this)
                .title(R.string.delete_history)
                .content(R.string.confirm_history)
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // TODO
                        db.collection("runfit").document(runId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                })
                .show();


    }
}
