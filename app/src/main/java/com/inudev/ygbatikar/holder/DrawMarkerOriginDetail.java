package com.inudev.ygbatikar.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Rem on 11/25/2017 on 9:13 AM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class DrawMarkerOriginDetail {

    private Context context;

    private DrawMarkerOriginDetail(Context context) {
        this.context = context;
    }

    public static DrawMarkerOriginDetail getInstance(Context context) {
        return new DrawMarkerOriginDetail(context);
    }

    public void draw(GoogleMap googleMap, Marker marker, LatLng location, int resDrawable) {
        Drawable circleDrawable = ContextCompat.getDrawable(context, resDrawable);
        assert circleDrawable != null;
        BitmapDescriptor makerIcon = getMarkerIconFromDrawable(circleDrawable);

        if (marker != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Your Location")
                    .anchor(0.5f, 0.5f)
                    .icon(makerIcon));
        } else {
            googleMap.clear();
        }
    }

    @NonNull
    private BitmapDescriptor getMarkerIconFromDrawable(@NonNull Drawable circleDrawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(circleDrawable.getIntrinsicWidth(), circleDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        circleDrawable.setBounds(0, 0, circleDrawable.getIntrinsicWidth(), circleDrawable.getIntrinsicHeight());
        circleDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
