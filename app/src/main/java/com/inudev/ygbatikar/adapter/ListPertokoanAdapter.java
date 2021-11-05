package com.inudev.ygbatikar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.holder.ListPertokoanHolder;
import com.inudev.ygbatikar.model.Shop;

import java.util.List;

/**
 * Created by Rem on 1/30/2017 on 10:02 AM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public abstract class ListPertokoanAdapter extends RecyclerView.Adapter<ListPertokoanHolder> {
    private List<Shop> mShop;

    public Context context;
    int num = 1;

    protected ListPertokoanAdapter(List<Shop> mShop) {
        this.mShop = mShop;
    }

    @NonNull
    @Override
    public ListPertokoanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pertokoan, parent, false);
        return new ListPertokoanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPertokoanHolder holder, int position) {
        setScaleAnimation(holder.itemView);
        try {
            bindHolder(holder, mShop.get(position));
        } catch (Exception e) {
            bindHolder(holder, null);
        }
    }

    protected abstract void bindHolder(ListPertokoanHolder holder, Shop mShop);

    private void setScaleAnimation(View view) {
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(350);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        //if (num * 3 > data.size()) {
        //  return data.size();
        //} else {
        //    Collections.shuffle(data);
        //    return num * 3;
        //}
        return mShop.size();
    }
}
