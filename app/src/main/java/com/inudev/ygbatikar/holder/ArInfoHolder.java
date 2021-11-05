package com.inudev.ygbatikar.holder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.model.Shop;

import java.util.Calendar;

/**
 * Created by Rem on 2/13/2018 on 2:28 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class ArInfoHolder extends RecyclerView.ViewHolder {

    private ImageView imageArInfo;
    private TextView ntokoArInfo;
    private TextView waktuOperasionalAr;
    private TextView operasionalPertokoan;

    public ArInfoHolder(View itemView) {
        super(itemView);
        waktuOperasionalAr = itemView.findViewById(R.id.waktu_operasional_ar);
        imageArInfo = itemView.findViewById(R.id.image_info_ar);
        ntokoArInfo = itemView.findViewById(R.id.ntoko_ar_info);
        operasionalPertokoan = itemView.findViewById(R.id.notif_operasional_info_ar);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Shop mShop) {
        ntokoArInfo.setText(mShop.getShopName());

        waktuOperasionalAr.setText("Setiap Hari" + " " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());

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
            operasionalPertokoan.setText("Hampir Buka");
            operasionalPertokoan.setTextColor(Color.WHITE);
            operasionalPertokoan.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_buka_title));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            operasionalPertokoan.setText("Buka");
            operasionalPertokoan.setTextColor(Color.WHITE);
            operasionalPertokoan.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_buka_title));
        } else if (hour == JamHampirTutup) {
            operasionalPertokoan.setText("Hampir Tutup");
            operasionalPertokoan.setTextColor(Color.WHITE);
            operasionalPertokoan.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_tutup_title));
        } else {
            operasionalPertokoan.setText("Tutup");
            operasionalPertokoan.setTextColor(Color.WHITE);
            operasionalPertokoan.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_tutup_title));
        }

        Glide.with(itemView.getContext())
                .load(mShop.getShopPictureProfile())
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageArInfo);
    }
}
