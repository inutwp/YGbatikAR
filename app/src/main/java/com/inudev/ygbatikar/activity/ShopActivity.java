package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aquery.AQuery;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.permission.LocationPermission;
import com.inudev.ygbatikar.permission.NetworkPermission;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class ShopActivity extends AppCompatActivity implements android.location.LocationListener {


    private static final String TAG = "ShopActivity";

    public boolean doubleTapBack = false;

    private AQuery aq;

    ImageView
            imageSearch, imageAr,
            imageList, imageTerdekat;

    //AdView adView;

    TextView title_appbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pertokoan);
        aq = new AQuery(this);

        initView();
        initOnClick();
        initMenuDialog();
        initCheckPermis();
        isNetworkConnected();
        isCompassSensorAvailable();
        //initAds();
    }

    private void isNetworkConnected() {
        NetworkPermission networkPermission = new NetworkPermission(getApplicationContext());
        if (networkPermission.isNetworkConnected()) {
            showNotification(true);
        } else {
            showNotification(false);
        }
    }

    private void isCompassSensorAvailable() {
        LocationPermission locationPermission = new LocationPermission(getApplicationContext());
        if (locationPermission.isCompassAvailable()) {
            aq.id(R.id.RL_AR_View).click(view -> {
                Intent intent2 = new Intent(ShopActivity.this, InfoPertokoanActivity.class);
                aq.open(intent2);
            });
        } else {
            aq.id(R.id.RL_AR_View).click(view -> {
                FragmentManager mFragmentManager = getSupportFragmentManager();
                CompassSensorCheckFragment compassSensorCheckFragment = new CompassSensorCheckFragment();
                compassSensorCheckFragment.show(mFragmentManager, "compasscheckdialog");
            });
        }
    }

    private void initCheckPermis() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {

                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                Log.d(TAG, "onPermissionsChecked: " + " OK");
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        }
                    })
                    .onSameThread()
                    .check();
        }
    }

    private void initMenuDialog() {
        aq.id(R.id.menu_dialog).click(view -> showABoutMenu());
    }

    private void showABoutMenu() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.show(fragmentManager, "about");
    }

    private void initAds() {
        //MobileAds.initialize(this, "ca-app-pub-5043666985925226~1656830634"
        // );
        //adView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder()
        // .build();
        //adView.loadAd(adRequest);
    }

    private void initView() {
        title_appbar = findViewById(R.id.title_appbar);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        title_appbar.startAnimation(animation);

        imageSearch = findViewById(R.id.image_search);
        Animation animation1 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fadein
        );
        imageSearch.setAnimation(animation1);

        imageTerdekat = findViewById(R.id.image_terdekat);
        Animation animation2 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fadein
        );
        imageTerdekat.setAnimation(animation2);

        imageAr = findViewById(R.id.image_ar);
        Animation animation3 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fadein
        );
        imageAr.setAnimation(animation3);

        imageList = findViewById(R.id.image_List);
        Animation animation4 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fadein
        );
        imageList.setAnimation(animation4);

    }

    private void initOnClick() {
        aq.id(R.id.RL_Cari_Pertokoan).click(view -> {
            Intent intent = new Intent(ShopActivity.this, CariPertokoanActivity.class);
            aq.open(intent);
        });

        aq.id(R.id.RL_Pertokoan_Disekitar).click(view -> {
            Intent intent1 = new Intent(ShopActivity.this, CariSekitarkuActivity.class);
            aq.open(intent1);
        });

        aq.id(R.id.RL_List_Pertokoan).click(view -> {
            Intent intent3 = new Intent(ShopActivity.this, ListPertokoanActivity.class);
            aq.open(intent3);
        });
    }

    private void showNotification(boolean isConnected) {
        if (isConnected) {
            Log.i(TAG, "showNotification: " + true);
        } else {
            Snackbar.make(findViewById(R.id.pertokoan_CL), "Tidak terkoneksi ke Internet", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        if (doubleTapBack) {
            super.onBackPressed();
            return;
        }

        this.doubleTapBack = true;
        aq.toast("Tekan sekali lagi untuk keluar");

        new Handler().postDelayed(() -> doubleTapBack = false, 2000);
    }

    @Override
    public void onLocationChanged(Location location) {
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
}
