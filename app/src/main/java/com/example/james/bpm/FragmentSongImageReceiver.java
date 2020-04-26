package com.example.james.bpm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FragmentSongImageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String url = intent.getStringExtra("ImageURL");
        try {
            SongVisualFragment.getInstance().updateTheImageView(url);
        } catch (Exception e) {

        }
    }
}
