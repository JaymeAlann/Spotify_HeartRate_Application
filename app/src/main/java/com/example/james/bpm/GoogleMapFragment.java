package com.example.james.bpm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline mPolyline;
    private LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private Boolean isRunning = false;
    private ArrayList<LatLng> LatLngList = new ArrayList<>();
    private ArrayList<Double> locationAltidude = new ArrayList<>();
    private Button startStopBtn;
    private Chronometer workoutTime;

    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static GoogleMapFragment newInstance() {
        GoogleMapFragment fragment = new GoogleMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_google_map, container, false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        workoutTime = view.findViewById(R.id.chronometer);
        startStopBtn = view.findViewById(R.id.startstop);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning){
                    workoutTime.setVisibility(View.VISIBLE);
                    startStopBtn.setText(R.string.stop_workout);
                    workoutTime.setBase(SystemClock.elapsedRealtime());
                    workoutTime.start();
                    isRunning = true;
                }else if(isRunning){
                    startStopBtn.setText(R.string.start_workout);
                    workoutTime.stop();
                    isRunning = false;
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d("Maps...................","NETWORK_PROVIDER");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    locationAltidude.add(location.getAltitude());
                    LatLng latLng = new LatLng(latitude, longitude);
                    if(isRunning){
                        LatLngList.add(latLng);
                    }
                    Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.mapicon);
                    Bitmap bitmap = drawable.getBitmap();
                    Bitmap icon_small = Bitmap.createScaledBitmap(bitmap, 50,50,false);
                    mMap.clear();
                    if(isRunning){drawPolyLine();}
                    mMap.addMarker(new MarkerOptions().position(latLng).title("currentAddress").icon(BitmapDescriptorFactory.fromBitmap(icon_small)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        return view;
    }

    public void drawPolyLine(){
        for (int i = 0; i < LatLngList.size()-1; i++){
            LatLng src = LatLngList.get(i);
            double srcAlt = locationAltidude.get(i);
            LatLng dest = LatLngList.get(i + 1);
            double destAlt = locationAltidude.get(i + 1);

            mPolyline = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude,dest.longitude)
                    ).width(10).color(Color.BLUE).geodesic(true)
            );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
