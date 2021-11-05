package com.inudev.ygbatikar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.inudev.ygbatikar.R;

public class AboutFragment extends BottomSheetDialogFragment {

    RelativeLayout RLInfoKampus, RLPrivacyPolicy, RLIconLibrary;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);
        RLInfoKampus = view.findViewById(R.id.info_kampus);
        RLInfoKampus.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), WebViewTwoActivity.class);
            startActivity(intent);
        });

        RLPrivacyPolicy = view.findViewById(R.id.RL_privacypolicy);
        RLPrivacyPolicy.setOnClickListener(view12 -> {
            Intent intent2 = new Intent(view12.getContext(), WebViewOneActivity.class);
            startActivity(intent2);
        });

        RLIconLibrary = view.findViewById(R.id.RL_iconlibrary);
        RLIconLibrary.setOnClickListener(view13 -> {
            Intent intent3 = new Intent(view13.getContext(), IconLibraryActivity.class);
            startActivity(intent3);
        });
        return view;
    }
}
