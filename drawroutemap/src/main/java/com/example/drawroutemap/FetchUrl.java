package com.example.drawroutemap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rem on 11/9/2017 on 9:26 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

class FetchUrl {
    static String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }
}
