package com.inudev.ygbatikar.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.Filterable;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.CariPertokoanHolder;
import com.inudev.ygbatikar.model.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rem on 11/29/2017 on 7:56 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public abstract class CariPertokoanAdapter extends RecyclerView.Adapter<CariPertokoanHolder> implements Filterable {

    private List<Shop> shopList;
    private List<Shop> filteredShopList;

    protected CariPertokoanAdapter(List<Shop> mShop) {
        this.shopList = mShop;
        this.filteredShopList = mShop;
    }

    @NonNull
    @Override
    public CariPertokoanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.content_cari_pertokoan, parent, false);
        return new CariPertokoanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CariPertokoanHolder holder, int position) {
        setScaleAnimation(holder.itemView);
        try {
            bindHolder(holder, filteredShopList.get(position));
        } catch (Exception e) {
            bindHolder(holder, null);
        }
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(400);
        view.startAnimation(anim);
    }

    protected abstract void bindHolder(CariPertokoanHolder holder, Shop mShop);

    @Override
    public int getItemCount() {
        if (filteredShopList == null)
            return 0;
        else
            return filteredShopList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredShopList = shopList;
                } else {
                    List<Shop> searchShop = new ArrayList<>();
                    for (Shop mShop : shopList) {
                        if (mShop.getShopName().toLowerCase().contains(charString.toLowerCase())
                                || mShop.getShopAddress().contains(charSequence)) {
                            searchShop.add(mShop);
                        }
                    }
                    filteredShopList = searchShop;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredShopList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredShopList = (ArrayList<Shop>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
