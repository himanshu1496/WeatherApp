package com.himanshubaghel.dev.mausam.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Methods {

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("d MMM, hh:mm a");

    public static String formateCalenderToDateTimeString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return Methods.DATE_TIME_FORMAT.format(calendar.getTime());
    }

    public static HashMap<String, String> PrepareQueryFromLocation(Location location){
        HashMap<String, String> queryMap = new HashMap<>();

        String query = "select * from weather.forecast where woeid in (SELECT woeid FROM geo.placefinder WHERE text='"+location.getLatitude()+", "+location.getLongitude()+"' and gflags='R') and u = 'c'";
        queryMap.put("q",query);
        queryMap.put("format", "json");
        queryMap.put("env", "store://datatables.org/alltableswithkeys");
        return queryMap;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
}
