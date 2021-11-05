package com.inudev.ygbatikar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.inudev.ygbatikar.R;

/**
 * Created by Rem on 1/30/2017 on 10:03 AM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class SplashScreen extends Activity {

    TextView splashTextView;
    int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

        splashTextView = findViewById(R.id.splashTextView);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(splashInterval);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                Intent i = new Intent(SplashScreen.this, ShopActivity.class);
                startActivity(i);
                SplashScreen.this.finish();
            }
        });
        thread.start();
    }
}
