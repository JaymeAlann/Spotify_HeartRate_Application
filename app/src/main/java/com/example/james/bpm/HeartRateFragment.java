package com.example.james.bpm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class HeartRateFragment extends Fragment {

    private static final String TAG = "Heart Rate Fragment";

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mHeartRateView;
    private TextView monitorDevice;
    private ImageButton btnONOFF;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"onReceive: STATE TURNING ON");
                        break;

                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

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

        mHeartRateView = view.findViewById(R.id.HeartRateText);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDiableBT();
            }
        });


        PulsatorLayout pulsator = view.findViewById(R.id.pulsator);
        pulsator.start();
        return view;
    }

    public void enableDiableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilites");
        }
        else if(!mBluetoothAdapter.isEnabled()){
            btnONOFF.setImageResource(R.drawable.bluetooth_on);
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mReceiver, BTIntent);
        }
        else if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            btnONOFF.setImageResource(R.drawable.bluetooth_off);
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mReceiver, BTIntent);
        }

    }
}
