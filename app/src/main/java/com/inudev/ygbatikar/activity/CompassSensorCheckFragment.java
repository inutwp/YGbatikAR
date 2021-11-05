package com.inudev.ygbatikar.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inudev.ygbatikar.R;

public class CompassSensorCheckFragment extends DialogFragment {

    public CompassSensorCheckFragment() {
        // Required empty public constructor
    }

    TextView textCheckCompassone, textCheckCompasstwo;
    ImageView imageCheckCompass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass_check, container, false);
        imageCheckCompass   = view.findViewById(R.id.image_checkSensor_compass);
        textCheckCompassone = view.findViewById(R.id.TV_check_compass);
        textCheckCompasstwo = view.findViewById(R.id.TV_check2_compass);
        return view;
    }
}
