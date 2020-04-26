package com.example.james.bpm;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.bluetooth.BluetoothGatt.GATT_FAILURE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HeartRateFragment extends Fragment{

    private static final String TAG = "Heart Rate Fragment";
    static final int REQUEST_ENABLE_BT = 1234;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback scanCallback;
    private BluetoothGattCallback gattCallback;
    private BluetoothDevice mBTDevice;

    private final int PERMISSION_REQUEST_COARSE_LOCATION = 2020;
    private UUID HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D);
    private UUID HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37);
    private UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = convertFromInteger(0x2902);

    private TextView hearRateText;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
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
        hearRateText = view.findViewById(R.id.HeartRateText);
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        PulsatorLayout pulsator = view.findViewById(R.id.pulsator);
        //pulsator.setDuration();
        pulsator.start();

        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.e(TAG, "STATE: " + newState);
                if(status == GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.e(TAG, "STATE_CONNECTED");
                        // We successfully connected, proceed with service discovery
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.e(TAG, "STATE_DISCONNECTED");
                        // We successfully disconnected on our own request
                        gatt.close();
                    } else {
                        Log.e(TAG, "STATE_ELSE");
                        // We're CONNECTING or DISCONNECTING, ignore for now
                    }
                } else {
                    gatt.close();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                Log.e(TAG, "Service: " + status);
                // Check if the service discovery succeeded. If not disconnect
                if (status == GATT_FAILURE) {
                    Log.e(TAG, "Service discovery failed");
                    gatt.disconnect();
                    return;
                }

                final List<BluetoothGattService> services = gatt.getServices();

                Log.e(TAG, "onServicesDiscovered: services FOUND :D");

                BluetoothGattCharacteristic characteristic = null;
                for(BluetoothGattService service : services){
                    if(service.getUuid().equals(HEART_RATE_SERVICE_UUID)){
                        final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        for(BluetoothGattCharacteristic character : characteristics){
                            if(character.getUuid().equals(HEART_RATE_MEASUREMENT_CHAR_UUID)){
                                Log.e(TAG, "FOUND IT");
                                gatt.setCharacteristicNotification(character, true);
                                BluetoothGattDescriptor descriptor = character.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                                if(descriptor!= null){
                                    Log.e(TAG, "Descriptor Found");
                                }
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
                            else{
                                Log.e(TAG, "Nooooot IT");
                            }
                        }
                    }
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                BluetoothGattCharacteristic Char = gatt.getService(HEART_RATE_SERVICE_UUID).getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                byte[] bytes = characteristic.getValue();
                int heartRate = bytes[1];
                String str = new String(bytes);
                hearRateText.setText(heartRate+" ");
                int heartRateMili = 6000/heartRate;
                pulsator.setDuration(heartRateMili);

                Intent heartIntent = new Intent();
                heartIntent.putExtra("BeatsPerMinute", heartRate);
                heartIntent.setAction("android.intent.action.BPM_RECEIVED");
                heartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Objects.requireNonNull(getActivity()).sendBroadcast(heartIntent);
            }
        };
        scanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBTDevice = device;
                        mBluetoothAdapter.stopLeScan(scanCallback);
                        mBTDevice.connectGatt(getActivity(), false, gattCallback, BluetoothDevice.TRANSPORT_LE);
                    }
                });
            }
        };
        UUID[] filter = new UUID[1];
        filter[0] = HEART_RATE_SERVICE_UUID;
        mBluetoothAdapter.startLeScan(filter, scanCallback);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothAdapter.startLeScan(scanCallback);

    }

    @Override
    public void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
