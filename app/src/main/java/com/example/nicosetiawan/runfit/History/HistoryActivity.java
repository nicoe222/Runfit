package com.example.nicosetiawan.runfit.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nicosetiawan.runfit.Adapter.HistoryAdapter;
import com.example.nicosetiawan.runfit.Database.Runit;
import com.example.nicosetiawan.runfit.Login.FrontActivity;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.example.nicosetiawan.runfit.Utils.DividerItemDecoration;
import com.example.nicosetiawan.runfit.Utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryAdapterListener {
    private static final String TAG = "HistoryActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = HistoryActivity.this;
    private HistoryAdapter mAdapter;
    private RecyclerView recyclerview;
    private ArrayList<Runit> runitArrayList = new ArrayList<Runit>();
    DatabaseReference databaseRunhistory;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String userId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Log.d(TAG, "onCreate: Started. test");

        //databaseRunhistory = FirebaseDatabase.getInstance().getReference("runhistory");
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(HistoryActivity.this);
        userId = sessionManager.getUserId();

        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new HistoryAdapter(HistoryActivity.this,runitArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( mContext );
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.addItemDecoration(new DividerItemDecoration( mContext, LinearLayoutManager.VERTICAL));
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(mAdapter);
        //setupFirebaseAuth();
        getData();

        SetupBottomNavigationView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        //checkCurrentUser(mAuth.getCurrentUser());


    }
    /**
     * -------------------------------------- FIREBASE ------------------------------------------------------------
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: Check Current User" + user.getUid());
        if (user == null) {
            Intent intent = new Intent(mContext, FrontActivity.class);
            startActivity(intent);
        }
        userId = user.getUid();
    }
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Starting Firebase");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                checkCurrentUser(user);
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: Sign In");
                } else {
                    Log.d(TAG, "onAuthStateChanged: Sign Out");
                }
            }
        };
    }
    public void getData(){
        Log.d(TAG, " User id " + userId);
        runitArrayList.clear();
        db.collection("runfit")
                .whereEqualTo("userId", userId)

                .addSnapshotListener(HistoryActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                            //doc.getDocument().toObject(Event.class);

                            Log.d("Res getData", " > " + doc.getDocument());
                            //do something...
                            String runId        = doc.getDocument().getId();
                            String runTitle     = doc.getDocument().get("runTitle").toString();
                            String POsisi       = doc.getDocument().get("posisi").toString();
                            String runDate      = doc.getDocument().get("runDate").toString();
                            String runStartTime = doc.getDocument().get("runStartTime").toString();
                            String runStopTime  = doc.getDocument().get("runStopTime").toString();
                            String runDistance  = doc.getDocument().get("runDistance").toString();
                            String runDuration  = doc.getDocument().get("runDuration").toString();


                            Runit runit = new Runit( runId, runTitle, runDate, runStartTime, runStopTime, runDistance, runDuration, POsisi);


                            runitArrayList.add(runit);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
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

    @Override
    public void onRowClicked(int position) {
        Runit runit = runitArrayList.get(position);
        Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
        intent.putExtra("runfit", runit);

        startActivity(intent);

    }

    @Override
    public void onRowLongClicked(int position) {

    }
}
