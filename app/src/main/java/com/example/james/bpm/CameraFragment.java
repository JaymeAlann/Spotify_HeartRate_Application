package com.example.james.bpm;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class CameraFragment extends Fragment {

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;

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

    private void setupViewPager(View view){

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabsTop);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.Gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.Photo));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_camera, container, false);

        if(checkPermissionArray(Permissions.PERMISSIONS)){
            setupViewPager(view);
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
