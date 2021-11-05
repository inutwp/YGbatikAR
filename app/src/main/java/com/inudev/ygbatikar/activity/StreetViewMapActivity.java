package com.inudev.ygbatikar.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.inudev.ygbatikar.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.inudev.ygbatikar.model.Shop;

import java.util.Objects;

public class StreetViewMapActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    StreetViewPanorama streetView;
    TextView ntoko_street_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view_map);

        initStreetView();
        initViewLayout();
    }

    private void initStreetView() {
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.google_map_street_view);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    private void initViewLayout() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            ntoko_street_view = findViewById(R.id.ntoko_street_view);
            ntoko_street_view.setText(Objects.requireNonNull(mShop).getShopName());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    @Override
    public void onStreetViewPanoramaReady(@Nullable StreetViewPanorama streetViewPanorama) {
        try {
            streetView = streetViewPanorama;
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            assert mShop != null;
            final double latitude = mShop.getShopLatitude();
            final double longitude = mShop.getShopLongitude();

            final LatLng pertokoan_street_view = new LatLng(latitude, longitude);

            assert streetViewPanorama != null;
            streetViewPanorama.setPosition(pertokoan_street_view);
            streetViewPanorama.setStreetNamesEnabled(true);
            streetViewPanorama.setPanningGesturesEnabled(true);
            streetViewPanorama.setZoomGesturesEnabled(true);
            streetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder().
                            orientation(new StreetViewPanoramaOrientation(20, 20))
                            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                            .bearing(30)
                            .build(), 1500
            );
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
