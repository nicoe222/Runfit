package com.example.nicosetiawan.runfit.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.gpsstatus.GpsStatusProxy;
import com.example.nicosetiawan.runfit.Login.FrontActivity;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Service.LocationService;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.example.nicosetiawan.runfit.Utils.UniversalImageLoader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivityOk extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext = HomeActivityOk.this;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private GpsStatusProxy proxy;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    // private PlaceInfo mPlace;
    private Marker mMarker;

    // new
    public LocationService locationService;
    private MapView mapView;

    private Marker userPositionMarker;
    private Circle locationAccuracyCircle;
    private BitmapDescriptor userPositionMarkerBitmapDescriptor;
    private Polyline runningPathPolyline;
    private PolylineOptions polylineOptions;
    private int polylineWidth = 30;

    //boolean isZooming;
    //boolean isBlockingAutoZoom;

    boolean zoomable = true;

    Timer zoomBlockingTimer;
    boolean didInitialZoom;
    private Handler handlerOnUIThread;


    private BroadcastReceiver locationUpdateReceiver;
    private BroadcastReceiver predictedLocationReceiver;

    private Button startButton;
    private Button stopButton;

    /* Filater */
    private Circle predictionRange;
    BitmapDescriptor oldLocationMarkerBitmapDescriptor;
    BitmapDescriptor noAccuracyLocationMarkerBitmapDescriptor;
    BitmapDescriptor inaccurateLocationMarkerBitmapDescriptor;
    BitmapDescriptor kalmanNGLocationMarkerBitmapDescriptor;
    ArrayList<Marker> malMarkers = new ArrayList<>();
    final Handler handler = new Handler();

    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();

    private TextView textTimer;
    private TextView textDistance;
    private TextView lblDistance;
    public ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
    public Integer IS_STAR = 0;
    // end new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Starting");
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        textTimer = findViewById(R.id.duration);
        textDistance = findViewById(R.id.distance);
        lblDistance  = findViewById(R.id.labDistance);

        mapView = (MapView) this.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        SetupBottomNavigationView();
        initImageLoader();
        // FIREBASE
        setupFirebaseAuth();
        getLocationPermission();
        proxy = GpsStatusProxy.getInstance(getApplicationContext());
        proxy.register();



        // add new

        final Intent locationService = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(locationService);
        this.getApplication().bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);





        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location newLocation = intent.getParcelableExtra("location");
                Log.d("new newLocation", newLocation.toString());

                drawLocationAccuracyCircle(newLocation);
                drawUserPositionMarker(newLocation);

                if (HomeActivityOk.this.locationService.isLogging) {
                    addPolyline();
                }
                zoomMapTo(newLocation);

                /* Filter Visualization */
                drawMalLocations();

            }
        };

        predictedLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location predictedLocation = intent.getParcelableExtra("location");

                drawPredictionRange(predictedLocation);

            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationUpdateReceiver,
                new IntentFilter("LocationUpdated"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                predictedLocationReceiver,
                new IntentFilter("PredictLocation"));

        startButton = findViewById(R.id.startactivity);
        stopButton = findViewById(R.id.stopactivity);
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IS_STAR = 1;
                startButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                start();
                clearPolyline();
                clearMalMarkers();
                clearInfo();
                HomeActivityOk.this.locationService.startLogging();

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                HomeActivityOk.this.locationService.stopLogging();
                stop();
                IS_STAR = 0;
            }
        });
        // end new
    }

    @Override
    protected void onDestroy() {
        if (this.mapView != null) {
            this.mapView.onDestroy();
        }

        try {
            if (locationUpdateReceiver != null) {
                unregisterReceiver(locationUpdateReceiver);
            }

            if (predictedLocationReceiver != null) {
                unregisterReceiver(predictedLocationReceiver);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        super.onDestroy();
        //locationManager.removeUpdates(locationListener);
        proxy.unRegister();
    }


    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            //getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }*/

    private void runMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //getDeviceLocation();
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(false);
                if (ActivityCompat.checkSelfPermission(HomeActivityOk.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivityOk.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(false);
                    //return;
                }

                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                            Log.d(TAG, "onCameraMoveStarted after user's zoom action");

                            zoomable = false;
                            if (zoomBlockingTimer != null) {
                                zoomBlockingTimer.cancel();
                            }

                            handlerOnUIThread = new Handler();

                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    handlerOnUIThread.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            zoomBlockingTimer = null;
                                            zoomable = true;

                                        }
                                    });
                                }
                            };
                            zoomBlockingTimer = new Timer();
                            zoomBlockingTimer.schedule(task, 10 * 1000);
                            Log.d(TAG, "start blocking auto zoom for 10 seconds");
                        }
                    }
                });
            }
        });
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initMap();
                runMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    //initMap();
                    proxy.register();
                }
            }
        }
    }

    /*private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }*/


    private void getDeviceLocation(){
        Log.d("dev Location", "getDeviceLocation: getting the devices current location");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                proxy.notifyLocation(location);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("dev Location","Lat " + latitude );
                final LatLng latLng = new LatLng(latitude, longitude);
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivityOk.this);
                try{
                    if(mLocationPermissionsGranted){
                        final Task location1 = mFusedLocationProviderClient.getLastLocation();
                        location1.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: found location!");
                                    Location currentLocation = (Location) task.getResult();
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            DEFAULT_ZOOM,
                                            "My Location");
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                    Toast.makeText(HomeActivityOk.this, "New Posistion " + latLng, Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.d(TAG, "onComplete: current location is null");
                                    Toast.makeText(HomeActivityOk.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }catch (SecurityException e){
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(HomeActivityOk.this, "New Posistion " , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    private void SetupBottomNavigationView(){
        Log.d(TAG, "SetupBottomNavigationView: Setting Up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.SetupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }



    /**
     * -------------------------------------- FIREBASE ------------------------------------------------------------
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: Check Current User");
        if (user == null) {
            Intent intent = new Intent(mContext, FrontActivity.class);
            startActivity(intent);
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        //checkCurrentUser(mAuth.getCurrentUser());
        if (this.mapView != null) {
            this.mapView.onStart();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (this.mapView != null) {
            this.mapView.onStop();
        }
    }

    // add new
    @Override
    public void onPause() {
        super.onPause();

        if (this.mapView != null) {
            this.mapView.onPause();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (this.mapView != null) {
            this.mapView.onResume();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (this.mapView != null) {
            this.mapView.onLowMemory();
        }
    }

    private void drawLocationAccuracyCircle(Location location){
        if(location.getAccuracy() < 0){
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("Lat Long", "" + latLng.toString());
        //if ( IS_STAR == 1 ){
            latLngArrayList.add(latLng);
            LatLng sLng = latLngArrayList.get(0);


            Location slocation = new Location("");
            slocation.setLatitude(sLng.latitude);
            slocation.setLongitude(sLng.longitude);

            Location curlocation = new Location("");
            curlocation.setLatitude(latLng.latitude);
            curlocation.setLongitude(latLng.longitude);

            float distance = slocation.distanceTo(curlocation) ;


            if ( distance > 1000 ){
                distance = slocation.distanceTo(curlocation) / 1000;
                lblDistance.setHint(R.string.distance_km);
            }else{
                lblDistance.setHint(R.string.distance_m);
            }
            double jarak = (double) (Math.round(distance * 100)) / 100;
            textDistance.setText( Double.toString(jarak)  );
        //}

        if (this.locationAccuracyCircle == null) {
            this.locationAccuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .fillColor(Color.argb(64, 0, 0, 0))
                    .strokeColor(Color.argb(64, 0, 0, 0))
                    .strokeWidth(0.0f)
                    .radius(location.getAccuracy())); //set readius to horizonal accuracy in meter.
        } else {
            this.locationAccuracyCircle.setCenter(latLng);
        }
        //Toast.makeText(HomeActivity.this, " "+ latLngArrayList.toString() , Toast.LENGTH_SHORT).show();
    }

    private void drawMalLocations(){
        drawMalMarkers(locationService.oldLocationList, oldLocationMarkerBitmapDescriptor);
        drawMalMarkers(locationService.noAccuracyLocationList, noAccuracyLocationMarkerBitmapDescriptor);
        drawMalMarkers(locationService.inaccurateLocationList, inaccurateLocationMarkerBitmapDescriptor);
        drawMalMarkers(locationService.kalmanNGLocationList, kalmanNGLocationMarkerBitmapDescriptor);
    }
    private void drawMalMarkers(ArrayList<Location> locationList, BitmapDescriptor descriptor){
        for(Location location : locationList){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(descriptor));

            malMarkers.add(marker);
        }
    }
    private void drawUserPositionMarker(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(this.userPositionMarkerBitmapDescriptor == null){
            userPositionMarkerBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.user_position_point);
        }

        if (userPositionMarker == null) {
            userPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(this.userPositionMarkerBitmapDescriptor));
        } else {
            userPositionMarker.setPosition(latLng);
        }
    }

    private void addPolyline() {
        ArrayList<Location> locationList = locationService.locationList;

        if (runningPathPolyline == null) {
            if (locationList.size() > 1){
                Location fromLocation = locationList.get(locationList.size() - 2);
                Location toLocation = locationList.get(locationList.size() - 1);

                LatLng from = new LatLng(((fromLocation.getLatitude())),
                        ((fromLocation.getLongitude())));

                LatLng to = new LatLng(((toLocation.getLatitude())),
                        ((toLocation.getLongitude())));

                this.runningPathPolyline = mMap.addPolyline(new PolylineOptions()
                        .add(from, to)
                        .width(polylineWidth).color(Color.parseColor("#801B60FE")).geodesic(true));
            }
        } else {
            Location toLocation = locationList.get(locationList.size() - 1);
            LatLng to = new LatLng(((toLocation.getLatitude())),
                    ((toLocation.getLongitude())));

            List<LatLng> points = runningPathPolyline.getPoints();
            points.add(to);

            runningPathPolyline.setPoints(points);
        }
    }

    private void zoomMapTo(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.didInitialZoom == false) {
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
                this.didInitialZoom = true;
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Toast.makeText(this.getActivity(), "Inital zoom in process", Toast.LENGTH_LONG).show();
        }

        if (zoomable) {
            try {
                zoomable = false;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                zoomable = true;
                            }

                            @Override
                            public void onCancel() {
                                zoomable = true;
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            String name = className.getClassName();

            if (name.endsWith("LocationService")) {
                locationService = ((LocationService.LocationServiceBinder) service).getService();

                locationService.startUpdatingLocation();


            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            if (className.getClassName().equals("LocationService")) {
                locationService.stopUpdatingLocation();
                locationService = null;
            }
        }
    };

    private void drawPredictionRange(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.predictionRange == null) {
            this.predictionRange = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .fillColor(Color.argb(50, 30, 207, 0))
                    .strokeColor(Color.argb(128, 30, 207, 0))
                    .strokeWidth(1.0f)
                    .radius(30)); //30 meters of the prediction range
        } else {
            this.predictionRange.setCenter(latLng);
        }

        this.predictionRange.setVisible(true);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivityOk.this.predictionRange.setVisible(false);
            }
        }, 2000);
    }

    private void clearPolyline() {
        if (runningPathPolyline != null) {
            runningPathPolyline.remove();
            runningPathPolyline = null;
        }
    }

    public void clearMalMarkers(){
        for (Marker marker : malMarkers){
            marker.remove();
        }
    }

    // timer
    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stop() {
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            textTimer.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };

    public void clearInfo(){
        latLngArrayList.clear();
        textDistance.setText("0,00");

    }
    // end new

//END
}
