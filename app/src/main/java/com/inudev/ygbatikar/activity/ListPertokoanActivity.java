package com.inudev.ygbatikar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aquery.AQuery;
import com.inudev.ygbatikar.model.Shop;
import com.inudev.ygbatikar.model.ShopModel;
import com.inudev.ygbatikar.utils.ApiBuilder;
import com.inudev.ygbatikar.utils.ApiService;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.adapter.InfoMarkerAdapter;
import com.inudev.ygbatikar.adapter.ListPertokoanAdapter;
import com.inudev.ygbatikar.holder.InfoMarkerHolder;
import com.inudev.ygbatikar.holder.ListPertokoanHolder;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ListPertokoanActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final String FIXED = "FIXED";
    private static final String TAG = "ListPertokoanActivity";
    private static final int UPDATE_INTERVAL = 1000;
    private static final int DISPLACEMENT = 1;
    private int mDrawableIcon = R.drawable.ic_013_placeholder;
    private GoogleMap mMap;
    private Marker mMarker;
    private SupportMapFragment mMapFragment;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewListPertokoan;
    private AlertDialog mAlertDialog;
    private LocationManager mLocationManager;
    private LatLng mDestination;
    private Location mCurrentLocation;
    private AQuery aq;
    public double mLatitude = 0;
    public double mLongitude = 0;
    public CoordinatorLayout mCoordinatorLayout;
    public LatLng mOrigin;
    CharSequence[] mValues = {"Jam Buka", "Nama Pertokoan", "Alamat"};
    ImageView mExpandButton;
    RelativeLayout viewCheckConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pertokoan);
        aq = new AQuery(this);

        initBottomSheet();
        initRecycleviewListPertokoan();
        initConnectionCheck();
        initMapsReady();
        //initTapSort();
    }

    private void initConnectionCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            actionCheck(true);
        } else {
            actionCheck(false);
        }
    }

    private void actionCheck(boolean isConnected) {
        if (isConnected) {
            aq.id(R.id.avload).show();
            aq.id(R.id.loading_text_list_pertokoan).show();
        } else {
            aq.id(R.id.avload).show();
            aq.id(R.id.loading_text_list_pertokoan).show();
            viewCheckConnection = findViewById(R.id.listPertokoan_RV_check_connection);
            if (viewCheckConnection.getVisibility() == View.GONE) {
                viewCheckConnection.setVisibility(View.VISIBLE);
            } else {
                viewCheckConnection.setVisibility(View.GONE);
            }
            aq.id(R.id.image_checkInternet_list_pertokoan).image(R.drawable.internetconnection);
            aq.id(R.id.TV_check_list_pertokoan).text("Sepertinya koneksi anda terputus");
            aq.id(R.id.TV_check2_list_pertokoan).text("Pastikan anda terhubung dengan koneksi internet dan coba lagi");
        }
    }

    private void initTapSort() {
        new MaterialTapTargetPrompt.Builder(ListPertokoanActivity.this)
                .setTarget(findViewById(R.id.sortPertokoan))
                .setPrimaryText("Sorting")
                .setSecondaryText("Sorting Berdasarkan Nama, Alamat, Dan Jam Buka")
                .setBackgroundColourFromRes(R.color.Primary_Light_Material_900)
                .setIcon(R.drawable.ic_sort_black_24dp)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .show();
    }

    private void initMapsReady() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_pertokoan_maps);

        ListPertokoanActivity.this.runOnUiThread(() -> mMapFragment.getMapAsync(ListPertokoanActivity.this));
    }

    private void initRecycleviewListPertokoan() {
        mRecyclerView = findViewById(R.id.recycle_view_list_pertokoan);

        ApiService service = ApiBuilder.getClient().create(ApiService.class);
        retrofit2.Call<ShopModel> ShopModelCall = service.getPertokoanData();
        ShopModelCall.enqueue(new Callback<ShopModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                if (response.isSuccessful()) {
                    try {
                        ListPertokoanActivity.this.runOnUiThread(() -> {
                            List<Shop> mShop = Objects.requireNonNull(response.body()).getShops();
                            aq.id(R.id.avload).hide();
                            aq.id(R.id.loading_text_list_pertokoan).hide();
                            final ListPertokoanAdapter listPertokoanAdapter = new ListPertokoanAdapter(mShop) {
                                @Override
                                protected void bindHolder(ListPertokoanHolder holder, final Shop mShop) {
                                    holder.bind(mShop);
                                    holder.itemView.setOnClickListener(view -> {
                                        Intent intent_list = new Intent(view.getContext(), InfoPertokoanActivity.class);
                                        intent_list.putExtra("Shop", mShop);
                                        startActivity(intent_list);
                                    });
                                }
                            };

                            ListPertokoanActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    mRecyclerView.setAdapter(listPertokoanAdapter);
                                    initAlertDialog();
                                }

                                private void initAlertDialog() {
                                    aq.id(R.id.sortPertokoan).click(view -> {
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ListPertokoanActivity.this);
                                        mBuilder.setTitle("Urutkan List");
                                        mBuilder.setSingleChoiceItems(mValues, -1, (dialogInterface, item) -> {
                                            Log.d(TAG, "onClick: " + item);
                                            switch (item) {
                                                case 0:
                                                    mShop.remove(mShop);
                                                    listPertokoanAdapter.notifyDataSetChanged();
                                                    Collections.sort(mShop, (filter1a, filter1b) -> filter1a.getShopOpenTime().compareTo(filter1b.getShopOpenTime()));
                                                    Snackbar.make(findViewById(R.id.coordinatorListPertokoan), "Memilih Pengurutan Berdasarkan Jambuka", Snackbar.LENGTH_SHORT).show();
                                                    break;
                                                case 1:
                                                    mShop.remove(mShop);
                                                    listPertokoanAdapter.notifyDataSetChanged();
                                                    Collections.sort(mShop, (filter2a, filter2b) -> filter2a.getShopName().compareTo(filter2b.getShopName()));
                                                    Snackbar.make(findViewById(R.id.coordinatorListPertokoan), "Memilih Pengurutan Berdasarkan Nama", Snackbar.LENGTH_SHORT).show();
                                                    break;
                                                case 2:
                                                    mShop.remove(mShop);
                                                    listPertokoanAdapter.notifyDataSetChanged();
                                                    Collections.sort(mShop, (filter3a, filter3b) -> filter3a.getShopAddress().compareTo(filter3b.getShopAddress()));
                                                    Snackbar.make(findViewById(R.id.coordinatorListPertokoan), "Memilih Pengurutan Berdasarkan Alamat", Snackbar.LENGTH_SHORT).show();
                                                    break;
                                            }
                                            mAlertDialog.dismiss();
                                        });
                                        mAlertDialog = mBuilder.create();
                                        mAlertDialog.show();
                                    });
                                }
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        aq.toast(e.getMessage());
                        Log.e(TAG, "onFailure: ", e);
                    }
                } else {
                    ListPertokoanActivity.this.runOnUiThread(() -> {
                        aq.id(R.id.avload).hide();
                        aq.id(R.id.loading_text_list_pertokoan).hide();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                try {
                    ListPertokoanActivity.this.runOnUiThread(() -> {
                        aq.id(R.id.avload).hide();
                        aq.id(R.id.loading_text_list_pertokoan).hide();
                    });
                } catch (NullPointerException n) {
                    Log.e(TAG, "onProgressDIalog :", n);
                }
            }
        });

    }

    private void initBottomSheet() {

        mExpandButton = findViewById(R.id.expand);
        mCoordinatorLayout = findViewById(R.id.coordinatorListPertokoan);
        View bottomSheet = mCoordinatorLayout.findViewById(R.id.bottom_sheet_list_pertokoan);

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mExpandButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mExpandButton.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mExpandButton.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
        }

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    ListPertokoanActivity.this, R.raw.style_maps_json
            ));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "cant find style. Error: ", e);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        initMapsView();
    }

    private void initMapsView() {
        if (mMap != null) {
            ApiService apiService = ApiBuilder.getClient().create(ApiService.class);
            retrofit2.Call<ShopModel> ShopModelCall = apiService.getPertokoanData();
            ShopModelCall.enqueue(new Callback<ShopModel>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                    ListPertokoanActivity.this.runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            List<Shop> listShop = Objects.requireNonNull(response.body()).getShops();
                            aq.id(R.id.avload).hide();
                            aq.id(R.id.loading_text_list_pertokoan).hide();
                            try {
                                for (final Shop mShop : listShop) {
                                    double latitude = mShop.getShopLatitude();
                                    double longitude = mShop.getShopLongitude();
                                    mDestination = new LatLng(latitude, longitude);

                                    Drawable drawable = ContextCompat.getDrawable(ListPertokoanActivity.this, mDrawableIcon);
                                    assert drawable != null;
                                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(drawable);

                                    mMarker = mMap.addMarker(new MarkerOptions()
                                            .position(mDestination)
                                            .title(mShop.getShopName())
                                            .icon(markerIcon));

                                    mMarker.setTag(mShop);

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDestination, 13));
                                    mMap.getUiSettings().setAllGesturesEnabled(true);
                                    mMap.getUiSettings().setMapToolbarEnabled(false);

                                    mMap.setOnMarkerClickListener(marker -> {

                                        final Shop markerShop = ((Shop) marker.getTag());

                                        mRecyclerViewListPertokoan = findViewById(R.id.recycleview_maps_list_pertokoan);
                                        aq.id(R.id.avload).show();
                                        aq.id(R.id.loading_text_list_pertokoan).show();

                                        ApiService apiService1 = ApiBuilder.getClient().create(ApiService.class);
                                        retrofit2.Call<ShopModel> PertokoanModelCall1 = apiService1.getPertokoanData();
                                        PertokoanModelCall1.enqueue(new Callback<ShopModel>() {
                                            @Override
                                            public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                                                try {
                                                    ListPertokoanActivity.this.runOnUiThread(() -> {
                                                        if (response.isSuccessful()) {
                                                            List<Shop> listShop = Objects.requireNonNull(response.body()).getShops();
                                                            aq.id(R.id.avload).hide();
                                                            aq.id(R.id.loading_text_list_pertokoan).hide();
                                                            InfoMarkerAdapter infoMarkerAdapter = new InfoMarkerAdapter(listShop) {
                                                                @Override
                                                                protected void bindHolder(InfoMarkerHolder holder, Shop mShop) {
                                                                    holder.bind(mShop);
                                                                    holder.itemView.setOnClickListener(view -> {
                                                                        Intent intentInfoMarker = new Intent(ListPertokoanActivity.this, InfoPertokoanActivity.class);
                                                                        intentInfoMarker.putExtra("Shop", markerShop);
                                                                        startActivity(intentInfoMarker);
                                                                    });
                                                                }
                                                            };

                                                            ListPertokoanActivity.this.runOnUiThread(() -> {
                                                                mRecyclerViewListPertokoan.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                                                mRecyclerViewListPertokoan.setAdapter(infoMarkerAdapter);
                                                                infoMarkerAdapter.notifyDataSetChanged();
                                                                infoMarkerAdapter.getFilter().filter(Objects.requireNonNull(markerShop).getShopName());
                                                            });
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                                                Log.e(TAG, "onFailure2: ", t);
                                                try {
                                                    ListPertokoanActivity.this.runOnUiThread(() -> {
                                                        aq.id(R.id.avload).hide();
                                                        aq.id(R.id.loading_text_list_pertokoan).hide();
                                                    });
                                                } catch (NullPointerException n) {
                                                    Log.e(TAG, "onFailure: ", n);
                                                }
                                            }
                                        });

                                        marker.showInfoWindow();
                                        return false;
                                    });
                                }
                            } catch (Exception e) {
                                aq.toast(e.getMessage());
                            }
                        } else {
                            ListPertokoanActivity.this.runOnUiThread(() -> {
                                aq.id(R.id.avload).hide();
                                aq.id(R.id.loading_text_list_pertokoan).hide();
                            });
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                    try {
                        ListPertokoanActivity.this.runOnUiThread(() -> {
                            aq.id(R.id.avload).hide();
                            aq.id(R.id.loading_text_list_pertokoan).hide();
                        });
                    } catch (NullPointerException n) {
                        Log.e(TAG, "onFailure: ", n);
                    }
                }
            });
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable circleDraw) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(circleDraw.getIntrinsicWidth(), circleDraw.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        circleDraw.setBounds(0, 0, circleDraw.getIntrinsicWidth(), circleDraw.getIntrinsicHeight());
        circleDraw.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mCurrentLocation = location;

            if (mCurrentLocation != null) {
                mLatitude = mCurrentLocation.getLatitude();
                mLongitude = mCurrentLocation.getLongitude();

                mOrigin = new LatLng(mLatitude, mLongitude);

                Log.d(TAG, "onLocationChanged: " + mLatitude + " " + mLongitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
            aq.toast("Update Lokasi");
        }
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
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

            String provider = mLocationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                mCurrentLocation = location;
            } else {
                Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (networkLocation != null) {
                    mCurrentLocation = networkLocation;
                } else {
                    mCurrentLocation = new Location(FIXED);
                    mCurrentLocation.setLatitude(-7.764167);
                    mCurrentLocation.setLongitude(110.359344);
                }
                onLocationChanged(mCurrentLocation);
            }

            mLocationManager.requestLocationUpdates(provider, UPDATE_INTERVAL, DISPLACEMENT, this);
        } catch (Exception e) {
            e.printStackTrace();
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
}
