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
import com.inudev.ygbatikar.model.ShopModel;
import com.inudev.ygbatikar.model.Shop;

import java.util.Calendar;


/**
 * Created by Rem on 1/30/2017 on 8:34 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class ListPertokoanHolder extends RecyclerView.ViewHolder {


    private TextView ntoko;
    private TextView alamat;
    private TextView notif_operasional;
    //private TextView jarak;
    private ImageView pictoko;

    public ShopModel shopModel;


    public ListPertokoanHolder(View itemView) {
        super(itemView);
        ntoko = itemView.findViewById(R.id.nama_toko);
        alamat = itemView.findViewById(R.id.alamat_toko);
        pictoko = itemView.findViewById(R.id.pic_toko);
        notif_operasional = itemView.findViewById(R.id.notif_operasional);
        //jarak = (TextView) itemView.findViewById(R.id.jarak_toko);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    public void bind(@NonNull final Shop mShop) {
        ntoko.setText(mShop.getShopName());
        alamat.setText("Jl.Malioboro");
        //jarak.setText(shopModel.getJarak());

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
            notif_operasional.setText("Hampir Buka");
            notif_operasional.setTextColor(Color.WHITE);
            notif_operasional.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_buka_title));
        } else if (hour >= JamBuka && hour < JamHampirTutup) {
            notif_operasional.setText("Buka");
            notif_operasional.setTextColor(Color.WHITE);
            notif_operasional.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_buka_title));
        } else if (hour == JamHampirTutup) {
            notif_operasional.setText("Hampir Tutup");
            notif_operasional.setTextColor(Color.WHITE);
            notif_operasional.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_hampir_tutup_title));
        } else {
            notif_operasional.setText("Tutup");
            notif_operasional.setTextColor(Color.WHITE);
            notif_operasional.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_tutup_title));
        }

        Glide.with(itemView.getContext())
                .load(mShop.getShopPictureProfile())
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.placeholderOf(R.drawable.actionbar_img))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(pictoko);
    }
}

