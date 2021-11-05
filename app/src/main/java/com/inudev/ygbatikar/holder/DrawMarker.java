package com.inudev.ygbatikar.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Rem on 2/6/2017 on 7:36 AM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class DrawMarker {

    public static DrawMarker getInstance(Context context) {
        return new DrawMarker(context);
    }

    private Context context;

    private DrawMarker(Context context) {
        this.context = context;
    }

    public void draw(@NonNull GoogleMap googleMap, LatLng location, int resDrawable, String title, String street) {
        Drawable circleDrawable = ContextCompat.getDrawable(context, resDrawable);
        assert circleDrawable != null;
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .snippet(street)
                .icon(markerIcon)
        );
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    @NonNull
    private BitmapDescriptor getMarkerIconFromDrawable(@NonNull Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
