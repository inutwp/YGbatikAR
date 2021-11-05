package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aquery.AQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.inudev.ygbatikar.model.Shop;
import com.inudev.ygbatikar.model.ShopModel;
import com.inudev.ygbatikar.utils.ApiBuilder;
import com.inudev.ygbatikar.utils.ApiService;
import com.inudev.ygbatikar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Callback;
import retrofit2.Response;

public class CariSekitarkuActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "CariSekitarkuActivity";
    private static final int UPDATE_INTERVAL = 1000;
    private static final int DISPLACEMENT = 1;
    private int drawableIconDestination = R.drawable.ic_013_placeholder;
    private int drawableIconOrigin = R.drawable.ic_001_placeholder_2;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Marker mMarker;
    private Marker mMarkerOrigin;
    private SupportMapFragment mMapFragment;
    private AQuery aq;
    private List<String> jarakpertokoan = new ArrayList<>();
    public Location mCurrentLocation;
    double mLatitude = 0;
    double mLongitude = 0;
    int jarakToInt;
    int mRadius;
    AlertDialog mAlertDialog;
    CharSequence[] mValue = {"Maps Style Day", "Maps Style Night"};
    LatLng destination;
    LatLng origin;
    CardView RVInfoMarker;
    RelativeLayout RLCheckInternet;
    ImageView imageMarkerInfo, closeInfoMarker;
    TextView radiusText, operasionalMarkerInfo;
    SeekBar seekbarRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_sekitarku);
        aq = new AQuery(this);

        initChangeRadius();
        initMapsReady();
        initCheckInternet();

        closeInfoMarker = findViewById(R.id.close_infomarker_caripertokoan);
    }

    private void initChangeRadius() {
        radiusText = findViewById(R.id.radiusText);
        seekbarRadius = findViewById(R.id.seekBarRadius);
        seekbarRadius.setOnSeekBarChangeListener(this);
        seekbarRadius.setMax(10000);
        seekbarRadius.setProgress(100);
    }

    private void initMapsReady() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        CariSekitarkuActivity.this.runOnUiThread(() -> mMapFragment.getMapAsync(CariSekitarkuActivity.this));
    }

    private void initCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            checkInternetConnection(true);
        } else {
            checkInternetConnection(false);
        }
    }

    private void checkInternetConnection(boolean isConnected) {
        if (isConnected) {
            Log.i(TAG, "checkInternetConnection: " + true);
            initCheckGPS();
        } else {
            RLCheckInternet = findViewById(R.id.RL_check_internet_connection_cari_pertokoan);
            if (RLCheckInternet.getVisibility() == View.GONE) {
                RLCheckInternet.setVisibility(View.VISIBLE);
            } else {
                RLCheckInternet.setVisibility(View.GONE);
            }
            aq.id(R.id.image_checkInternet_cari_pertokoan).image(R.drawable.internetconnection);
            aq.id(R.id.TV_check_cari_pertokoan).text("Sepertinya koneksi anda terputus");
            aq.id(R.id.TV_check2_cari_pertokoan).text("Pastikan anda terhubung dengan koneksi internet dan coba lagi");
        }
    }

    private void initCheckGPS() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus) {
            Log.d(TAG, "initCheckGPS: Aktif " + true);
        } else {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            CheckGPSFragment checkGPSFragment = new CheckGPSFragment();
            checkGPSFragment.show(mFragmentManager, "checkgpsdialog");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    CariSekitarkuActivity.this, R.raw.style_maps_json
            ));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "cant find style. Error: ", e);
        }

        aq.id(R.id.changeMapsStyle).click(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CariSekitarkuActivity.this);
            mBuilder.setTitle("Pilih Style Maps");
            mBuilder.setSingleChoiceItems(mValue, -1, (dialogInterface, i) -> {
                switch (i) {
                    case 0:
                        try {
                            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                                    CariSekitarkuActivity.this, R.raw.style_maps_json
                            ));
                            if (!success) {
                                Log.e(TAG, "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e(TAG, "cant find style. Error: ", e);
                        }
                        break;
                    case 1:
                        try {
                            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                                    CariSekitarkuActivity.this, R.raw.style_maps_night_json
                            ));
                            if (!success) {
                                Log.e(TAG, "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e(TAG, "cant find style. Error: ", e);
                        }
                        break;
                }
                mAlertDialog.dismiss();
            });
            mAlertDialog = mBuilder.create();
            mAlertDialog.show();
        });

        initMaps();
    }

    private void initMaps() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void markerDraw() {
        mMap.setOnMyLocationButtonClickListener(() -> {
            locationManager = (LocationManager) CariSekitarkuActivity.this.getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (GpsStatus) {
                Log.d(TAG, "initCheckGPS: Aktif " + true);
                mMap.clear();
                aq.id(R.id.avload).show();
                aq.id(R.id.loading_text_cari_sekitarku).show();

                ApiService service = ApiBuilder.getClient().create(ApiService.class);
                retrofit2.Call<ShopModel> PertokoanModelCall = service.getPertokoanData();
                PertokoanModelCall.enqueue(new Callback<ShopModel>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                        CariSekitarkuActivity.this.runOnUiThread(() -> {
                            aq.id(R.id.avload).hide();
                            aq.id(R.id.loading_text_cari_sekitarku).hide();
                            try {
                                if (response.isSuccessful()) {
                                    List<Shop> listShop = Objects.requireNonNull(response.body()).getShops();
                                    for (final Shop mShop : listShop) {
                                        double mLatitudeDestination = mShop.getShopLatitude();
                                        double mLongitudeDestination = mShop.getShopLongitude();
                                        destination = new LatLng(mLatitudeDestination, mLongitudeDestination);

                                        final int R = 6371;

                                        Double latDestination = Math.toRadians(mLatitudeDestination);
                                        Double latOrigin = Math.toRadians(mLatitude);
                                        Double lngDestination = Math.toRadians(mLongitudeDestination);
                                        Double lngOrigin = Math.toRadians(mLongitude);

                                        Double latDistance = (latDestination - latOrigin);
                                        Double lngDistance = (lngDestination - lngOrigin);

                                        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                                                Math.cos(latOrigin) * Math.cos(latDestination) *
                                                        Math.sin(lngDistance / 2)
                                                        * Math.sin(lngDistance / 2);
                                        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                        Double Jarak = R * c * 1000;
                                        String jaraktoString = new DecimalFormat("####").format(Jarak);

                                        jarakToInt = Integer.parseInt(jaraktoString);
                                        if (jarakToInt <= mRadius) {

                                            Drawable drawIconDestination = ContextCompat.getDrawable(CariSekitarkuActivity.this, drawableIconDestination);
                                            Drawable drawIconOrigin = ContextCompat.getDrawable(CariSekitarkuActivity.this, drawableIconOrigin);

                                            assert drawIconDestination != null;
                                            BitmapDescriptor markerIconDestination = getMarkerIconFromDrawable(drawIconDestination);
                                            assert drawIconOrigin != null;
                                            BitmapDescriptor markerIconOrigin = getMarkerIconFromDrawable(drawIconOrigin);

                                            mMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(destination)
                                                    .anchor(0.5f, 0.5f)
                                                    .title(mShop.getShopName())
                                                    .icon(markerIconDestination));
                                            mMarker.setTag(mShop);

                                            origin = new LatLng(mLatitude, mLongitude);
                                            mMarkerOrigin = mMap.addMarker(new MarkerOptions()
                                                    .position(origin)
                                                    .anchor(0.5f, 0.5f)
                                                    .title("Lokasi Mu")
                                                    .icon(markerIconOrigin));
                                            mMarkerOrigin.showInfoWindow();

                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13));

                                            mMap.setOnMarkerClickListener(marker -> {
                                                try {
                                                    final Shop sharedShop = ((Shop) marker.getTag());
                                                    assert sharedShop != null;

                                                    initViewShowInfoMarker(sharedShop);
                                                    initViewCloseInfoMarker();
                                                    initViewOperasionalInfoMarker(sharedShop);

                                                    int getJarak = jarakToInt;

                                                    if (getJarak < 1000) {
                                                        aq.id(com.inudev.ygbatikar.R.id.jarak_infomarker_caripertokoan).text(getJarak + " m");
                                                    } else {
                                                        int jarakToKm = getJarak / 1000;
                                                        aq.id(com.inudev.ygbatikar.R.id.jarak_infomarker_caripertokoan).text(jarakToKm + " Km");
                                                    }

                                                    aq.id(com.inudev.ygbatikar.R.id.ntoko_infomarker_caripertokoan).text(sharedShop.getShopName());
                                                    aq.id(com.inudev.ygbatikar.R.id.alamat_infomarker_caripertokoan).text(sharedShop.getShopAddress());

                                                    imageMarkerInfo = findViewById(com.inudev.ygbatikar.R.id.image_infomarker_caripertokoan);
                                                    Glide.with(CariSekitarkuActivity.this)
                                                            .load(sharedShop.getShopPictureProfile())
                                                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                                            .apply(RequestOptions.placeholderOf(com.inudev.ygbatikar.R.drawable.actionbar_img))
                                                            .into(imageMarkerInfo);

                                                    mMarker.showInfoWindow();
                                                } catch (NullPointerException n) {
                                                    n.printStackTrace();
                                                    Log.e(TAG, "NullPointerException :", n);
                                                }
                                                return false;
                                            });
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                aq.toast("Gagal Meload ke Maps");
                            }

                            if (ActivityCompat.checkSelfPermission(CariSekitarkuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CariSekitarkuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            assert mMap != null;
                            mMap.getUiSettings().setAllGesturesEnabled(true);
                            mMap.getUiSettings().setTiltGesturesEnabled(true);
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        try {
                            CariSekitarkuActivity.this.runOnUiThread(() -> {
                                aq.id(R.id.avload).hide();
                                aq.id(R.id.loading_text_cari_sekitarku).hide();
                            });
                        } catch (NullPointerException n) {
                            Log.e(TAG, "onFailure: ", n);
                        }
                    }
                });
            } else {
                FragmentManager mFragmentManager = getSupportFragmentManager();
                CheckGPSFragment checkGPSFragment = new CheckGPSFragment();
                checkGPSFragment.show(mFragmentManager, "checkgpsdialog");
            }
            return false;
        });
    }

    @SuppressLint("SetTextI18n")
    private void initViewOperasionalInfoMarker(Shop sharedShop) {
        operasionalMarkerInfo = findViewById(R.id.operasional_infomarker_caripertokoan);
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String jambuka = sharedShop.getShopOpenTime();
        String subJambuka = jambuka.substring(0, 2);

        String jamtutup = sharedShop.getShopCloseTime();
        String subJamtutup = jamtutup.substring(0, 2);

        int JamBuka = Integer.parseInt(subJambuka);
        int JamTutup = Integer.parseInt(subJamtutup);

        int JamHampirBuka = JamBuka - 1;
        int JamHampirTutup = JamTutup - 1;

        if (hour == JamHampirBuka) {
            operasionalMarkerInfo.setText("Hampir Buka" + " : " + sharedShop.getShopOpenTime() + " - " + sharedShop.getShopCloseTime());
            operasionalMarkerInfo.setTextColor(Color.WHITE);
            operasionalMarkerInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_hampir_buka_title));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            operasionalMarkerInfo.setText("Buka" + " : " + sharedShop.getShopOpenTime() + " - " + sharedShop.getShopCloseTime());
            operasionalMarkerInfo.setTextColor(Color.WHITE);
            operasionalMarkerInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_buka_title));
        } else if (hour == JamHampirTutup) {
            operasionalMarkerInfo.setText("Hampir Tutup" + " : " + sharedShop.getShopOpenTime() + " - " + sharedShop.getShopCloseTime());
            operasionalMarkerInfo.setTextColor(Color.WHITE);
            operasionalMarkerInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_hampir_tutup_title));
        } else {
            operasionalMarkerInfo.setText("Tutup" + " : " + sharedShop.getShopOpenTime() + " - " + sharedShop.getShopCloseTime());
            operasionalMarkerInfo.setTextColor(Color.WHITE);
            operasionalMarkerInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_tutup_title));
        }
    }

    private void initViewCloseInfoMarker() {
        closeInfoMarker.setOnClickListener(view -> {
            if (RVInfoMarker.getVisibility() == View.VISIBLE) {
                RVInfoMarker.setVisibility(View.GONE);
                closeInfoMarker.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            } else if (RVInfoMarker.getVisibility() == View.GONE) {
                RVInfoMarker.setVisibility(View.VISIBLE);
                closeInfoMarker.setImageResource(R.drawable.ic_close_black_24dp);
            }
        });
    }

    private void initViewShowInfoMarker(Shop sharedShop) {
        RVInfoMarker = findViewById(R.id.CV_infomarker_carisekitarku);
        if (RVInfoMarker.getVisibility() == View.GONE && closeInfoMarker.getVisibility() == View.GONE) {
            RVInfoMarker.setVisibility(View.VISIBLE);
            closeInfoMarker.setVisibility(View.VISIBLE);
        }
        RVInfoMarker.setOnClickListener(view -> {
            Intent intent = new Intent(CariSekitarkuActivity.this, InfoPertokoanActivity.class);
            intent.putExtra("Shop", sharedShop);
            aq.open(intent);
        });
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == seekbarRadius) {
            mRadius = i;
            if (i < 1000) {
                radiusText.setText("Pertokoan pada Radius " + i + " m");
            } else {
                int toKm = i / 1000;
                radiusText.setText("Pertokoan pada Radius " + toKm + " Km");
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
    protected void onResume() {
        super.onResume();
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

            assert locationManager != null;
            String provider = locationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    mCurrentLocation = location;
                    onLocationChanged(mCurrentLocation);
                } else {
                    Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        mCurrentLocation = networkLocation;
                        onLocationChanged(mCurrentLocation);
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                Log.e(TAG, "onResume: ", npe);
            }

            locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL, DISPLACEMENT, this);
        } catch (Exception e) {
            e.printStackTrace();
            aq.toast("Mendapatkan Lokasi");
        }

    }

    public void onLocationChanged(Location mCurrentLocation) {
        try {
            if (mCurrentLocation != null) {
                mLatitude = mCurrentLocation.getLatitude();
                mLongitude = mCurrentLocation.getLongitude();

                markerDraw();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Log.e(TAG, "onLocationChanged: ", npe);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onPause() {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        locationManager.removeUpdates(this);
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

    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
