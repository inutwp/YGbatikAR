package com.inudev.ygbatikar.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aquery.AQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.drawroutemap.DrawRouteMaps;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.DrawMarkerDestinationDetail;
import com.inudev.ygbatikar.holder.DrawMarkerRoute;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.inudev.ygbatikar.model.Shop;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsDetailActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnStreetViewPanoramaReadyCallback {

    private static final String TAG = "MapsDetailActivity";
    private FloatingActionButton mFloatingActionButton;
    private StreetViewPanorama streetView;
    private ProgressDialog mProgressDialog;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private AQuery aq;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private double mAzimut = 90;
    private double mLatitudeStreetView = 0;
    private double mLongitudeStreetView = 0;
    public static int UPDATE_INTERVAL = 2000;
    public static int DISPLACEMENT = 1;
    public SensorManager mSensorManager;
    public int waktuTempuhStep;
    public int waktuTempuhCar;
    public double distance = 0;
    public String jaraktoString;
    public String jaraktoString2;

    boolean showFAB = true;

    CoordinatorLayout coordinatorLayout;

    TextView
            alamat_toko_detail,
            toSingleAR,
            jarak_marker_meter,
            car, step;

    ImageView image_toko_detail;

    String jarakM;
    String jarakKM;
    Marker mMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_detail);
        aq = new AQuery(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);

        initCheckSensor();
        initMapStreetView();
        initContentandClick();
        setupWindowAnimations();

        mProgressDialog = ProgressDialog.show(this, null, "Mendapatkan Lokasi...", true, false);
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimations() {
        android.transition.Fade fade = new android.transition.Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }

    @SuppressLint("SetTextI18n")
    private void initContentandClick() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            step = findViewById(R.id.step);
            car = findViewById(R.id.car);

            aq.id(R.id.name_alamat_toko).text(Objects.requireNonNull(mShop).getShopAddress());

            jarak_marker_meter = findViewById(R.id.jarak_marker_meter);

            aq.id(R.id.operasional_toko_detail).text("Setiap Hari " + ((char) 45) + " 08.00 - 21.00");

            aq.id(R.id.nama_toko_detail).text(mShop.getShopName());

            aq.id(R.id.openMaps).click(view -> {
                final Shop shopOpenMap = (Shop)
                        getIntent()
                            .getExtras()
                                .get("Shop");

                final double latitude = Objects.requireNonNull(shopOpenMap).getShopLatitude();
                final double longitude = shopOpenMap.getShopLongitude();
                String googleMap = "com.google.android.apps.maps";
                Intent mapIntent;
                Uri gmmIntentUri;
                gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(googleMap);
                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException eX
                        ) {
                    try {
                        Intent unrestrictedintent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        startActivity(unrestrictedintent);
                    } catch (ActivityNotFoundException innereX) {
                        Toast.makeText(MapsDetailActivity.this, "Please install a Google Maps App", Toast.LENGTH_LONG).show();
                    }
                }
            });

            alamat_toko_detail = findViewById(R.id.alamat_toko_detail);
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            String jambuka = mShop.getShopOpenTime();
            String subJambuka = jambuka.substring(0, 2);

            String jamtutup = mShop.getShopCloseTime();
            String subJamtutup = jamtutup.substring(0, 2);

            int JamBuka = Integer.parseInt(subJambuka);
            int JamTutup = Integer.parseInt(subJamtutup);

            int JamHampirBuka = JamBuka - 1;
            int JamHampirTutup = JamTutup - 1;

            if (hour == JamHampirBuka) {
                alamat_toko_detail.setText("Hampir Buka");
                alamat_toko_detail.setTextColor(Color.WHITE);
                alamat_toko_detail.setBackground(getResources().getDrawable(R.drawable.bg_notif_hampirbuka));
                alamat_toko_detail.setTextColor(getResources().getColor(R.color.Primary_Light_Brown_200));
            } else if (hour >= JamBuka && hour < JamHampirTutup) {
                alamat_toko_detail.setText("Buka");
                alamat_toko_detail.setTextColor(Color.WHITE);
                alamat_toko_detail.setBackground(getResources().getDrawable(R.drawable.bg_notif_buka));
                alamat_toko_detail.setTextColor(getResources().getColor(R.color.Primary_Light_Flat_Navigation));
            } else if (hour == JamHampirTutup) {
                alamat_toko_detail.setText("Hampir Tutup");
                alamat_toko_detail.setTextColor(Color.WHITE);
                alamat_toko_detail.setBackground(getResources().getDrawable(R.drawable.bg_notif_hampirtutup));
                alamat_toko_detail.setTextColor(getResources().getColor(R.color.Primary_Light_Red_200));
            } else {
                alamat_toko_detail.setText("Tutup");
                alamat_toko_detail.setTextColor(Color.WHITE);
                alamat_toko_detail.setBackground(getResources().getDrawable(R.drawable.bg_notif_tutup));
                alamat_toko_detail.setTextColor(getResources().getColor(R.color.Primary_Light_Red_600));
            }

            aq.id(R.id.no_telp_toko_detail).text(mShop.getShopPhoneNumber());

            image_toko_detail = findViewById(R.id.image_toko_detail);
            if (image_toko_detail != null) {
                Glide.with(this)
                        .load(mShop.getShopPictureProfile())
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                        .into(image_toko_detail);
            }

            aq.id(R.id.refresh_map).click(view -> {
                finish();
                overridePendingTransition(R.anim.alphaanim, R.anim.alphaanim);
                aq.open(getIntent());
            });

            final Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
            final Animation shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);

            mFloatingActionButton.setVisibility(View.VISIBLE);
            mFloatingActionButton.startAnimation(growAnimation);

            shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFloatingActionButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            coordinatorLayout = findViewById(R.id.coordinator_maps);
            View bottomset = coordinatorLayout.findViewById(R.id.maps_detail_bottom);

            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomset);

            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                            if (showFAB)
                                mFloatingActionButton.startAnimation(shrinkAnimation);
                            break;

                        case BottomSheetBehavior.STATE_COLLAPSED:
                            showFAB = true;
                            mFloatingActionButton.setVisibility(View.VISIBLE);
                            mFloatingActionButton.startAnimation(growAnimation);
                            break;

                        case BottomSheetBehavior.STATE_EXPANDED:
                            showFAB = false;
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    private void initMapStreetView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        MapsDetailActivity.this.runOnUiThread(() -> {
            mMapFragment.getMapAsync(MapsDetailActivity.this);

            StreetViewPanoramaFragment streetViewPanoramaFragment =
                    (StreetViewPanoramaFragment) getFragmentManager()
                            .findFragmentById(R.id.google_map_street_view);
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(MapsDetailActivity.this);
        });
    }

    private void initCheckSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try {
            assert mSensorManager != null;
            if (mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                toSingleAR = findViewById(R.id.toSingleAR);
                toSingleAR.setOnClickListener(view -> {
                    int hitungditanceTo = Integer.parseInt(jaraktoString2);
                    int hitungHarvesine = Integer.parseInt(jaraktoString);
                    new AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("Pengukuran Jarak ke " + Objects.requireNonNull(mShop).getShopName())
                            .setMessage("Method distanceTo() : " + jaraktoString2 + " m" + "\n" +
                                    "Formula Harvesine : " + jaraktoString + " m" + "\n")
                            .show();
                });

                mFloatingActionButton = findViewById(R.id.gmail_fab);
                mFloatingActionButton.setOnClickListener(view -> {
                    Intent loadMaps = new Intent(MapsDetailActivity.this, NavigationActivity.class);
                    loadMaps.putExtra("Shop", mShop);
                    MapsDetailActivity.this.startActivity(loadMaps);
                });
            } else {
                toSingleAR = findViewById(R.id.toSingleAR);
                toSingleAR.setOnClickListener(view -> {
                    FragmentManager mFragmentManager = getSupportFragmentManager();
                    CompassSensorCheckFragment compassSensorCheckFragment = new CompassSensorCheckFragment();
                    compassSensorCheckFragment.show(mFragmentManager, "compasscheckdialog");
                });

                mFloatingActionButton = findViewById(R.id.gmail_fab);
                mFloatingActionButton.setOnClickListener(view -> {
                    FragmentManager mFragmentManager = getSupportFragmentManager();
                    CompassSensorCheckFragment compassSensorCheckFragment = new CompassSensorCheckFragment();
                    compassSensorCheckFragment.show(mFragmentManager, "compasscheckdialog");
                });
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        streetView = streetViewPanorama;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_maps_detail_json
            ));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "cant find style. Error: ", e);
        }

        mGoogleMap = googleMap;
        initMap();
    }


    private void initMap() {
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);

            initDrawMaps();
        }
    }

    private void initDrawMaps() {
        MapsDetailActivity.this.runOnUiThread(() -> {
            try {
                if (mCurrentLocation != null) {
                    mLatitude = mCurrentLocation.getLatitude();
                    mLongitude = mCurrentLocation.getLongitude();
                }

                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                final double latitude = mShop.getShopLatitude();
                final double longitude = mShop.getShopLongitude();
                final LatLng destination = new LatLng(latitude, longitude);
                final LatLng origin = new LatLng(mLatitude, mLongitude);

                DrawRouteMaps.getInstance(MapsDetailActivity.this)
                        .draw(origin, destination, mGoogleMap);

                DrawMarkerRoute.getInstance(getApplicationContext())
                        .draw(mGoogleMap, mMarker, origin, R.drawable.ic_001_placeholder_2);

                DrawMarkerDestinationDetail.getInstance(getApplicationContext())
                        .draw(mGoogleMap, destination, R.drawable.ic_013_placeholder, mShop.getShopName(), mShop.getShopAddress());

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(origin)
                        .include(destination).build();
                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                mGoogleMap.moveCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, displaySize.y, 300, 15));

                //LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
                //latlngBuilder.include(origin);
                //latlngBuilder.include(destination);

                //LatLngBounds bounds = latlngBuilder.build();

                //int width = getResources().getDisplayMetrics().widthPixels;
                //int height = getResources().getDisplayMetrics().heightPixels;
                //int paddingMap = (int) (width * 0.2);
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingMap);
                //mGoogleMap.animateCamera(cameraUpdate);
                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            } catch (NullPointerException n) {
                n.printStackTrace();
                Toast.makeText(MapsDetailActivity.this, "Koneksi Internet Bermasalah!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initGeocoding() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

            if (addresses.size() > 0) {
                Address getAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                strAddress.append(getAddress.getLocality() + "," + getAddress.getAdminArea());

                aq.id(R.id.origin_location).text(strAddress.toString());
            } else {
                aq.id(R.id.origin_location).text("Mencari Alamat");
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initCalculate() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            Location originLoc = new Location("Origin");
            originLoc.setLatitude(mLatitude);
            originLoc.setLongitude(mLongitude);

            Location destinationLoc = new Location("Destination");
            destinationLoc.setLatitude(Objects.requireNonNull(mShop).getShopLatitude());
            destinationLoc.setLongitude(mShop.getShopLongitude());

            distance = originLoc.distanceTo(destinationLoc);
            jaraktoString2 = new DecimalFormat("####").format(distance);

            //Harvesine Formula
            final double R = 6371;

            double latDestination = Math.toRadians(mShop.getShopLatitude());
            double latOrigin = Math.toRadians(mLatitude);
            double lngDestination = Math.toRadians(mShop.getShopLongitude());
            double lngOrigin = Math.toRadians(mLongitude);

            double latDistance = (latDestination - latOrigin);
            double lngDistance = (lngDestination - lngOrigin);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(latOrigin) * Math.cos(latDestination) *
                            Math.sin(lngDistance / 2)
                            * Math.sin(lngDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double Jarak = R * c * 1000;

            //Menentukan Jarak Tempuh dan Waktu Tempuh
            jaraktoString = new DecimalFormat("####").format(Jarak);
            int jaraktoInt = Integer.parseInt(jaraktoString);

            if (jaraktoInt < 1000) {
                jarakM = new DecimalFormat("####").format(Jarak);
                jarak_marker_meter.setText(jarakM + " m");
            } else {
                Double jaraktoKM = Jarak / 1000;
                jarakKM = new DecimalFormat("#.#").format(jaraktoKM);
                jarak_marker_meter.setText(jarakKM + " Km");
            }

            int totJarak = Integer.parseInt(jaraktoString);

            int stepSpeedMinute = 60;

            waktuTempuhStep = totJarak / stepSpeedMinute;
            if (waktuTempuhStep < 1) {
                step.setText("< 1 Menit");
            } else if (waktuTempuhStep < 60) {
                String wwkt = String.valueOf(waktuTempuhStep);

                step.setText(wwkt + " Menit");
            } else if (waktuTempuhStep == 60) {
                step.setText("1 Jam");
            } else {
                step.setText("> 1 Jam");
            }

            int carSpeed = 20 * 5 / 18;

            waktuTempuhCar = totJarak / (carSpeed * 60);
            if (waktuTempuhCar < 1) {
                car.setText("-");
            } else {
                String wwktCar = String.valueOf(waktuTempuhCar);

                car.setText(wwktCar + " Menit");
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    private void initStreetView() {
        MapsDetailActivity.this.runOnUiThread(() -> {
            try {
                if (streetView != null) {

                    if (mCurrentLocation != null) {
                        mLatitudeStreetView = mCurrentLocation.getLatitude();
                        mLongitudeStreetView = mCurrentLocation.getLongitude();
                    }

                    final LatLng pertokoan_stret_view = new LatLng(mLatitudeStreetView, mLongitudeStreetView);

                    streetView.setPosition(pertokoan_stret_view);
                    streetView.setStreetNamesEnabled(false);
                    streetView.setPanningGesturesEnabled(true);
                    streetView.setZoomGesturesEnabled(true);
                    streetView.animateTo(
                            new StreetViewPanoramaCamera.Builder().
                                    orientation(new StreetViewPanoramaOrientation(20, 20))
                                    .zoom(streetView.getPanoramaCamera().zoom)
                                    .bearing((float) mAzimut)
                                    .build(), 2000
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

            String provider = mLocationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            try {
                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    mCurrentLocation = location;
                    onLocationChanged(mCurrentLocation);
                } else {
                    Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        mCurrentLocation = networkLocation;
                        onLocationChanged(mCurrentLocation);
                    }
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
                Log.e(TAG, "location: ", ne);
            }

            mLocationManager.requestLocationUpdates(provider, UPDATE_INTERVAL, DISPLACEMENT, this);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            Log.e(TAG, "onResume: ", iae);
        }

    }

    public void onLocationChanged(Location mCurrentLocation) {
        try {
            if (mCurrentLocation != null) {
                mLatitude = mCurrentLocation.getLatitude();
                mLongitude = mCurrentLocation.getLongitude();

                initCalculate();
                initStreetView();
                initGeocoding();
                mProgressDialog.dismiss();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Log.e(TAG, "onLocationChanged: ", npe);
        }
    }

    @Override
    protected void onPause() {
        mLocationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLocationManager.removeUpdates(this);
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapFragment.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapFragment != null) {
            mMapFragment.onSaveInstanceState(outState);
        }
    }
}
