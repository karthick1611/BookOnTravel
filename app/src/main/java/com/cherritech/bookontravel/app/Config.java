package com.cherritech.bookontravel.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Config {

/*
    Place Types
    https://developers.google.com/places/web-service/supported_types
    Search Nearby hotels
    https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=11.932214,79.815462&radius=2000&type=restaurant&opennow&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo
*/

/*
    PLace Details
    https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJHfT0oYFhUzoR-lKiKk2-fkU&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo
*/

/*
    Get Photo
    https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="photo Reference"&key=AIzaSyCc-OtUroQSsYJ8WiIOlvx2uHZbj6NNlUo
*/
/*
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
*/
    public static void showLayout(final Activity activity)
    {
        Intent intent = (activity).getIntent();
        (activity).startActivity(intent);
        (activity).finish();
        (activity).overridePendingTransition(0,0);
    }

    public static void shortToast(final Context context, final String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }
}