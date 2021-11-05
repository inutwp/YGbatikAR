package com.inudev.ygbatikar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inudev.ygbatikar.R;

public class CheckGPSFragment extends DialogFragment {

    public CheckGPSFragment() {
        // Required empty public constructor
    }

    TextView textCheckGpsone, textCheckGpstwo;
    ImageView imageCheckGps, imageCheckGpsClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_gps, container, false);
        textCheckGpsone = view.findViewById(R.id.TV_check_gps);
        textCheckGpstwo = view.findViewById(R.id.TV_check2_gps);
        imageCheckGps = view.findViewById(R.id.image_check_gps);
        imageCheckGps.setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            dismiss();
        });
        imageCheckGpsClose = view.findViewById(R.id.img_check_gps_close);
        imageCheckGpsClose.setOnClickListener(view12 -> dismiss());

        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCancelable(false);
        return view;
    }
}
