package com.inudev.ygbatikar.holder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
 * Created by Rem on 2/26/2018 on 2:53 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class InfoMarkerHolder extends RecyclerView.ViewHolder {

    private TextView nTokoInfoMarker, alamatInfoMarker, operasionalInfoMarker;
    private ImageView imageMarkerInfo;

    public InfoMarkerHolder(View itemView) {
        super(itemView);
        nTokoInfoMarker = itemView.findViewById(R.id.ntoko_infomarker);
        operasionalInfoMarker = itemView.findViewById(R.id.operasional_infomarker);
        alamatInfoMarker = itemView.findViewById(R.id.alamat_infomarker);
        imageMarkerInfo = itemView.findViewById(R.id.image_infomarker);
    }

    @SuppressLint("SetTextI18n")
    public void bind(@NonNull final Shop mShop) {
        nTokoInfoMarker.setText(mShop.getShopName());
        alamatInfoMarker.setText(mShop.getShopAddress());

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
            operasionalInfoMarker.setText("Hampir Buka" + " : " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());
            operasionalInfoMarker.setTextColor(Color.WHITE);
            operasionalInfoMarker.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_buka_title));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            operasionalInfoMarker.setText("Buka" + " : " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());
            operasionalInfoMarker.setTextColor(Color.WHITE);
            operasionalInfoMarker.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_buka_title));
        } else if (hour == JamHampirTutup) {
            operasionalInfoMarker.setText("Hampir Tutup" + " : " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());
            operasionalInfoMarker.setTextColor(Color.WHITE);
            operasionalInfoMarker.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_tutup_title));
        } else {
            operasionalInfoMarker.setText("Tutup" + " : " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());
            operasionalInfoMarker.setTextColor(Color.WHITE);
            operasionalInfoMarker.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_tutup_title));
        }

        Glide.with(itemView.getContext())
                .load(mShop.getShopPictureProfile())
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageMarkerInfo);
    }
}
