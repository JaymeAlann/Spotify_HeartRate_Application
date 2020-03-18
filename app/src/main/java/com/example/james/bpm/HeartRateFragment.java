package com.example.james.bpm;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class HeartRateFragment extends Fragment{

    private static final String TAG = "Heart Rate Fragment";

    private ImageButton btnONOFF;


    public static HeartRateFragment newInstance() {
        HeartRateFragment fragment = new HeartRateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);
        // Inflate the layout for this fragment

        btnONOFF = view.findViewById(R.id.BluetoothOnOff);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        PulsatorLayout pulsator = view.findViewById(R.id.pulsator);
        pulsator.start();
        return view;
    }
}
