package com.example.james.bpm;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class HeartRateFragment extends Fragment {

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mHeartRateView;

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
        mHeartRateView = view.findViewById(R.id.HeartRateText);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        PulsatorLayout pulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsator.start();
        return view;


    }
}
