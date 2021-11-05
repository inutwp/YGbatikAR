package com.inudev.ygbatikar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.model.ShopModel;

import java.util.Objects;

public class LoadNavigationActivity extends AppCompatActivity {

    int splashInterval = 10000;

    TextView loadMaps;
    ImageView location_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_navigation);

        loadMaps = findViewById(R.id.loadMaps);
        //Animation animation = AnimationUtils.loadAnimation(
        //        getApplicationContext(), R.anim.fadein);
        //loadMaps.startAnimation(animation);

        location_logo = findViewById(R.id.location_logo);
        Animation animation1 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.blink);
        location_logo.setAnimation(animation1);

        try {
            final ShopModel shopModel = (ShopModel)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");
            assert shopModel != null;

            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(splashInterval);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    Intent compassView = new Intent(LoadNavigationActivity.this,
                            NavigationActivity.class);
                    compassView.putExtra("Shop", shopModel);
                    LoadNavigationActivity.this.startActivity(compassView);
                    LoadNavigationActivity.this.finish();
                }
            });
            thread.start();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

}
