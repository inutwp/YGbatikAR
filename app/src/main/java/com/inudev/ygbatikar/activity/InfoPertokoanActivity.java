package com.inudev.ygbatikar.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aquery.AQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.DrawMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.inudev.ygbatikar.model.Shop;

import java.util.Calendar;
import java.util.Objects;

public class InfoPertokoanActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "InfoPertokoanActivity";
    private SupportMapFragment mSupportMapFragment;
    private AQuery aq;
    public SensorManager mSensorManager;
    public LocationManager mLocationManager;

    TextView
            notifOperasional,
            keNavigation, streetView,
            callPertokoan, sharePertokoan,
            hariSenin, hariSelasa, hariRabu, hariKamis, hariJumat, hariSabtu, hariMinggu,
            operasionalSenin, operasionalSelasa, operasionalRabu, operasionalKamis, operasionalJumat, operasionalSabtu, operasionalMinggu;

    ImageView imagePertokoan;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pertokoan);
        aq = new AQuery(this);

        final Shop mShop = (Shop)
                Objects.requireNonNull(getIntent()
                        .getExtras())
                        .get("Shop");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar AB = getSupportActionBar();
        Objects.requireNonNull(AB).setDisplayHomeAsUpEnabled(true);
        AB.setDisplayShowTitleEnabled(true);
        AB.setTitle(Objects.requireNonNull(mShop).getShopName());
        AB.setDefaultDisplayHomeAsUpEnabled(true);

        initMapStreetViewReady();
        initNotifOperasional();
        initContentandClick();
        initCheckSensor();
        setupWindowAnimations();
    }

    @SuppressLint("NewApi")
    private void setupWindowAnimations() {
        android.transition.Slide slide = new android.transition.Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);
    }

    private void initContentandClick() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            imagePertokoan = findViewById(R.id.image_pertokoan);
            Glide.with(this)
                    .load(Objects.requireNonNull(mShop).getShopPictureProfile())
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imagePertokoan);


            aq.id(R.id.image_pertokoan).click(view -> {
                Intent intenttoImageFullScreen = new Intent(InfoPertokoanActivity.this, ImageFullScreenActivity.class);
                intenttoImageFullScreen.putExtra("Shop", mShop);
                aq.open(intenttoImageFullScreen);
            });

            aq.id(R.id.waktu_operasional).text("Setiap Hari" + " : " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());

            aq.id(R.id.info_alamat).text(mShop.getShopAddress());

            aq.id(R.id.info_notelepon).text(mShop.getShopPhoneNumber());

            aq.id(R.id.nama_pertokoan).text(mShop.getShopName());

            aq.id(R.id.alamat_pertokoan).text(mShop.getShopAddress());

            keNavigation = findViewById(R.id.ke_navigation);
            //Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            //keNavigation.startAnimation(animation);
            keNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initCheckGPS();
                }

                private void initCheckGPS() {
                    mLocationManager = (LocationManager) InfoPertokoanActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    assert mLocationManager != null;
                    boolean GpsStatus = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (GpsStatus) {
                        Intent ViewMaps = new Intent(InfoPertokoanActivity.this, MapsDetailActivity.class);
                        ViewMaps.putExtra("Shop", mShop);
                        aq.open(ViewMaps);
                    } else {
                        FragmentManager mFragmentManager = getSupportFragmentManager();
                        CheckGPSFragment checkGPSFragment = new CheckGPSFragment();
                        checkGPSFragment.show(mFragmentManager, "checkgpsdialog");
                    }
                }
            });

            streetView = findViewById(R.id.streetview);
            //Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            //streetView.startAnimation(animation1);
            streetView.setOnClickListener(view -> {
                Intent streetview = new Intent(InfoPertokoanActivity.this, StreetViewMapActivity.class);
                streetview.putExtra("Shop", mShop);
                aq.open(streetview);
            });

            callPertokoan = findViewById(R.id.call_pertokoan);
            //Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            //callPertokoan.startAnimation(animation2);
            callPertokoan.setOnClickListener(view -> loadCall());

            sharePertokoan = findViewById(R.id.share_pertokoan);
            //Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            //sharePertokoan.startAnimation(animation3);
            sharePertokoan.setOnClickListener(view -> {
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                final Shop shopList = (Shop) getIntent().getExtras().get("Shop");
                String sharedBody = "\n" + "Toko " + Objects.requireNonNull(shopList).getShopName() + "\n\n"
                        + " "
                        + "\n" + "Alamat:" + "\n" + shopList.getShopAddress() + "\n\n"
                        + " "
                        + "\n" + "No telepon:" + "\n" + shopList.getShopPhoneNumber() + "\n\n"
                        + " "
                        + "\n" + "Operasional:" + "Setiap Hari" + "\n" + "Jam Buka:  " + shopList.getShopOpenTime() + "\n" + "Jam Tutup:  " + shopList.getShopCloseTime() + "\n\n"
                        + " "
                        + "Lokasi Toko : " + "\n" +
                        "http://maps.google.com/?q=" + shopList.getShopLatitude() + "," + shopList.getShopLongitude() + "\n\n"
                        + "Using App 'YGbatikAR'";
                intent2.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                intent2.putExtra(Intent.EXTRA_TEXT, sharedBody);
                startActivity(Intent.createChooser(intent2, "Pilih Aplikasi"));
            });
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initNotifOperasional() {
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            notifOperasional = findViewById(R.id.notif_operasional);
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            String jambuka = Objects.requireNonNull(mShop).getShopOpenTime();
            String subJambuka = jambuka.substring(0, 2);

            String jamtutup = mShop.getShopCloseTime();
            String subJamtutup = jamtutup.substring(0, 2);

            int JamBuka = Integer.parseInt(subJambuka);
            int JamTutup = Integer.parseInt(subJamtutup);

            int JamHampirBuka = JamBuka - 1;
            int JamHampirTutup = JamTutup - 1;

            if (hour == JamHampirBuka) {
                notifOperasional.setText("Hampir Buka ");
                notifOperasional.setTextColor(getResources().getColor(R.color.Primary_Light_Brown_200));
                notifOperasional.setBackground(getResources().getDrawable(R.drawable.bg_notif_hampirbuka));
                notifOperasional.setTextSize(15);
            } else if (hour >= JamBuka && hour < JamHampirTutup) {
                notifOperasional.setText("Buka ");
                notifOperasional.setTextColor(getResources().getColor(R.color.Primary_Light_Flat_Navigation));
                notifOperasional.setBackground(getResources().getDrawable(R.drawable.bg_notif_buka));
                notifOperasional.setTextSize(15);
            } else if (hour == JamHampirTutup) {
                notifOperasional.setText("Hampir Tutup ");
                notifOperasional.setTextColor(getResources().getColor(R.color.Primary_Light_Red_200));
                notifOperasional.setBackground(getResources().getDrawable(R.drawable.bg_notif_hampirtutup));
                notifOperasional.setTextSize(15);
            } else {
                notifOperasional.setText("Tutup ");
                notifOperasional.setTextColor(getResources().getColor(R.color.Primary_Light_Red_600));
                notifOperasional.setBackground(getResources().getDrawable(R.drawable.bg_notif_tutup));
                notifOperasional.setTextSize(15);
            }

            Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case Calendar.MONDAY:
                    hariSenin = findViewById(R.id.hariSenin);
                    hariSenin.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariSenin.setTypeface(font);
                    operasionalSenin = findViewById(R.id.operasional1);
                    operasionalSenin.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalSenin.setTypeface(font);
                    break;
                case Calendar.TUESDAY:
                    hariSelasa = findViewById(R.id.hariSelasa);
                    hariSelasa.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariSelasa.setTypeface(font);
                    operasionalSelasa = findViewById(R.id.operasional2);
                    operasionalSelasa.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalSelasa.setTypeface(font);
                    break;
                case Calendar.WEDNESDAY:
                    hariRabu = findViewById(R.id.hariRabu);
                    hariRabu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariRabu.setTypeface(font);
                    operasionalRabu = findViewById(R.id.operasional3);
                    operasionalRabu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalRabu.setTypeface(font);
                    break;
                case Calendar.THURSDAY:
                    hariKamis = findViewById(R.id.hariKamis);
                    hariKamis.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariKamis.setTypeface(font);
                    operasionalKamis = findViewById(R.id.operasional4);
                    operasionalKamis.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalKamis.setTypeface(font);
                    break;
                case Calendar.FRIDAY:
                    hariJumat = findViewById(R.id.hariJumat);
                    hariJumat.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariJumat.setTypeface(font);
                    operasionalJumat = findViewById(R.id.operasional5);
                    operasionalJumat.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalJumat.setTypeface(font);
                    break;
                case Calendar.SATURDAY:
                    hariSabtu = findViewById(R.id.hariSabtu);
                    hariSabtu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariSabtu.setTypeface(font);
                    operasionalSabtu = findViewById(R.id.operasional6);
                    operasionalSabtu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalSabtu.setTypeface(font);
                    break;
                case Calendar.SUNDAY:
                    hariMinggu = findViewById(R.id.hariMinggu);
                    hariMinggu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    hariMinggu.setTypeface(font);
                    operasionalMinggu = findViewById(R.id.operasional7);
                    operasionalMinggu.setTextColor(getResources().getColor(R.color.Primary_Light_Material_1000));
                    operasionalMinggu.setTypeface(font);
                    break;
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
            Log.i(TAG, "initNotifOperasional: ", n);
        }
    }

    private void initMapStreetViewReady() {
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.info_maps_pertokoan);

        InfoPertokoanActivity.this.runOnUiThread(() -> {
            mSupportMapFragment.getMapAsync(InfoPertokoanActivity.this);
        });
    }

    private void initCheckSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        if (mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
            try {
                final Shop mShop = (Shop)
                        Objects.requireNonNull(getIntent()
                                .getExtras())
                                .get("Shop");

                aq.id(R.id.view_AR_LinearLayout).click(view -> {
                    Intent toViewAR = new Intent(InfoPertokoanActivity.this, SingleArviewActivity.class);
                    toViewAR.putExtra("Shop", mShop);
                    aq.open(toViewAR);
                });
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
        } else {
            aq.id(R.id.view_AR_LinearLayout).click(view -> {
                FragmentManager mFragmentManager = getSupportFragmentManager();
                CompassSensorCheckFragment compassSensorCheckFragment = new CompassSensorCheckFragment();
                compassSensorCheckFragment.show(mFragmentManager, "compasscheckdialog");
            });
        }
    }

    private void loadCall() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CallFragment callFragment = new CallFragment();
        callFragment.show(fragmentManager, "dialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_maps_json
            ));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "cant find style. Error: ", e);
        }

        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            final double latitude = Objects.requireNonNull(mShop).getShopLatitude();
            final double longitude = mShop.getShopLongitude();
            final LatLng origin = new LatLng(latitude, longitude);

            DrawMarker.getInstance(getApplicationContext()).draw(googleMap, origin, R.drawable.ic_013_placeholder, mShop.getShopName(), mShop.getShopAddress());
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(InfoPertokoanActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(InfoPertokoanActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
