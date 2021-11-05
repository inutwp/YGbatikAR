package com.inudev.ygbatikar.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopModel implements Parcelable {

    @SerializedName("shops")
    @Expose
    public List<Shop> shops = null;
    public final static Parcelable.Creator<ShopModel> CREATOR = new Creator<ShopModel>() {

        public ShopModel createFromParcel(Parcel in) {
            return new ShopModel(in);
        }

        public ShopModel[] newArray(int size) {
            return (new ShopModel[size]);
        }

    };

    protected ShopModel(@NonNull Parcel in) {
        in.readList(this.shops, (com.inudev.ygbatikar.model.Shop.class.getClassLoader()));
    }

    public ShopModel() {
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(shops);
    }

    public int describeContents() {
        return 0;
    }
}