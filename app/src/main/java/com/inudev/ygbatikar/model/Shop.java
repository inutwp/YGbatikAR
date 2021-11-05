package com.inudev.ygbatikar.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shop implements Parcelable {

    @SerializedName("shop_id")
    @Expose
    public Integer shopId;
    @SerializedName("shop_name")
    @Expose
    public String shopName;
    @SerializedName("shop_phone_number")
    @Expose
    public String shopPhoneNumber;
    @SerializedName("shop_address")
    @Expose
    public String shopAddress;
    @SerializedName("shop_picture_profile")
    @Expose
    public String shopPictureProfile;
    @SerializedName("shop_open_time")
    @Expose
    public String shopOpenTime;
    @SerializedName("shop_close_time")
    @Expose
    public String shopCloseTime;
    @SerializedName("shop_latitude")
    @Expose
    public Double shopLatitude;
    @SerializedName("shop_longitude")
    @Expose
    public Double shopLongitude;

    public final static Parcelable.Creator<Shop> CREATOR = new Creator<Shop>() {

        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return (new Shop[size]);
        }

    };

    protected Shop(@NonNull Parcel in) {
        this.shopId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.shopName = ((String) in.readValue((String.class.getClassLoader())));
        this.shopPhoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.shopAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.shopPictureProfile = ((String) in.readValue((String.class.getClassLoader())));
        this.shopOpenTime = ((String) in.readValue((String.class.getClassLoader())));
        this.shopCloseTime = ((String) in.readValue((String.class.getClassLoader())));
        this.shopLatitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.shopLongitude = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public Shop() {
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhoneNumber() {
        return shopPhoneNumber;
    }

    public void setShopPhoneNumber(String shopPhoneNumber) {
        this.shopPhoneNumber = shopPhoneNumber;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPictureProfile() {
        return shopPictureProfile;
    }

    public void setShopPictureProfile(String shopPictureProfile) {
        this.shopPictureProfile = shopPictureProfile;
    }

    public String getShopOpenTime() {
        return shopOpenTime;
    }

    public void setShopOpenTime(String shopOpenTime) {
        this.shopOpenTime = shopOpenTime;
    }

    public String getShopCloseTime() {
        return shopCloseTime;
    }

    public void setShopCloseTime(String shopCloseTime) {
        this.shopCloseTime = shopCloseTime;
    }

    public Double getShopLatitude() {
        return shopLatitude;
    }

    public void setShopLatitude(Double shopLatitude) {
        this.shopLatitude = shopLatitude;
    }

    public Double getShopLongitude() {
        return shopLongitude;
    }

    public void setShopLongitude(Double shopLongitude) {
        this.shopLongitude = shopLongitude;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(shopId);
        dest.writeValue(shopName);
        dest.writeValue(shopPhoneNumber);
        dest.writeValue(shopAddress);
        dest.writeValue(shopPictureProfile);
        dest.writeValue(shopOpenTime);
        dest.writeValue(shopCloseTime);
        dest.writeValue(shopLatitude);
        dest.writeValue(shopLongitude);
    }

    public int describeContents() {
        return 0;
    }
}