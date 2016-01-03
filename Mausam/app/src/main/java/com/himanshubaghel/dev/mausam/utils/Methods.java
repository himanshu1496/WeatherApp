package com.himanshubaghel.dev.mausam.utils;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.HashMap;

public class Methods {

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

}
