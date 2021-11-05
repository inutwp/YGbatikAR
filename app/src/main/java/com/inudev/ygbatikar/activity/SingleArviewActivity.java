package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aquery.AQuery;
import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.model.Shop;

import java.util.Calendar;
import java.util.Objects;

public class SingleArviewActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener {

    public static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final String TAG = "SingleArviewActivity";
    private ProgressDialog mProgressDialog;
    private AQuery aq;

    BeyondarFragmentSupport mBeyoundFragment;
    SeekBar mSeekbarMaxDistance, mSeekbarFactorDistance;
    World mWorld;
    RadarView radarView;
    RadarWorldPlugin mRadarWorldPlugin;
    TextView mTextViewMaxDistance, mTextViewFactorDistance, showHide, mOperasional, mJarakInfo;
    CardView mCardViewInfomarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_arview);
        aq = new AQuery(this);

        initSingleARView();
        initSeekBar();
        initCameraPermission();
        initArLocation();
        initShowHideSeekbar();
        initInfoSingleAR();
    }

    @SuppressLint("SetTextI18n")
    private void initInfoSingleAR() {
        final Shop mShop = (Shop)
                Objects.requireNonNull(getIntent()
                        .getExtras())
                        .get("Shop");

        mOperasional = findViewById(R.id.notif_operasional_info_single_ar);

        aq.id(R.id.ntoko_single_ar_info).text(Objects.requireNonNull(mShop).getShopName());
        aq.id(R.id.waktu_operasional_single_ar).text("Setiap Hari" + " " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());
//        if (mShop.getShopPictureProfile().equals("-")){
//            aq.id(R.id.image_info_single_ar).image(R.drawable.actionbar_img);
//        }
//        aq.id(R.id.image_info_single_ar).image(mShop.getShopPictureProfile());

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
            mOperasional.setText("Hampir Buka");
            mOperasional.setTextColor(Color.WHITE);
            mOperasional.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_hampir_buka_title));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            mOperasional.setText("Buka");
            mOperasional.setTextColor(Color.WHITE);
            mOperasional.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_buka_title));
        } else if (hour == JamHampirTutup) {
            mOperasional.setText("Hampir Tutup");
            mOperasional.setTextColor(Color.WHITE);
            mOperasional.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_hampir_tutup_title));
        } else {
            mOperasional.setText("Tutup");
            mOperasional.setTextColor(Color.WHITE);
            mOperasional.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_tutup_title));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initShowHideSeekbar() {
        showHide = findViewById(R.id.showHIde);
        showHide.setOnClickListener(view -> {
            if (mTextViewMaxDistance.getVisibility() == View.VISIBLE && mTextViewFactorDistance.getVisibility() == View.VISIBLE && mSeekbarMaxDistance.getVisibility() == View.VISIBLE && mSeekbarFactorDistance.getVisibility() == View.VISIBLE) {
                mTextViewMaxDistance.setVisibility(View.GONE);
                mTextViewFactorDistance.setVisibility(View.GONE);
                mSeekbarMaxDistance.setVisibility(View.GONE);
                mSeekbarFactorDistance.setVisibility(View.GONE);
                showHide.setText("SHOW");
            } else {
                mTextViewMaxDistance.setVisibility(View.VISIBLE);
                mTextViewFactorDistance.setVisibility(View.VISIBLE);
                mSeekbarMaxDistance.setVisibility(View.VISIBLE);
                mSeekbarFactorDistance.setVisibility(View.VISIBLE);
                showHide.setText("HIDE");
            }
        });
    }

    private void initSingleARView() {
        mBeyoundFragment = (BeyondarFragmentSupport) getSupportFragmentManager()
                .findFragmentById(R.id.singleAR);

        radarView = findViewById(R.id.singleRadarView);

        mRadarWorldPlugin = new RadarWorldPlugin(this);
        if (radarView != null) {
            mRadarWorldPlugin.setRadarView(radarView);
        }
        mRadarWorldPlugin.setMaxDistance(1000);

        mWorld = initSingleARworld();

        mBeyoundFragment.setWorld(mWorld);

        mWorld.addPlugin(mRadarWorldPlugin);
    }

    private void initSeekBar() {
        mSeekbarMaxDistance = findViewById(R.id.singleSeekBarDistance);
        mSeekbarFactorDistance = findViewById(R.id.singleseekDistanceFactor);
        mTextViewMaxDistance = findViewById(R.id.singleMaxDIstance);
        mTextViewFactorDistance = findViewById(R.id.singletextDistanceFactor);

        mSeekbarMaxDistance.setOnSeekBarChangeListener(this);
        mSeekbarFactorDistance.setOnSeekBarChangeListener(this);

        mSeekbarMaxDistance.setMax(10000);
        mSeekbarFactorDistance.setMax(1000);

        mSeekbarMaxDistance.setProgress(100);
    }

    private void initArLocation() {
        BeyondarLocationManager.addWorldLocationUpdate(mWorld);
        BeyondarLocationManager.setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
    }

    private void initCameraPermission() {
        if (ContextCompat.checkSelfPermission(SingleArviewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SingleArviewActivity.this,
                    android.Manifest.permission.CAMERA)) {
                Log.d(TAG, "initCameraPermission:Permission Camera");
            } else {
                ActivityCompat.requestPermissions(SingleArviewActivity.this,
                        new String[]{android.Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    private World initSingleARworld() {

        final World mWorld = new World(this);

        mProgressDialog = ProgressDialog.show(this, null, "Harap Tunggu...", true, false);

        SingleArviewActivity.this.runOnUiThread(() -> {
            mProgressDialog.dismiss();
            try {
                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                assert mShop != null;
                double latitude = mShop.getShopLatitude();
                double longitude = mShop.getShopLongitude();

                mWorld.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude(), mWorld.getAltitude());

                int shopID = mShop.getShopId();

                GeoObject geoObject = new GeoObject(shopID);
                geoObject.setGeoPosition(latitude, longitude);
                geoObject.setImageResource(R.mipmap.placeholder);
                geoObject.calculateDistanceMeters(geoObject);
                geoObject.setName(mShop.getShopName());

                mWorld.addBeyondarObject(geoObject);

                mBeyoundFragment.setOnClickBeyondarObjectListener(arrayList -> {
                    if (arrayList.size() > 0) {
                        mCardViewInfomarker = findViewById(R.id.CV_infomarker_single_ar);
                        mJarakInfo = findViewById(R.id.jarak_info_single_ar);
                        if (mCardViewInfomarker.getVisibility() == View.GONE) {
                            mCardViewInfomarker.setVisibility(View.VISIBLE);
                        }
                    }

                    mCardViewInfomarker.setOnClickListener(view -> {
                        Intent intent = new Intent(SingleArviewActivity.this, InfoPertokoanActivity.class);
                        intent.putExtra("Shop", mShop);
                        startActivity(intent);
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return mWorld;
    }

    private double round(double value) {
        long factor = (long) Math.ceil(10);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (mRadarWorldPlugin == null)
            return;
        if (seekBar == mSeekbarMaxDistance) {
            if (i < 1000) {
                mTextViewMaxDistance.setText("Jarak Max : " + i + " m");
            } else {
                int toKm = i / 1000;
                mTextViewMaxDistance.setText("Jarak Max : " + toKm + " Km");
            }
            mBeyoundFragment.setMaxDistanceToRender(i);
            mRadarWorldPlugin.setMaxDistance(i);
        } else if (seekBar == mSeekbarFactorDistance) {
            if (i < 1000) {
                mTextViewFactorDistance.setText("Zoom : " + i);
            } else {
                int toKmFactor = i / 1000;
                mTextViewFactorDistance.setText("Zoom : " + toKmFactor);
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
        initArLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeyoundFragment.onResume();
        BeyondarLocationManager.enable();
    }

    @Override
    protected void onPause() {
        mBeyoundFragment.onPause();
        BeyondarLocationManager.disable();
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
