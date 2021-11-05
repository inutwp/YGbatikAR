package com.inudev.ygbatikar.adapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.InfoMarkerHolder;
import com.inudev.ygbatikar.model.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rem on 2/26/2018 on 2:53 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public abstract class InfoMarkerAdapter extends RecyclerView.Adapter<InfoMarkerHolder> implements Filterable {

    private List<Shop> listShop;
    private List<Shop> filteredShopList;

    protected InfoMarkerAdapter(List<Shop> mShop) {
        this.listShop = mShop;
        this.filteredShopList = mShop;
    }

    @NonNull
    @Override
    public InfoMarkerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.content_infomarker, parent, false);
        return new InfoMarkerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoMarkerHolder holder, int position) {

        try {
            bindHolder(holder, filteredShopList.get(position));
        } catch (Exception e) {
            bindHolder(holder, null);
        }
    }

    protected abstract void bindHolder(InfoMarkerHolder holder, Shop mShop);

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
                    filteredShopList = listShop;
                } else {
                    List<Shop> searchShop = new ArrayList<>();
                    for (Shop mShop : listShop) {
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
