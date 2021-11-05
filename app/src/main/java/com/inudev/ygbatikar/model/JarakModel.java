package com.inudev.ygbatikar.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Rem on 2/7/2018 on 4:14 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class JarakModel implements Parcelable {

    public String jarak;

    public JarakModel(@NonNull Parcel in) {
        jarak = in.readString();
    }

    public static final Creator<JarakModel> CREATOR = new Creator<JarakModel>() {
        @Override
        public JarakModel createFromParcel(Parcel in) {
            return new JarakModel(in);
        }

        @Override
        public JarakModel[] newArray(int size) {
            return new JarakModel[size];
        }
    };

    public JarakModel() {

    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jarak);
    }
}
