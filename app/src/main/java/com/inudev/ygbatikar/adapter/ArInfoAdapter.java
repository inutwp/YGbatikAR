package com.inudev.ygbatikar.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.ArInfoHolder;
import com.inudev.ygbatikar.model.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rem on 2/13/2018 on 2:33 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public abstract class ArInfoAdapter extends RecyclerView.Adapter<ArInfoHolder> implements Filterable {

    private List<Shop> shopList;
    private List<Shop> filteredShopList;

    protected ArInfoAdapter(List<Shop> mShop) {
        this.shopList = mShop;
        this.filteredShopList = mShop;
    }

    @NonNull
    @Override
    public ArInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.content_ar_pertokoan, parent, false);
        return new ArInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArInfoHolder holder, int position) {

        try {
            bindHolder(holder, filteredShopList.get(position));
        } catch (Exception e) {
            bindHolder(holder, null);
        }
    }

    protected abstract void bindHolder(ArInfoHolder holder, Shop mShop);

    @Override
    public int getItemCount() {
        int num = 1;
        if (num > filteredShopList.size()) {
            return filteredShopList.size();
        } else {
            return num;
        }
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
                    for (Shop getShop : shopList) {
                        if (getShop.getShopName().toLowerCase().contains(charString.toLowerCase())
                                || getShop.getShopAddress().contains(charSequence)) {
                            searchShop.add(getShop);
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