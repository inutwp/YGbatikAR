package com.inudev.ygbatikar.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.model.Shop;

import java.util.Objects;


public class CallFragment extends BottomSheetDialogFragment {

    public static String TAG = "CallFragment";
    public static final int PERMISSION_CALL = 1;

    TextView noTelp;

    public CallFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        view.findViewById(R.id.img_close).setOnClickListener(view1 -> dismiss());
        try {
            final Shop mShop = (Shop)
                    Objects.requireNonNull(Objects.requireNonNull(getActivity())
                            .getIntent()
                            .getExtras())
                            .get("Shop");


            noTelp = view.findViewById(R.id.no_telp);
            assert mShop != null;
            noTelp.setText(mShop.getShopPhoneNumber());
            noTelp.setOnClickListener(view12 -> {
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mShop.getShopPhoneNumber()));
                try {
                    startActivity(intentCall);
                } catch (SecurityException se) {
                    if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) view.getContext(),
                                Manifest.permission.CALL_PHONE)) {
                            Log.e(TAG,"PermissionGrant");
                        } else {
                            ActivityCompat.requestPermissions((Activity) view.getContext(),
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    PERMISSION_CALL);
                        }
                    }
                }
            });
        } catch (NullPointerException n) {
            n.printStackTrace();
            Log.e(TAG, "onCreateView: ", n);
        }
        return view;
    }
}