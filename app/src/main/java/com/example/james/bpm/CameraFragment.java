package com.example.james.bpm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;


public class CameraFragment extends Fragment {
    private static final int CAMERA_RQ_CODE = 1;
    private static final int VERIFY_PERMISSIONS_REQUEST = 5;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo, container, false);

        if(checkPermissionArray(Permissions.PERMISSIONS)){
            Button btnLaunchCam = view.findViewById(R.id.btnLaunchCamera);
            btnLaunchCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camIntent, CAMERA_RQ_CODE);
                }
            });
        }else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
        return view;
    }

    public void verifyPermissions(String[] permissions){
        ActivityCompat.requestPermissions(getActivity(), permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    public boolean checkPermissionArray(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission){
        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }
}
