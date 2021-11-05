package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.opengl.util.LowPassFilter;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.drawroutemap.DrawRouteMaps;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.helper.ArrowNavigationView;
import com.inudev.ygbatikar.holder.DrawMarkerDestinationDetail;
import com.inudev.ygbatikar.holder.DrawMarkerOriginDetail;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.inudev.ygbatikar.model.Shop;
import com.tapadoo.alerter.Alerter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Objects;

public class NavigationActivity extends AppCompatActivity implements SensorEventListener, LocationListener, OnMapReadyCallback {

    private static final String TAG = "NavigationActivity";
    private static final int LOCATION_MIN_TIME = 10000;
    private static final int LOCATION_MIN_DISTANCE = 1;
    private SensorManager mSensorManager;
    private Sensor mSensorGravity;
    private Sensor mSensorMagnetic;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private GeomagneticField mGeomagneticField;
    private GoogleMap mGoogleMap;
    private ArrowNavigationView mArrowNavigationView;
    private SupportMapFragment mMapFragment;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mRotation = new float[9];
    private float[] mOrientation = new float[3];
    public float[] mSmoothed = new float[3];
    public static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final String NA = "N/A";
    public static final String FIXED = "FIXED";
    public Marker mMarker;
    public double mLatitude = 0;
    public double mLongitude = 0;
    double mAzimuth = 0;
    float mBearing = 0;
    float mDirectionTo;
    //double mTilt = 0;
    double mJarak;
    int mRange;

    TextView textDirection, textBearingRadian, textLat, textLng, txtJarak, bearingtoPertokoan;

    ImageView change_view;

    RelativeLayout ar_relay;

    BeyondarFragmentSupport mBeyondarFragmentCompass;
    World mWorldCompass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation);

        initBeyondAR();
        initCameraPermiss();
        initMapReady();
        initContentandClick();
        initARLocation();
    }

    private void initARLocation() {
        BeyondarLocationManager.addWorldLocationUpdate(mWorldCompass);
        BeyondarLocationManager.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
    }

    private void initContentandClick() {
        textDirection = findViewById(R.id.bearing_compass);

        textBearingRadian = findViewById(R.id.bearing_radian_compass);

        bearingtoPertokoan = findViewById(R.id.bearingt_to_Pertokoan);

        textLat = findViewById(R.id.lat_origin);

        textLng = findViewById(R.id.long_origin);

        txtJarak = findViewById(R.id.jarak_compass);

        mArrowNavigationView = findViewById(R.id.compassView);

        ar_relay = findViewById(R.id.ar_relay);

        change_view = findViewById(R.id.change_view);
        change_view.setOnClickListener(view -> {
            if (ar_relay.getVisibility() == View.GONE && textLat.getVisibility() == View.GONE && textLng.getVisibility() == View.GONE) {
                ar_relay.setVisibility(View.VISIBLE);
                textLat.setVisibility(View.VISIBLE);
                textLng.setVisibility(View.VISIBLE);
                change_view.setImageResource(R.drawable.ic_map_black_all_24dp);
                textDirection.setTextColor(Color.WHITE);
                textBearingRadian.setTextColor(Color.WHITE);
                txtJarak.setTextColor(Color.WHITE);
                bearingtoPertokoan.setTextColor(Color.WHITE);
            } else {
                ar_relay.setVisibility(View.GONE);
                textLat.setVisibility(View.GONE);
                textLng.setVisibility(View.GONE);
                textDirection.setTextColor(Color.BLACK);
                textBearingRadian.setTextColor(Color.BLACK);
                txtJarak.setTextColor(Color.BLACK);
                bearingtoPertokoan.setTextColor(Color.BLACK);
                change_view.setImageResource(R.drawable.ic_ar_camera_black_24dp);
                overridePendingTransition(R.anim.alphaanim, R.anim.alphaanim);
            }
        });
    }

    private void initMapReady() {
        mMapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map_view);

        NavigationActivity.this.runOnUiThread(() -> mMapFragment.getMapAsync(NavigationActivity.this));
    }

    private void initBeyondAR() {
        mBeyondarFragmentCompass = (BeyondarFragmentSupport) getSupportFragmentManager()
                .findFragmentById(R.id.arview_compass);

        mWorldCompass = initArView();

        mBeyondarFragmentCompass.setWorld(mWorldCompass);
    }

    private void initCameraPermiss() {
        if (ContextCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NavigationActivity.this,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG, "initCameraPermiss:Permission Camera");
            } else {
                ActivityCompat.requestPermissions(NavigationActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateTextDirection(double mAzimuth) {
        mRange = (int) (mAzimuth / (360f / 16f));
        String dirTxt = "";
        String dirTxtIndo = "";

        if (mRange == 15 || mRange == 0) {
            dirTxt = "N";
            dirTxtIndo = "Utara";
        } else if (mRange == 1 || mRange == 2) {
            dirTxt = "NE";
            dirTxtIndo = "Timur Laut";
        } else if (mRange == 3 || mRange == 4) {
            dirTxt = "E";
            dirTxtIndo = "Timur";
        } else if (mRange == 5 || mRange == 6) {
            dirTxt = "SE";
            dirTxtIndo = "Tenggara";
        } else if (mRange == 7 || mRange == 8) {
            dirTxt = "S";
            dirTxtIndo = "Selatan";
        } else if (mRange == 9 || mRange == 10) {
            dirTxt = "SW";
            dirTxtIndo = "Barat Daya";
        } else if (mRange == 11 || mRange == 12) {
            dirTxt = "W";
            dirTxtIndo = "Barat";
        } else if (mRange == 13 || mRange == 14) {
            dirTxt = "NW";
            dirTxtIndo = "Barat Laut";
        }

        int mRangePertokoan = (int) (mDirectionTo / (360f / 16f));

        String dirTxtPertokoan = "";
        String dirTxtIndoPertokoan = "";

        if (mRangePertokoan == 15 || mRangePertokoan == 0) {
            dirTxtPertokoan = "N";
            dirTxtIndoPertokoan = "Utara";
        } else if (mRangePertokoan == 1 || mRangePertokoan == 2) {
            dirTxtPertokoan = "NE";
            dirTxtIndoPertokoan = "Timur Laut";
        } else if (mRangePertokoan == 3 || mRangePertokoan == 4) {
            dirTxtPertokoan = "E";
            dirTxtIndoPertokoan = "Timur";
        } else if (mRangePertokoan == 5 || mRangePertokoan == 6) {
            dirTxtPertokoan = "SE";
            dirTxtIndoPertokoan = "Tenggara";
        } else if (mRangePertokoan == 7 || mRangePertokoan == 8) {
            dirTxtPertokoan = "S";
            dirTxtIndoPertokoan = "Selatan";
        } else if (mRangePertokoan == 9 || mRangePertokoan == 10) {
            dirTxtPertokoan = "SW";
            dirTxtIndoPertokoan = "Barat Daya";
        } else if (mRangePertokoan == 11 || mRangePertokoan == 12) {
            dirTxtPertokoan = "W";
            dirTxtIndoPertokoan = "Barat";
        } else if (mRangePertokoan == 13 || mRangePertokoan == 14) {
            dirTxtPertokoan = "NW";
            dirTxtIndoPertokoan = "Barat Laut";
        }

        textBearingRadian.setText(dirTxt);
        bearingtoPertokoan.setText(dirTxtPertokoan + " / " + dirTxtIndoPertokoan + " pada " + (int) mDirectionTo + ((char) 176));
        textDirection.setText("" + ((int) mAzimuth) + ((char) 176));
    }

    private World initArView() {

        final World mWorldCompass = new World(NavigationActivity.this);

        NavigationActivity.this.runOnUiThread(() -> {
            try {
                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                mWorldCompass.setGeoPosition(mWorldCompass.getLatitude(), mWorldCompass.getLongitude(), mWorldCompass.getAltitude());

                GeoObject geoObject = new GeoObject(1L);
                geoObject.setGeoPosition(mShop.getShopLatitude(), mShop.getShopLongitude());
                geoObject.setImageResource(R.mipmap.placeholder);
                geoObject.calculateDistanceMeters(geoObject);
                geoObject.setName(mShop.getShopName());

                mWorldCompass.addBeyondarObject(geoObject);

                mBeyondarFragmentCompass.setOnClickBeyondarObjectListener(arrayList -> {
                    if (arrayList.size() > 0) {
                        Alerter.create(NavigationActivity.this)
                                .setBackgroundColorRes(R.color.Primary_Light_Material_700)
                                .setIcon(R.drawable.ic_store_mall_directory_black_all_24dp)
                                .enableSwipeToDismiss()
                                .setTitle(arrayList.get(0).getName())
                                .setText(mShop.getShopName() + "\n\n" +
                                        "Buka Setiap Hari 08:00 - 21:00" + "\n\n" +
                                        "Jarak  : " + round(arrayList.get(0).getDistanceFromUser()) + " m")
                                .setTitleAppearance(R.style.AlerterTitleApperance)
                                .setTextAppearance(R.style.AlertTextAppearance)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf"))
                                .show();
                    }
                });
            } catch (NullPointerException n) {
                n.printStackTrace();
                Toast.makeText(NavigationActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });

        return mWorldCompass;
    }

    private double round(double value) {
        long factor = (long) Math.ceil(10);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void mapsDrawRoute() {
        try {
            if (mCurrentLocation != null) {
                mLatitude = mCurrentLocation.getLatitude();
                mLongitude = mCurrentLocation.getLongitude();
            }

            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            final double latitude = Objects.requireNonNull(mShop).getShopLatitude();
            final double longitude = mShop.getShopLongitude();
            final LatLng destination = new LatLng(latitude, longitude);
            final LatLng origin = new LatLng(mLatitude, mLongitude);

            DrawRouteMaps.getInstance(NavigationActivity.this)
                    .draw(origin, destination, mGoogleMap);

            DrawMarkerOriginDetail.getInstance(getApplicationContext())
                    .draw(mGoogleMap, mMarker, origin, R.drawable.ic_location_on_black_24dp);

            DrawMarkerDestinationDetail.getInstance(getApplicationContext())
                    .draw(mGoogleMap, destination, R.drawable.ic_013_placeholder, mShop.getShopName(), mShop.getShopAddress());

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NavigationActivity.this.runOnUiThread(() -> {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(origin)
                                .zoom(19f)
                                .tilt(90f)
                                .bearing((float) mAzimuth)
                                .build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
                    });
                    mHandler.postDelayed(this, 2000);
                }
            }, 2000);

        } catch (NullPointerException n) {
            n.printStackTrace();
            Toast.makeText(NavigationActivity.this, "Mendapatkan Lokasi", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifOnPlace() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            //Fungsi Notifikasi pada saat sampai pada LatLng tujuan
            String LatNotif = new DecimalFormat("##.####").format(mCurrentLocation.getLatitude());
            String LngNotif = new DecimalFormat("###.####").format(mCurrentLocation.getLongitude());

            String LatOriNotif = new DecimalFormat("##.####").format(Objects.requireNonNull(mShop).getShopLatitude());
            String LngOriNotif = new DecimalFormat("###.####").format(mShop.getShopLongitude());

            int mJaraktoInt = ((int) mJarak);
            int min = 5;

            if (LatOriNotif.equals(LatNotif) && LngOriNotif.equals(LngNotif) && mJaraktoInt <= min) {
                Alerter.create(this)
                        .setText("Anda Sudah Sampai di " + mShop.getShopName() + " Selamat Berbelanja")
                        .setBackgroundColorRes(R.color.Primary_Light_Material_700)
                        .setTextAppearance(com.inudev.ygbatikar.R.style.AlertTextAppearance)
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf"))
                        .show();
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void jarakcompass(final Location mCurrentLocation) {
        NavigationActivity.this.runOnUiThread(() -> {
            try {
                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                final int R = 6371;

                Double latDestination = Math.toRadians(Objects.requireNonNull(mShop).getShopLatitude());
                Double latOrigin = Math.toRadians(mCurrentLocation.getLatitude());
                Double lngDestination = Math.toRadians(mShop.getShopLongitude());
                Double lngOrigin = Math.toRadians(mCurrentLocation.getLongitude());

                Double latDistance = (latDestination - latOrigin);
                Double lngDistance = (lngDestination - lngOrigin);

                Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                        Math.cos(latOrigin) * Math.cos(latDestination) *
                                Math.sin(lngDistance / 2)
                                * Math.sin(lngDistance / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                mJarak = R * c * 1000;

                String jarakStr = new DecimalFormat("####").format(mJarak);
                txtJarak.setText(jarakStr + " m");
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
        });
    }

    private void updatelocation(Location mCurrentLocation) {
        if (FIXED.equals(mCurrentLocation.getProvider())) {
            textLng.setText(NA);
            textLng.setText(NA);
        }

        DecimalFormatSymbols dFS = new DecimalFormatSymbols();
        dFS.setDecimalSeparator('.');
        NumberFormat Numformat = new DecimalFormat("#0.000000", dFS);
        textLat.setText(Numformat.format(mCurrentLocation.getLatitude()));
        textLng.setText(Numformat.format(mCurrentLocation.getLongitude()));
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accurancy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD &&
                accurancy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(this, "Compass Sensor not detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        final Shop mShop = (Shop)
                Objects.requireNonNull(getIntent()
                        .getExtras())
                        .get("Shop");

        boolean accelOrMagnetic = false;

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mSmoothed = LowPassFilter.filter(sensorEvent.values, mGravity);
            mGravity[0] = mSmoothed[0];
            mGravity[1] = mSmoothed[1];
            mGravity[2] = mSmoothed[2];
            accelOrMagnetic = true;
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mSmoothed = LowPassFilter.filter(sensorEvent.values, mGeomagnetic);
            mGeomagnetic[0] = mSmoothed[0];
            mGeomagnetic[1] = mSmoothed[1];
            mGeomagnetic[2] = mSmoothed[2];
            accelOrMagnetic = true;
        }

        SensorManager.getRotationMatrix(mRotation, null, mGravity, mGeomagnetic);
        SensorManager.getOrientation(mRotation, mOrientation);
        mAzimuth = mOrientation[0];
        mAzimuth = Math.toDegrees(mAzimuth);

        //mTilt = mRotation[0];
        //mTilt = Math.toDegrees(mTilt);

        //if (mTilt < 0) {
        //     mTilt = mTilt + 90;
        //}

        if (mGeomagneticField != null) {
            mAzimuth += mGeomagneticField.getDeclination();
        }

        if (mAzimuth < 0) {
            mAzimuth += 360;
        }

        // Mencari nilai bearing
        Location destination_location = new Location("Destination");
        destination_location.setLatitude(Objects.requireNonNull(mShop).getShopLatitude());
        destination_location.setLongitude(mShop.getShopLongitude());

        Location origin_location = new Location("OriginLoc");
        origin_location.setLatitude(mCurrentLocation.getLatitude());
        origin_location.setLongitude(mCurrentLocation.getLongitude());

        mDirectionTo = origin_location.bearingTo(destination_location);

        if (mDirectionTo < 0) {
            mDirectionTo += 360;
        }

        mBearing = mDirectionTo - ((float) mAzimuth);

        if (mGeomagneticField != null) {
            mBearing += mGeomagneticField.getDeclination();
        }

        if (mBearing < 0) {
            mBearing += 360;
        }

        mArrowNavigationView.setmBearing(mBearing);
        //Log.d(TAG, "onSensorChanged: " + mBearing);

        if (accelOrMagnetic) {
            mArrowNavigationView.postInvalidate();
        }

        updateTextDirection(mAzimuth);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        assert mSensorManager != null;
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mSensorGravity, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorMagnetic, SensorManager.SENSOR_DELAY_GAME);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeyondarFragmentCompass.onResume();
        BeyondarLocationManager.enable();
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

            assert mLocationManager != null;
            String provider = mLocationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            try {
                Location gpsLocation = mLocationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    mCurrentLocation = gpsLocation;
                    onLocationChanged(mCurrentLocation);
                } else {
                    Location networkLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        mCurrentLocation = networkLocation;
                        onLocationChanged(mCurrentLocation);
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                Log.e(TAG, "onResume: ", npe);
            }

            mLocationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLocationChanged(Location mCurrentLocation) {
        try {
            if (mCurrentLocation != null) {
                updatelocation(mCurrentLocation);
                jarakcompass(mCurrentLocation);
                notifOnPlace();
                mapsDrawRoute();

                initArView();

                mGeomagneticField = new GeomagneticField(
                        (float) mCurrentLocation.getLatitude(),
                        (float) mCurrentLocation.getLongitude(),
                        (float) mCurrentLocation.getAltitude(),
                        System.currentTimeMillis());
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Log.e(TAG, "onLocationChanged: ", npe);
        }
    }

    @Override
    protected void onPause() {
        mBeyondarFragmentCompass.onPause();
        mLocationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this, mSensorGravity);
        mSensorManager.unregisterListener(this, mSensorMagnetic);
        mLocationManager.removeUpdates(this);
        BeyondarLocationManager.disable();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapFragment.onLowMemory();
        mBeyondarFragmentCompass.onLowMemory();
    }
}
