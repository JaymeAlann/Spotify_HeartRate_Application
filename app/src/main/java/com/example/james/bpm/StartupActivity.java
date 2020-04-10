package com.example.james.bpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class StartupActivity extends AppCompatActivity {

    Animation topAnim, botAnim, beatAnim;
    ImageView logo, logoHeart;
    TextView appTitle, appSubTitle;
    Handler handler;
    Timer timer;
    MediaPlayer soundBite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beatAnim = AnimationUtils.loadAnimation(StartupActivity.this,R.anim.beat_pulse_animation);
                soundBite = MediaPlayer.create(StartupActivity.this,R.raw.heartbeat);
                logoHeart = findViewById(R.id.Heart);
                logoHeart.setVisibility(View.VISIBLE);
                logoHeart.setAnimation(beatAnim);
                soundBite.start();
            }
        },2000);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(StartupActivity.this, SpotifyAuthenticationActivity.class);
                startActivity(intent);
            }
        },5500);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_down_animation);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.botton_up_animation);

        logo = findViewById(R.id.HeadphoneHeart);
        appTitle = findViewById(R.id.BPMBeats);
        appSubTitle = findViewById(R.id.poweredBy);

        logo.setAnimation(topAnim);
        appTitle.setAnimation(botAnim);
        appSubTitle.setAnimation(botAnim);
    }
}
