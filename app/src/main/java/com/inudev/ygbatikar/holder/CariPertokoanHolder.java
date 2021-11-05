package com.inudev.ygbatikar.holder;

import android.annotation.SuppressLint;
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
 * Created by Rem on 11/29/2017 on 7:51 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class CariPertokoanHolder extends RecyclerView.ViewHolder {

    private TextView ntoko_list, notifikasi_label, waktuOperasional;
    private ImageView image_toko_list;

    public CariPertokoanHolder(View itemView) {
        super(itemView);
        waktuOperasional = itemView.findViewById(R.id.waktu_operasional_cari_pertokoan);
        ntoko_list = itemView.findViewById(R.id.ntoko_list);
        notifikasi_label = itemView.findViewById(R.id.notifikasi_label);
        image_toko_list = itemView.findViewById(R.id.image_toko_list);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Shop mShop) {

        ntoko_list.setText(mShop.getShopName());
        waktuOperasional.setText("Setiap Hari" + " " + mShop.getShopOpenTime() + " - " + mShop.getShopCloseTime());

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
            notifikasi_label.setText("Hampir Buka");
            notifikasi_label.setTextSize(10);
            notifikasi_label.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_notif_hampirbuka));
            notifikasi_label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.Primary_Light_Brown_200));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            notifikasi_label.setText("Buka");
            notifikasi_label.setTextSize(10);
            notifikasi_label.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_notif_buka));
            notifikasi_label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.Primary_Light_Flat_Navigation));
        } else if (hour == JamHampirTutup) {
            notifikasi_label.setText("Hampir Tutup");
            notifikasi_label.setTextSize(10);
            notifikasi_label.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_notif_hampirtutup));
            notifikasi_label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.Primary_Light_Red_200));
        } else {
            notifikasi_label.setText("Tutup");
            notifikasi_label.setTextSize(10);
            notifikasi_label.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_notif_tutup));
            notifikasi_label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.Primary_Light_Red_600));
        }

        if (image_toko_list != null) {
            Glide.with(itemView.getContext())
                    .load(mShop.getShopPictureProfile())
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(image_toko_list);
        }
    }
}
