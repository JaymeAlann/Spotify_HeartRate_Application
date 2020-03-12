package com.example.james.bpm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.james.bpm.OnBoardingActivity;

public class SongPlayerDataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String sName = intent.getStringExtra("SongTitle");
        String sArtist = intent.getStringExtra("SongArtist");
        long sDuration = intent.getLongExtra("SongDuration_MS",0);

        try {
            OnBoardingActivity.getInstance().updateTextViews(sName,sArtist, sDuration);
        } catch (Exception e) {

        }
    }
}
