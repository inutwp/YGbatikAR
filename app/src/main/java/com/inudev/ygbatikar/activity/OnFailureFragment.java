package com.inudev.ygbatikar.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.inudev.ygbatikar.R;

public class OnFailureFragment extends DialogFragment{

    ImageView imageOnFailure;

    public OnFailureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_failure, container, false);

        imageOnFailure = view.findViewById(R.id.image_on_failure);

        return view;
    }
}
