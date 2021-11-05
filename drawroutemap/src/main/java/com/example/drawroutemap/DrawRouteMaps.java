package com.example.drawroutemap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rem on 11/9/2017 on 9:25 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class DrawRouteMaps {

    private static DrawRouteMaps instance;
    private Context context;

    public static DrawRouteMaps getInstance(Context context) {
        instance = new DrawRouteMaps();
        instance.context = context;
        return instance;
    }

    public void draw(LatLng origin, LatLng destination, GoogleMap googleMap){
        String url_route = FetchUrl.getUrl(origin, destination);
        DrawRoute drawRoute = new DrawRoute(googleMap);
        drawRoute.execute(url_route);
    }

    public static Context getContext() {
        return instance.context;
    }
}
