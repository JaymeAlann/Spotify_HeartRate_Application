package com.example.james.bpm;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.http.GET;

public class OnBoardingActivity extends FragmentActivity {

    private final String TAG = "ONBOARD ACTIVITY: ";
    SongService songService;
    private PlayerService mPlayerService;
    MyFragmentPageAdapter myFragmentPageAdapter;
    private BroadcastReceiver songPlayerDataReceiver;
    private static OnBoardingActivity mOnBoardingActivity;
    private boolean isPlaying = true;
    private  boolean shuffleBool = true;
    private boolean isBound;
    private ImageView shuffle;
    private long minutesDown, minutesUp = 0;
    private long secondsDown, secondsUp = 0;
    ImageButton previousBTN, playPauseBTN, nextBTN;
    private SeekBar songProgressBar;
    TextView songTitle, songArtist, songProgressPositive, songProgressNegative, songTempo;
    Intent playerServiceIntent;
    ServiceConnection myServiceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle passedBundle = getIntent().getExtras();
        mOnBoardingActivity = this;
        songPlayerDataReceiver = new SongPlayerDataReceiver();
        setContentView(R.layout.activity_on_boarding);

        isBound=false;

        songService = new SongService(getApplicationContext());
        shuffle = findViewById(R.id.shuffleButton);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!shuffleBool){
                    shuffle.setImageResource(R.drawable.shuffle_green);
                    shuffleBool = true;
                }
                else{
                    shuffle.setImageResource(R.drawable.shuffle_white);
                    shuffleBool = false;
                }
            }
        });

        playerServiceIntent = new Intent(OnBoardingActivity.this, PlayerService.class);
        Bundle serviceBundle = new Bundle();
        assert passedBundle != null;
        serviceBundle.putSerializable("ChosenPlaylist",(ArrayList<Song>) passedBundle.getSerializable("ChosenPlaylist"));
        serviceBundle.putSerializable("ChosenSong", passedBundle.getSerializable("ChosenSong"));
        playerServiceIntent.putExtras(serviceBundle);
        playerServiceIntent.putExtra("SongPosition",getIntent().getIntExtra("SongPosition",0));
        startService(playerServiceIntent);

        if(!isBound){
            bindMyService();
            Intent intent = new Intent(OnBoardingActivity.this, PlayerService.class);
            bindService(intent, myServiceConnection, BIND_AUTO_CREATE);
            isBound = true;
        }


        // INITIATE IMAGE BUTTONS FOR CONTROLLING PLAYBACK
        previousBTN = findViewById(R.id.previousBTN);
        nextBTN = findViewById(R.id.skipBTN);
        previousBTN.setOnClickListener(v -> {
            if(isBound){
                mPlayerService.getPreviousSongForQueue();
                songProgressBar.setProgress(0);
                playPauseBTN.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying=true;
            }
        });
        nextBTN.setOnClickListener(v -> {
            if(isBound){
                mPlayerService.getNextSongForQueue();
                songProgressBar.setProgress(0);
                playPauseBTN.setImageResource(android.R.drawable.ic_media_pause);
                isPlaying=true;
            }
        });


        List<Fragment> fragments = getFragments();
        myFragmentPageAdapter = new MyFragmentPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = findViewById(R.id.fragmentViewPager);
        pager.setAdapter(myFragmentPageAdapter);

        playPauseBTN = findViewById(R.id.playPauseBTN);
        playPauseBTN.setOnClickListener(v -> {
            if(isPlaying && isBound){
                mPlayerService.pauseSpotifyRemote();
                isPlaying=false;
                playPauseBTN.setImageResource(android.R.drawable.ic_media_play);
            }else if(!isPlaying && isBound){
                mPlayerService.resumeSpotifyRemote();
                isPlaying=true;
                playPauseBTN.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        songProgressBar = findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(isBound && fromUser){
                    mPlayerService.seekToSpotifyRemote(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter myIF = new IntentFilter("android.intent.action.IMAGE_RECEIVED");
        registerReceiver(songPlayerDataReceiver,myIF);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(songPlayerDataReceiver);
    }

    public static OnBoardingActivity getInstance(){
        return mOnBoardingActivity;
    }

    public void updateTextViews(String name, String artist, long songDuration, double tempo){
        OnBoardingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songTitle = findViewById(R.id.songTitle);
                songTitle.setText(name);
                songArtist = findViewById(R.id.artistName);
                songArtist.setText(artist);
                songTempo = findViewById(R.id.songTempo);
                songTempo.setText("BPM: "+tempo);


                if(isPlaying){

                    int progress = (int)songDuration;
                    songProgressBar.setMax(progress);
                    songProgressBar.setProgress(songProgressBar.getProgress()+1000);

                    songProgressPositive = findViewById(R.id.songProgress);
                    songProgressNegative = findViewById(R.id.songTimeLeft);
                    // count up timer
                    secondsUp = (songProgressBar.getProgress()/1000) % 60;
                    minutesUp = (songProgressBar.getProgress()/60000);

                    String timeCounter;

                    timeCounter = " " + minutesUp;
                    timeCounter += ":";
                    if(secondsUp <10){timeCounter+="0";}
                    timeCounter += secondsUp;

                    songProgressPositive.setText(timeCounter);

                    minutesDown = TimeUnit.MILLISECONDS.toMinutes(songDuration-songProgressBar.getProgress());
                    secondsDown = TimeUnit.MILLISECONDS.toSeconds(songDuration-songProgressBar.getProgress());

                    String timeLeft;

                    timeLeft = " " + minutesDown;
                    timeLeft += ":";
                    if(secondsDown%60 <10){timeLeft+="0";}
                    timeLeft += secondsDown%60;

                    songProgressNegative.setText(timeLeft);
                }

            }
        });
    }

    private List<Fragment> getFragments(){
        List<Fragment> fragList = new ArrayList<>();
        fragList.add(SongVisualFragment.newInstance());
        fragList.add(HeartRateFragment.newInstance());
        fragList.add(HeartRateFragment.newInstance());
        return fragList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(playerServiceIntent);
        unbindService(myServiceConnection);
        this.finish();
    }


    private void bindMyService(){
        if(myServiceConnection==null){
            myServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    PlayerService.myBinder binder =(PlayerService.myBinder)service;
                    mPlayerService = binder.getService();
                    isBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isBound = false;
                }
            };
        }
    }
}
