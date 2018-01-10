package com.cherritech.bookontravel.services;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class ServiceLocation extends Service {

    private LocationManager locMan;
    Boolean locationChanged;
    private Handler handler = new Handler();

    public final static String MY_ACTION = "MY_ACTION";
    public static Location curLocation;
    public static boolean isService = true;

    String destination;
    MediaPlayer mMediaPlayer;

    LocationListener gpsListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            if (curLocation == null) {
                curLocation = location;
                locationChanged = true;

                //lat/lng: (11.752007073876568,79.7660918533802)

            } else if (curLocation.getLatitude() == location.getLatitude() && curLocation.getLongitude() == location.getLongitude()) {
                locationChanged = false;
                return;
            } else
                locationChanged = true;

            curLocation = location;

            if (locationChanged)
                locMan.removeUpdates(gpsListener);

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == 0)// UnAvailable
            {
                Log.e("UnAvailable",provider+","+extras);
            } else if (status == 1)// Trying to Connect
            {
                Log.e("Trying to Connect",provider+","+extras);
            } else if (status == 2) {// Available
                Log.e("Available",provider+","+extras);
            }
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();

        curLocation = getBestLocation();

        mMediaPlayer = new MediaPlayer();

        if (curLocation == null){
//            Toast.makeText(getBaseContext(), "Unable to get your location", Toast.LENGTH_SHORT).show();
            Log.e("Location","Unable to get your location");
        }
/*
        else {
//            Log.e("Current Location", curLocation.toString());
        }
*/

        isService = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStart(Intent i, int startId) {

        destination = i.getStringExtra("Destination");

//        Log.e("Intent",destination);

        handler.postDelayed(GpsFinder, 1);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(GpsFinder);
        handler = null;
//        Toast.makeText(this, "Stop services", Toast.LENGTH_SHORT).show();
        isService = false;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public Runnable GpsFinder = new Runnable() {
        public void run() {

            Location tempLoc = getBestLocation();
            if (tempLoc != null)
                curLocation = tempLoc;
            handler.postDelayed(GpsFinder, 10000);// register again to start after 1 seconds...
        }
    };

    public Location getBestLocation() {
        Location gpslocation = null;
        Location networkLocation = null;

        if (locMan == null) {
            locMan = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5f, gpsListener);// here you can set the 2nd argument time interval also that after how much time it will get the gps location

                    }
                }
                else
                    locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5f, gpsListener);// here you can set the 2nd argument time interval also that after how much time it will get the gps location

                try
                {
                    gpslocation = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    String destinationLocation[] = destination.split(": ");

                    String destination = destinationLocation[1].replace(")","");
                    String destination_location[] = destination.replace("(","").split(",");

                    if (distance(Double.parseDouble(destination_location[0]),Double.parseDouble(destination_location[1]), gpslocation.getLatitude(),gpslocation.getLongitude()) <= 1.0f) { // if distance < 1 kilometer we take locations as equal
                        //do what you want to do...

                        playSound(getBaseContext(), getAlarmUri());

                    }

                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);
                    intent.putExtra("CurLoc",gpslocation.toString());
                    sendBroadcast(intent);

                }
                catch (Exception e)
                {
//                    Log.e("GPSException",e.toString());
                }

            }
            if(locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000, 5f, gpsListener);

                    }
                }
                else
                    locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000, 5f, gpsListener);

                try
                {
                    networkLocation = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    String destinationLocation[] = destination.split(": ");

                    String destination = destinationLocation[1].replace(")","");
                    String destination_location[] = destination.replace("(","").split(",");

                    if (distance(Double.parseDouble(destination_location[0]),Double.parseDouble(destination_location[1]), networkLocation.getLatitude(),networkLocation.getLongitude()) <= 1.0f) { // if distance < 1 kilometer we take locations as equal
                        //do what you want to do...

                        playSound(getBaseContext(), getAlarmUri());

                    }

                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);
                    intent.putExtra("CurLoc",networkLocation.toString());
                    sendBroadcast(intent);
                }
                catch (Exception e)
                {
//                    Log.e("NETWORKException",e.toString());
                }

            }
        } catch (IllegalArgumentException e) {
//            Log.e("error", e.toString());
        }
        if(gpslocation==null && networkLocation==null)
            return null;

        if(gpslocation!=null && networkLocation!=null){
            if(gpslocation.getTime() < networkLocation.getTime()){
                return networkLocation;
            }else{
                return gpslocation;
            }
        }
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        return null;
    }

//-------------------------------------Methods------------------------------------------------

    /** calculates the distance between two locations in Kilometers*/
    private float distance(double lat1, double lng1, double lat2, double lng2) {

//        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double earthRadius = 6371; // in kilometer, change to 3958.75 for miles output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return Float.parseFloat(String.valueOf(dist)); // output distance, in MILES
    }

    private void playSound(Context context, Uri alert) {

        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setScreenOnWhilePlaying(true);
//                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {

        /*
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
*/
/*
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
*/
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + this.getPackageName() + "/raw/alarm");
    }

}
