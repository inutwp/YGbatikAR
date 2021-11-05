package com.inudev.ygbatikar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.model.Shop;

import java.util.Objects;

public class ImageFullScreenActivity extends AppCompatActivity {

    ImageView imageFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_full_screen);

        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(getIntent()
                            .getExtras())
                            .get("Shop");

            imageFullScreen = findViewById(R.id.image_fullscreen);
            Glide.with(this)
                    .load(Objects.requireNonNull(mShop).getShopPictureProfile())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(imageFullScreen);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }
}
