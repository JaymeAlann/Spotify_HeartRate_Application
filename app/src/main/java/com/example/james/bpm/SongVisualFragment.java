package com.example.james.bpm;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class SongVisualFragment extends Fragment {

    private ImageView songImage;
    private BroadcastReceiver myReceiver;
    private static SongVisualFragment mSongVisualFragment;

    public static SongVisualFragment newInstance() {
        SongVisualFragment fragment = new SongVisualFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onResume();
        IntentFilter filter = new IntentFilter("android.intent.action.IMAGE_RECEIVED");
        myReceiver = new FragmentSongImageReceiver();
        Log.i("BROADCAST INITIATED: ", "BROADCAST");
        getActivity().registerReceiver(myReceiver,filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_song_visual, container, false);
        mSongVisualFragment = this;
        return fragView;
    }

    public static SongVisualFragment getInstance(){
        return mSongVisualFragment;
    }

    public void updateTheImageView(final String url) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                songImage = getView().findViewById(R.id.SongImageView);
                loadImageFromUrl(url, songImage);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("android.intent.action.IMAGE_RECEIVED");
        getActivity().registerReceiver(myReceiver,filter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(myReceiver);
        super.onPause();
    }

    private void loadImageFromUrl(String url, ImageView holder){
        Picasso.with(this.getContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
