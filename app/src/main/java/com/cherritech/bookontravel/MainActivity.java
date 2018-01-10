package com.cherritech.bookontravel;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cherritech.bookontravel.Session.SessionManagement;
import com.cherritech.bookontravel.TapTarget.TapTarget;
import com.cherritech.bookontravel.TapTarget.TapTargetView;
import com.cherritech.bookontravel.adapter.CustomDirectionAdapter;
import com.cherritech.bookontravel.app.Config;
import com.cherritech.bookontravel.connectivity.ConnectivityReceiver;
import com.cherritech.bookontravel.connectivity.MyApplication;
import com.cherritech.bookontravel.model.DirectionAdapter;
import com.cherritech.bookontravel.model.NearBy;
import com.cherritech.bookontravel.permission.AfterPermissionGranted;
import com.cherritech.bookontravel.permission.AppSettingsDialog;
import com.cherritech.bookontravel.permission.EasyPermissions;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings({"ConstantConditions", "unchecked", "deprecation"})
public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback,
        ConnectivityReceiver.ConnectivityReceiverListener, EasyPermissions.PermissionCallbacks, GoogleMap.OnMapLongClickListener {
    //,SensorEventListener
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    SessionManagement management;

    ImageView view_map;

    private AdView mAdView;
    ImageButton image_my_location;

    SliderLayout mDemoSlider;

    ProgressDialog progressDialog;
    OkHttpClient client = new OkHttpClient();

    LatLng destinationlatLng;

    LatLng latLng;
    Marker mCurrLocation;

    MyReceiver myReceiver;

    String Latitude = "";
    String Longitude = "";

    private static final int RC_LOCATION_PERM = 124;

    GoogleMap mMap;

    CustomDirectionAdapter adapter;
    List<DirectionAdapter> directionAdapters;

    Map<String,String> map;

    List<List<HashMap<String, String>>> routes;
    List<NearBy> nearBy;
    List<String> photo_reference;

    String distance, duration, end_address, destination_location;

    ArrayList<LatLng> points;

    ListView listDirection;

    FrameLayout layout_frame;
    RelativeLayout layout_main, layout_map, layout_place, layout_map_place, layout_hotel;
    LinearLayout layout_distance_cal, layout_gps;
    CoordinatorLayout layout_distance;

    TextView textView_time, textView_distance, textView_route, hotel_name, hotel_address, hotel_contact;
    RatingBar hotel_star;
    Button button_enable_gps, button_close, button_order;

    MediaPlayer mMediaPlayer;

    FloatingActionButton fab_ways;
    PlaceAutocompleteFragment autocompleteFragment;
    LocationManager manager;
    boolean statusOfGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();

        management = new SessionManagement(this);
        management.createLoginSession();

        photo_reference = new ArrayList<>();
        points = new ArrayList<>();
        directionAdapters = new ArrayList<>();
//        latLngAlarm = new ArrayList<>();
        nearBy = new ArrayList<>();
        map = new HashMap<>();

        mDemoSlider = findViewById(R.id.slider);

        mMediaPlayer = new MediaPlayer();
        myReceiver = new MyReceiver();

        button_close = findViewById(R.id.button_close);
        button_order = findViewById(R.id.button_order);

        //Hotel Review
        layout_hotel = findViewById(R.id.layout_hotel);
        hotel_name = findViewById(R.id.hotel_name);
        hotel_star = findViewById(R.id.hotel_star);
        hotel_address = findViewById(R.id.hotel_address);
        hotel_contact = findViewById(R.id.hotel_contact);

        LayerDrawable stars = (LayerDrawable) hotel_star.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        view_map = findViewById(R.id.view_map);
        layout_distance = findViewById(R.id.layout_distance);
        textView_time = findViewById(R.id.textView_time);
        textView_distance = findViewById(R.id.textView_distance);
        textView_route = findViewById(R.id.textView_route);

        layout_map = findViewById(R.id.layout_map);
        layout_place = findViewById(R.id.layout_place);

        layout_distance_cal = findViewById(R.id.layout_distance_cal);

        layout_main = findViewById(R.id.layout_main);

        layout_frame = findViewById(R.id.layout_frame);

        fab_ways = findViewById(R.id.fab_ways);

        listDirection = findViewById(R.id.listView_ways);

        image_my_location = findViewById(R.id.image_my_location);

        layout_gps = findViewById(R.id.layout_gps);
        layout_map_place = findViewById(R.id.layout_map_place);

        button_enable_gps = findViewById(R.id.button_enable_gps);

        mAdView = findViewById(R.id.adView);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationTask();
        }

        checkGPS();

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layout_hotel.setVisibility(View.GONE);
                mDemoSlider.stopAutoCycle();
            }
        });

        try {

            if (management.isLoggedIn()) {

                management.updateLoginSession();
                // You don't always need a sequence, and for that there's a single time tap target
                final SpannableString spannedDesc = new SpannableString("Mark Your Destination...");
                spannedDesc.setSpan(new UnderlineSpan(), spannedDesc.length(), spannedDesc.length(), 0);
                TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.view_map), "Long Click On Map..", spannedDesc)
                        .cancelable(false)
                        .drawShadow(true)
                        .targetRadius(100)
                        .icon(getResources().getDrawable(R.drawable.finger_long))
                        .tintTarget(false), new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        // .. which evidently starts the sequence we defined earlier
                        view_map.setVisibility(View.GONE);

                        Toast.makeText(view.getContext(), "You Can Try it Your Own!..", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onOuterCircleClick(TapTargetView view) {
                        super.onOuterCircleClick(view);
                        view.dismiss(true);
                        view_map.setVisibility(View.GONE);

//                        Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
//                        Log.d("TapTargetViewSample", "You dismissed me :(");
                        view_map.setVisibility(View.GONE);

                    }
                });

            }

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
//                .addTestDevice(deviceId)
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

        button_enable_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 101);

            }
        });

        mapFragment.getMapAsync(this);

    }

    //    --------------------------------------------------All Overrides------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        Log.e("TOP",top+"");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.setPadding(5,200,5,0);
//        mMap.setPadding(Left,Top,Right,Bottom);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        textView_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!layout_frame.isShown()) {
                    Animation push_up_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
                    Animation push_up_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_out);
                    layout_frame.startAnimation(push_up_in);
                    layout_main.startAnimation(push_up_in);
                    layout_map.startAnimation(push_up_out);

                    layout_place.setVisibility(View.GONE);
                    layout_map.setVisibility(View.GONE);
                    layout_frame.setVisibility(View.VISIBLE);

                }

            }
        });

        fab_ways.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation push_down_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
                layout_frame.startAnimation(push_down_in);
                layout_main.startAnimation(push_down_in);
                layout_map.startAnimation(push_down_in);

                layout_place.setVisibility(View.VISIBLE);
                layout_map.setVisibility(View.VISIBLE);
                layout_frame.setVisibility(View.GONE);

            }
        });

        mMap.setOnMapLongClickListener(this);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Enter Destination");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try {
                    destinationlatLng = place.getLatLng();

                    getPlace(destinationlatLng);

                } catch (Exception e) {
//                    Log.e("Exception",e.toString());
                }

            }

            @Override
            public void onError(Status status) {
//                Log.e(TAG, "An error occurred: " + status);
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            //place marker at current position
            mMap.clear();

            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(18)
//                    .tilt( 50F) // viewing angle
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        image_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                            .zoom(18)
//                            .tilt(50F) // viewing angle
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                } catch (Exception e) {
//                    Log.e("Error",e.toString());
                }

            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); //5 seconds
        mLocationRequest.setFastestInterval(1000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            //remove previous current location marker and add new one at current position
            if (mCurrLocation != null) {
                mCurrLocation.remove();
            }
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //If you only need one location, unregister the listener
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
//            Log.e("Exception",e.toString());
        }
    }


    @Override
    public void onMapLongClick(final LatLng latLng) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Are You Sure to set this as Destination Point?..");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                destinationlatLng = latLng;

                getPlace(destinationlatLng);

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    public void locationTask() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            checkGPS();

        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location),
                    RC_LOCATION_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        finish();
        Intent intent = getIntent();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        finish();
        Intent intent = getIntent();
        startActivity(intent);
        overridePendingTransition(0, 0);

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Config.showLayout(MainActivity.this);

        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == 101) {
                finish();
                Intent intent = getIntent();
                startActivity(intent);
            }

        }
    }

//    --------------------------------------------------All Methods------------------------------------------------

    public void getPlace(final LatLng latLng) {

        mMap.clear();

        getNearBy(latLng, "1000");

        String location[] = String.valueOf(latLng).split(": ");

        String location1 = location[1].replace(")", "");
        final String location2 = location1.replace("(", "");

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location2 + "&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String response1 = response.body().string();

                try {

                    JSONObject object = new JSONObject(response1);

                    JSONArray array = object.getJSONArray("results");

                    JSONObject object1 = (JSONObject) array.get(0);

                    final String address1 = object1.getString("formatted_address");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            drawMarker(latLng, address1);

                            getNavigation(latLng);

                            layout_distance.setVisibility(View.VISIBLE);

                            adapter = new CustomDirectionAdapter(MainActivity.this, directionAdapters);

                            listDirection.setAdapter(adapter);

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(18)
                                    .bearing(30F) // orientation
                                    .tilt(50F) // viewing angle
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void getNearBy(final LatLng latLng, String range) {

        String location[] = String.valueOf(latLng).split(": ");

        String location1 = location[1].replace(")", "");
        final String location3 = location1.replace("(", "");

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location3 + "&type=restaurant&opennow&radius=" + range + "&strictbounds&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String response1 = response.body().string();

                LatLng latLng1;

                String name;
                String address;
                String place_id;
                boolean open_hrs;
                String rating = null;

                try {

                    JSONObject object = new JSONObject(response1);

                    JSONArray array = object.getJSONArray("results");

                    Log.e("Hotels Length", array.length() + "");

                    for (int i = 0; i < array.length(); i++) {
                        NearBy nearByAdapter = new NearBy();

                        JSONObject object1 = (JSONObject) array.get(i);

                        String latitude = object1.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String longitude = object1.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        String icon = object1.getString("icon");
                        name = object1.getString("name");
                        place_id = object1.getString("place_id");
                        address = object1.getString("vicinity");

                        if (object1.has("rating")) {
                            rating = object1.getString("rating");
                            nearByAdapter.setHotel_star(rating);
                        }

                        open_hrs = object1.getJSONObject("opening_hours").getBoolean("open_now");
                        nearByAdapter.setHotel_open_now(open_hrs);

                        latLng1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        nearByAdapter.setHotel_latitude_longitude(latLng1);
                        nearByAdapter.setHotel_icon(icon);
                        nearByAdapter.setHotel_name(name);
                        nearByAdapter.setHotel_address(address);

                        nearBy.add(nearByAdapter);

                        final LatLng finalLatLng = latLng1;
                        final String finalName = name;
                        final String finalRating = rating;
                        final String finalPlaceID = place_id;
                        final boolean finalOpen_hrs = open_hrs;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                // Creating an instance of MarkerOptions
                                final MarkerOptions markerOptions1 = new MarkerOptions().position(finalLatLng).title(finalName).snippet("Opens Now: " + finalOpen_hrs + "welcome" + finalRating + "welcome" + finalPlaceID);
                                // Setting latitude and longitude for the marker
                                markerOptions1.position(finalLatLng);
                                markerOptions1.draggable(false);
                                // Adding marker on the Google Map
                                mMap.addMarker(markerOptions1);

                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {

                                        LinearLayout info = new LinearLayout(MainActivity.this);
                                        info.setOrientation(LinearLayout.VERTICAL);

                                        if (!marker.getSnippet().equals("")) {
                                            final String snippet_Text[] = marker.getSnippet().split("welcome");

                                            Log.e("Snippet", marker.getTitle() + "," + marker.getSnippet());

                                            TextView title = new TextView(MainActivity.this);
                                            title.setTextColor(Color.RED);
                                            title.setGravity(Gravity.CENTER_HORIZONTAL);
                                            title.setTypeface(null, Typeface.BOLD);
                                            title.setText(marker.getTitle());

                                            TextView snippet = new TextView(MainActivity.this);
                                            snippet.setTextColor(Color.BLACK);
                                            snippet.setGravity(Gravity.CENTER_HORIZONTAL);
                                            snippet.setText(snippet_Text[0]);

                                            RatingBar ratingBar = new RatingBar(MainActivity.this);
                                            ratingBar.setMax(5);
                                            ratingBar.setIsIndicator(true);
                                            ratingBar.setScaleX(0.5f);
                                            ratingBar.setScaleY(0.5f);
                                            ratingBar.setStepSize(0.1f);

                                            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                                            stars.getDrawable(2).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                                            ratingBar.setRating(Float.parseFloat(snippet_Text[1]));

                                            Button order = new Button(MainActivity.this);
                                            order.setTextColor(Color.BLACK);
                                            order.setGravity(Gravity.CENTER);
                                            order.setText(R.string.order_now);
                                            order.setTextSize(14);

                                            info.addView(title);
                                            info.addView(snippet);
                                            info.addView(ratingBar);
                                            info.addView(order);

                                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                @Override
                                                public void onInfoWindowClick(Marker marker) {

//                                                    Config.shortToast(getApplicationContext(), snippet_Text[2]);
                                                    getNearByPlaceDetails(snippet_Text[2], marker.getTitle());

                                                }
                                            });

                                            return info;
                                        } else {
                                            TextView title = new TextView(MainActivity.this);
                                            title.setTextColor(Color.RED);
                                            title.setGravity(Gravity.CENTER_HORIZONTAL);
                                            title.setTypeface(null, Typeface.BOLD);
                                            title.setText(marker.getTitle());

                                            info.addView(title);

                                            return info;

                                        }

                                    }
                                });

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void getNearByPlaceDetails(final String place_id, final String hotel) {

        mDemoSlider.removeAllSliders();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String response1 = response.body().string();

                Log.e("Response", response1);

                String address;
                String mobile;
                String rating;

                try {

                    JSONObject object = new JSONObject(response1);

                    JSONObject object1 = object.getJSONObject("result");

                    address = object1.getString("formatted_address");
                    mobile = object1.getString("formatted_phone_number");
                    rating = object1.getString("rating");

                    JSONArray array = object1.getJSONArray("photos");

                    for (int i = 0; i < array.length(); i++)
                    {

                        JSONObject object2 = (JSONObject) array.get(i);

/*
                        String height = object2.getString("height");
                        String width = object2.getString("width");
*/
                        String photo_reference = object2.getString("photo_reference");

                        map.put(i+"",photo_reference);

                        Log.e("Photo Reference",map.get(i+""));
                    }

                    final String finalAddress = address;
                    final String finalMobile = mobile;
                    final String finalRating = rating;

                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {

                            Log.e("Photo Length",map.size()+"");

                            for(String name : map.keySet()){

                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                                // initialize a SliderLayout
                                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+map.get(name)+"&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo";
                                textSliderView
                                        .description(name)
                                        .image(url)
                                        .setScaleType(BaseSliderView.ScaleType.Fit);

                                //add your extra information
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("extra",name);

                                mDemoSlider.addSlider(textSliderView);

                            }

                            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            mDemoSlider.setDuration(4000);

                            //                  hotel_star_text,hotel_star_reviews
                            hotel_name.setText(hotel);
                            hotel_address.setText(finalAddress);
                            hotel_contact.setText(finalMobile);
                            hotel_star.setRating(Float.parseFloat(finalRating));

                            hotel_contact.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + finalMobile));
                                        startActivity(intent);
                                    }

                                }
                            });

                            button_order.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:"+finalMobile));
                                    startActivity(intent);

                                }
                            });

                            layout_hotel.setVisibility(View.VISIBLE);
                            mDemoSlider.startAutoCycle();

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void getNavigation(final LatLng latLng)
    {

        try
        {

            progressDialog.show();

            directionAdapters.clear();

            String destinationLocation[] = String.valueOf(latLng).split(": ");

            String destination = destinationLocation[1].replace(")","");
            destination_location = destination.replace("(","");

            final LatLng latLng1 = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

            String currentLocation[] = String.valueOf(latLng1).split(": ");

            String current = currentLocation[1].replace(")","");
            final String current_location = current.replace("(","");

//        https://maps.googleapis.com/maps/api/directions/json?origin=11.7436,79.7812&destination=11.905844,79.802825&departure_time=now&sensor=false&traffic_model=best_guess&key=AIzaSyBoMKPepiLc_8hdz-qywSRg5yqmswjamuU
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/directions/json?origin="+current_location+"&destination="+destination_location+"&departure_time=now&traffic_model=best_guess&mode=driving&sensor=false&key=AIzaSyBoMKPepiLc_8hdz-qywSRg5yqmswjamuU")
                    .build();

//        Log.e("URL","https://maps.googleapis.com/maps/api/directions/json?origin="+current_location+"&destination="+cus_latitude+","+cus_longitude+"&departure_time=now&traffic_model=best_guess&mode=driving&sensor=false&key=AIzaSyBoMKPepiLc_8hdz-qywSRg5yqmswjamuU");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // Error
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    try {

                        String res = response.body().string();

                        JSONObject jObject;

                        try {
                            jObject = new JSONObject(res);

                            // Starts parsing data
                            routes = parse(jObject);

                        } catch (Exception e) {
//                            Log.e("ParserTask",e.toString());
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {

                                try
                                {
                                    textView_time.setText(duration);
                                    textView_distance.setText("("+distance+")");

                                    ArrayList<LatLng> points;

                                    PolylineOptions lineOptions = null;

                                    // Traversing through all the routes
                                    for (int i = 0; i < routes.size(); i++) {
                                        points = new ArrayList<>();
                                        lineOptions = new PolylineOptions();

                                        // Fetching i-th route
                                        List<HashMap<String, String>> path = routes.get(i);

                                        // Fetching all the points in i-th route
                                        for (int j = 0; j < path.size(); j++) {
                                            HashMap<String, String> point = path.get(j);

                                            double lat = Double.parseDouble(point.get("lat"));
                                            double lng = Double.parseDouble(point.get("lng"));
                                            LatLng position = new LatLng(lat, lng);

                                            points.add(position);
                                        }

                                        // Adding all the points in the route to LineOptions
                                        lineOptions.addAll(points);
                                        lineOptions.width(10);
                                        lineOptions.color(Color.parseColor("#2196F3"));
                                        lineOptions.geodesic(true);
                                    }

                                    // Drawing polyline in the Google Map for the i-th route
                                    if(lineOptions != null) {
                                        mMap.addPolyline(lineOptions);

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                        //the include method will calculate the min and max bound.
                                        builder.include(latLng);
                                        builder.include(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                                        LatLngBounds bounds = builder.build();

                                        int width = getResources().getDisplayMetrics().widthPixels;
                                        int height = getResources().getDisplayMetrics().heightPixels-200;
                                        int padding = (int) (width * 0.33); // offset from edges of the map 10% of screen
//                                            int padding1 = (int) (height* 0.3); // offset from edges of the map 10% of screen

                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                        mMap.animateCamera(cu);

                                        progressDialog.dismiss();

                                        adapter.notifyDataSetChanged();

                                    }
                                    else {
                                        Log.e("onPostExecute","without Polylines drawn");
                                    }

                                }
                                catch (Exception e)
                                {
//                                    Log.e("Number Format",e.toString());
                                }

                            }
                        });

                    }
                    catch (Exception e)
                    {
//                        Log.e("Error",e.toString());
                    }

                }
            });

        }
        catch (Exception e)
        {
//            Log.e("Exception",e.toString());

            progressDialog.dismiss();

            finish();
            Intent intent = getIntent();
            startActivity(intent);
            Config.shortToast(MainActivity.this,"Can't able to get Current Location. Try Again.");

        }

    }

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /* Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(0)).getJSONArray("legs");

                JSONObject object2 = (JSONObject) jLegs.get(0);

                distance = object2.getJSONObject("distance").getString("text");
                duration = object2.getJSONObject("duration").getString("text");
                end_address = object2.getString("end_address");

//                Log.e("Distance & Duration",distance +","+duration);

                List path = new ArrayList<>();

                /* Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /* Traversing all steps */
                    for(int k=0;k<jSteps.length();k++) {

                        String polyline;

                        String distance1 = (((JSONObject) jSteps.get(k)).getJSONObject("distance")).getString("text");
                        String duration2 = (((JSONObject) jSteps.get(k)).getJSONObject("duration")).getString("text");
                        String html_instructions = (((JSONObject) jSteps.get(k)).getString("html_instructions"));

                        if (((JSONObject) jSteps.get(k)).has("maneuver")) {
                            String maneuver = (((JSONObject) jSteps.get(k)).getString("maneuver"));
//                            Log.e("Maneuver", maneuver + "," + distance1 + "," + duration2 + "," + html_instructions);

                            DirectionAdapter history = new DirectionAdapter();

                            history.setDistance(distance1);
                            history.setDuration(duration2);
                            history.setHtml_instructions(html_instructions);
                            history.setManeuver(maneuver);

                            directionAdapters.add(history);

                        }

                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /* Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception ignored){
        }

        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void checkConnection()
    {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            Config.shortToast(MainActivity.this,"Not connected to Internet");
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            String[] loc = intent.getExtras().getString("CurLoc").split(" ");

            String[] latlon = loc[1].split(",");

            Latitude = latlon[0];
            Longitude = latlon[1];

        }
    }

    public void checkGPS()
    {
        if (!statusOfGPS)
        {
            layout_map_place.setVisibility(View.GONE);
            layout_gps.setVisibility(View.VISIBLE);
            showSettingsAlert(MainActivity.this);
        }
    }

    public void showSettingsAlert(final Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,101);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void drawMarker(LatLng point,String name){
        // Creating an instance of MarkerOptions snippet("Opens Now: "+finalOpen_hrs+"@&#$#"+finalRating)
        MarkerOptions markerOptions = new MarkerOptions().position(point).title(name).snippet("");
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        markerOptions.draggable(true);
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    //    --------------------------------------------------Life Cycles------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();

        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    protected void onRestart() {
        mDemoSlider.startAutoCycle();
        super.onRestart();
    }

    @Override
    protected void onStop() {

        mDemoSlider.stopAutoCycle();
        super.onStop();

        try
        {
            unregisterReceiver(myReceiver);
        }
        catch (Exception e)
        {
//            Log.e("Ex",e.toString());
        }

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

    }

    //    --------------------------------------------------App End------------------------------------------------

    @Override
    public void onBackPressed() {

        if (layout_frame.isShown())
        {
            Animation push_down_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
            layout_frame.startAnimation(push_down_in);
            layout_main.startAnimation(push_down_in);
            layout_map.startAnimation(push_down_in);

            layout_place.setVisibility(View.VISIBLE);
            layout_map.setVisibility(View.VISIBLE);
            layout_frame.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
        }
    }

}
