package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aquery.AQuery;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.inudev.ygbatikar.model.Shop;
import com.inudev.ygbatikar.model.ShopModel;
import com.inudev.ygbatikar.utils.ApiBuilder;
import com.inudev.ygbatikar.utils.ApiService;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.adapter.ArInfoAdapter;
import com.inudev.ygbatikar.holder.ArInfoHolder;

import java.util.List;
import java.util.Objects;

import retrofit2.Callback;
import retrofit2.Response;

public class ArActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "ArActivity";
    public static final int PERMISSION_REQUEST_CAMERA = 1;
    RecyclerView mRecyclerView;
    BeyondarFragmentSupport mBeyoundFragment;
    SeekBar mSeekbarMaxDistance, mSeekbarDistanceFactor;
    World mWorld;
    RadarView radarView;
    RadarWorldPlugin mRadarWorldPlugin;
    RelativeLayout RLCheckConnection, RLSeekBar;
    ImageView imageCheckConnection;
    TextView mTextViewMaxDistance, mTextDistanceFactor, textCheckone, textChecktwo;
    private AQuery aq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar);
        aq = new AQuery(this);

        getCameraPermis();
        initARView();
        initRecycleView();
        initSeekBar();
        initARLocation();
        initConnectionCHeck();
    }

    private void initConnectionCHeck() {
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
            aq.id(R.id.loading_text_ar_view).show();
            Log.i(TAG, "actionCheck: " + true);
            initCheckGPS();
        } else {
            aq.id(R.id.avload).show();
            aq.id(R.id.loading_text_ar_view).show();
            RLCheckConnection = findViewById(R.id.RL_check_internet_connection_ar);
            RLSeekBar = findViewById(R.id.RL_seekbar_ar);
            imageCheckConnection = findViewById(R.id.image_checkInternet_ar);
            textCheckone = findViewById(R.id.TV_check_ar);
            textChecktwo = findViewById(R.id.TV_check2_ar);
            if (RLCheckConnection.getVisibility() == View.GONE && RLSeekBar.getVisibility() == View.VISIBLE) {
                RLCheckConnection.setVisibility(View.VISIBLE);
                RLSeekBar.setVisibility(View.GONE);
            } else {
                RLCheckConnection.setVisibility(View.GONE);
                RLSeekBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initCheckGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus) {
            Log.d(TAG, "initCheckGPS: " + "Aktif");
        } else {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            CheckGPSFragment checkGPSFragment = new CheckGPSFragment();
            checkGPSFragment.show(mFragmentManager, "checkgpsdialog");
        }
    }

    private void getCameraPermis() {
        if (ContextCompat.checkSelfPermission(ArActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ArActivity.this,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG, "getCameraPermis: true");
            } else {
                ActivityCompat.requestPermissions(ArActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    private void initRecycleView() {
        mRecyclerView = findViewById(R.id.rv_ar_info);
    }

    private void initARView() {
        mBeyoundFragment = (BeyondarFragmentSupport) getSupportFragmentManager()
                .findFragmentById(R.id.Arygbatik);

        radarView = findViewById(R.id.radarview);

        mRadarWorldPlugin = new RadarWorldPlugin(this);
        if (radarView != null) {
            mRadarWorldPlugin.setRadarView(radarView);
        }
        mRadarWorldPlugin.setMaxDistance(1000);

        mWorld = initArWorld();

        mBeyoundFragment.setWorld(mWorld);

        mWorld.addPlugin(mRadarWorldPlugin);
    }

    private void initSeekBar() {
        mSeekbarMaxDistance = findViewById(R.id.seekBarMaxDistance);
        mSeekbarDistanceFactor = findViewById(R.id.seekDistanceFactor);

        mTextViewMaxDistance = findViewById(R.id.textMaxDistance);
        mTextDistanceFactor = findViewById(R.id.textDistanceFactor);

        mSeekbarMaxDistance.setOnSeekBarChangeListener(this);
        mSeekbarDistanceFactor.setOnSeekBarChangeListener(this);

        mSeekbarMaxDistance.setMax(10000);
        mSeekbarDistanceFactor.setMax(1000);

        mSeekbarMaxDistance.setProgress(100);
    }

    private void initARLocation() {
        BeyondarLocationManager.addWorldLocationUpdate(mWorld);
        BeyondarLocationManager.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
    }

    private World initArWorld() {

        final World mWorld = new World(this);

        ApiService service = ApiBuilder.getClient().create(ApiService.class);
        retrofit2.Call<ShopModel> PertokoanModelCall = service.getPertokoanData();
        PertokoanModelCall.enqueue(new Callback<ShopModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                ArActivity.this.runOnUiThread(() -> {
                    if (response.isSuccessful()){
                        List<Shop> shops = Objects.requireNonNull(response.body()).getShops();
                        aq.id(R.id.avload).hide();
                        aq.id(R.id.loading_text_ar_view).hide();
                        try {
                            for (final Shop mShop : shops) {
                                double latitude = mShop.getShopLatitude();
                                double longitude = mShop.getShopLongitude();

                                mWorld.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude(), mWorld.getAltitude());

                                final GeoObject geoObject = new GeoObject(1L);
                                geoObject.setGeoPosition(latitude, longitude);
                                geoObject.setImageResource(R.mipmap.placeholder);
                                geoObject.calculateDistanceMeters(geoObject);
                                geoObject.setName(mShop.getShopName());

                                mWorld.addBeyondarObject(geoObject);

                                mBeyoundFragment.setOnClickBeyondarObjectListener(arrayList -> {
                                    if (arrayList.size() > 0) {
                                        aq.id(R.id.avload).show();
                                        aq.id(R.id.loading_text_ar_view).show();

                                        ApiService apiService = ApiBuilder.getClient().create(ApiService.class);
                                        retrofit2.Call<ShopModel> PertokoanModelCall1 = apiService.getPertokoanData();
                                        PertokoanModelCall1.enqueue(new Callback<ShopModel>() {
                                            @Override
                                            public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                                                ArActivity.this.runOnUiThread(() -> {
                                                    if (response.isSuccessful()){
                                                        aq.id(R.id.avload).hide();
                                                        aq.id(R.id.loading_text_ar_view).hide();
                                                        List<Shop> shops = Objects.requireNonNull(response.body()).getShops();
                                                        final ArInfoAdapter arInfoAdapter = new ArInfoAdapter(shops) {
                                                            @Override
                                                            protected void bindHolder(ArInfoHolder holder, final Shop mShop) {
                                                                holder.bind(mShop);
                                                                holder.itemView.setOnClickListener(view -> {
                                                                    Intent intent = new Intent(view.getContext(),
                                                                            InfoPertokoanActivity.class);
                                                                    intent.putExtra("Shop", mShop);
                                                                    startActivity(intent);
                                                                });
                                                            }
                                                        };

                                                        ArActivity.this.runOnUiThread(() -> {
                                                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                                            mRecyclerView.setAdapter(arInfoAdapter);
                                                            arInfoAdapter.notifyDataSetChanged();
                                                            arInfoAdapter.getFilter().filter(arrayList.get(0).getName());
                                                        });
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                                                Log.e(TAG, "onFailure2: ", t);
                                                try {
                                                    ArActivity.this.runOnUiThread(() -> {
                                                        aq.id(R.id.avload).hide();
                                                        aq.id(R.id.loading_text_ar_view).hide();
                                                    });
                                                } catch (NullPointerException n) {
                                                    Log.e(TAG, "onFailure: ", n);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            aq.toast("Koneksi Internet Bermasalah!");
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                try {
                    ArActivity.this.runOnUiThread(() -> aq.id(R.id.avload).hide());
                } catch (NullPointerException n) {
                    Log.e(TAG, "onFailure: ", n);
                }
            }
        });

        return mWorld;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (mRadarWorldPlugin == null) {
            return;
        }
        if (seekBar == mSeekbarMaxDistance) {
            if (i < 1000) {
                mTextViewMaxDistance.setText("Jarak Max : " + i + " m");
            } else {
                int toKm = i / 1000;
                mTextViewMaxDistance.setText("Jarak Max : " + toKm + " Km");
            }
            mBeyoundFragment.setMaxDistanceToRender(i);
            mRadarWorldPlugin.setMaxDistance(i);
        } else if (seekBar == mSeekbarDistanceFactor) {
            if (i < 1000) {
                mTextDistanceFactor.setText("Zoom : " + i);
            } else {
                int toKmFactor = i / 1000;
                mTextDistanceFactor.setText("Zoom : " + toKmFactor);
            }
            mBeyoundFragment.setDistanceFactor(i);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initARLocation();
        mBeyoundFragment.onResume();
        BeyondarLocationManager.enable();
    }

    @Override
    protected void onPause() {
        mBeyoundFragment.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mBeyoundFragment.onPause();
        BeyondarLocationManager.disable();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBeyoundFragment.onLowMemory();
    }

    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
