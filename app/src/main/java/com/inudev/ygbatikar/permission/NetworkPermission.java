package com.inudev.ygbatikar.permission;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class NetworkPermission {

    private Context mContext;

    public NetworkPermission(Context context) {
        this.mContext = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
