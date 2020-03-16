package com.example.james.bpm;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Track;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerService extends Service {

    private final String TAG = "Player Service: ";
    public static final int GET_RANDOM_NUMBER_FLAG = 0;
    private IBinder myBinder = new myBinder();
    //TESTER HEART RATE (DELETE LATER)
    private long countdown;
    private int currentBPM=105;


    private HeartRateFragment heartRateFragment;
    private boolean isRunning;
    SongService songService;
    private String songIMG, songURI, songName, songArtist;
    private double songTempo;
    private int songPos;
    private ArrayList<Song> chosenPlaylistUnsorted;
    private ArrayList<AudioFeatures> playlistAudioFeatures;
    private ArrayList<Song> previousSongs;
    private long songDuration;

    private static final String CLIENT_ID = "7f5cfb2a85b145608e657b2a9bf88da7";
    private static final String REDIRECT_URI = "com.example.james.bpm://callback";
    private SpotifyAppRemote mSpotifyAppRemote;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning = true;
        songService = new SongService(getApplicationContext());
        Log.i(TAG, "In OnStartCommand Thread ID is "+Thread.currentThread().getId());

        //GET PASSED INFORMATION FOR PLAYER
        Bundle bundle = intent.getExtras();
        chosenPlaylistUnsorted = (ArrayList<Song>)bundle.getSerializable("ChosenPlaylist");
        // Add song to already played list
        previousSongs = new ArrayList<>();
        Song chosenSong = (Song)bundle.getSerializable("ChosenSong");
        previousSongs.add(chosenSong);

        // Set current song information to chosen song.
        songURI = chosenSong.getSongUri();
        songName = chosenSong.getSongName();
        songArtist = chosenSong.getAlbum().getAlbumArtists().get(0).getArtistName();
        songIMG = chosenSong.getAlbum().getAlbumImages().get(0).getUrl();
        songPos = intent.getIntExtra("SongPosition",0);

        setPlayerState();

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setPlayerState(){
        String[] tempArray = new String[chosenPlaylistUnsorted.size()];
        for(int listIndex = 0; listIndex<chosenPlaylistUnsorted.size(); listIndex++){
            tempArray[listIndex] = chosenPlaylistUnsorted.get(listIndex).getSongID();
        }
        String joinedIDs = String.join(",", tempArray);
        songService.getSongsAudioFeatures(() -> {
            playlistAudioFeatures = songService.getAudioFeatures();
            Log.i("AUDIO FEATURES LIST: ",playlistAudioFeatures.size()+"");
            previousSongs.get(0).setSongAudioFeatures(playlistAudioFeatures.get(songPos));
            songTempo = previousSongs.get(0).getSongAudioFeatures().getTempo();
            songDuration = playlistAudioFeatures.get(songPos).getDuration_ms();
            for(AudioFeatures features : playlistAudioFeatures){
                chosenPlaylistUnsorted.get(playlistAudioFeatures.indexOf(features)).setSongAudioFeatures(features);
            }
            chosenPlaylistUnsorted = quickSort(chosenPlaylistUnsorted);
            countdown = songDuration;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startGenerator();
                }
            }
            ).start();
            startMusic();
        }, joinedIDs);
    }

    protected ArrayList<Song> quickSort(ArrayList<Song> list){
        if (list.size() <= 1)
            return list; // Already sorted

        ArrayList<Song> sorted = new ArrayList<Song>();
        ArrayList<Song> lesser = new ArrayList<Song>();
        ArrayList<Song> greater = new ArrayList<Song>();
        Song pivot = list.get(list.size()-1); // Use last Vehicle as pivot
        for (int i = 0; i < list.size()-1; i++)
        {
            //int order = list.get(i).compareTo(pivot);
            if (list.get(i).compareTo(pivot) < 0)
                lesser.add(list.get(i));
            else
                greater.add(list.get(i));
        }

        lesser = quickSort(lesser);
        greater = quickSort(greater);

        lesser.add(pivot);
        lesser.addAll(greater);
        sorted = lesser;

        return sorted;
    }


    private void startMusic(){
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected(songURI);

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }



    private void startGenerator(){
        while(isRunning){
            try {
                Thread.sleep(1000);
                countdown= countdown-1000;
                // when thread reaches zero needs to do the following
                // GET THE HEART_RATE AND GRAB SONG WITH CLOSEST TEMPO
                // UPDATE THE CURRENTLY PLAYING SONG INFORMATION
                if(isRunning){
                    Intent serviceIntent = new Intent();
                    serviceIntent.putExtra("ImageURL", songIMG);
                    serviceIntent.putExtra("SongTitle", songName);
                    serviceIntent.putExtra("SongArtist",songArtist);
                    serviceIntent.putExtra("SongDuration_MS", songDuration);
                    serviceIntent.putExtra("SongTempo",songTempo);
                    serviceIntent.setAction("android.intent.action.IMAGE_RECEIVED");
                    sendBroadcast(serviceIntent);
                    Log.i(TAG, "BROADCAST SENT");
                }
                if(countdown<=3000){
                    getNextSongForQueue();
                }
            }catch(InterruptedException e) {
                Log.i(TAG, "Thread Interrupted.");
            }
        }
    }

    private void queueNextSong(Song nextSong){
        songURI = nextSong.getSongUri();
        songName = nextSong.getSongName();
        songArtist = nextSong.getAlbum().getAlbumArtists().get(0).getArtistName();
        songIMG = nextSong.getAlbum().getAlbumImages().get(0).getUrl();
        songDuration = nextSong.getSongAudioFeatures().getDuration_ms();
        songTempo = nextSong.getSongAudioFeatures().getTempo();

        previousSongs.add(nextSong);
        startMusic();
    }
    private void queuePreviousSong(Song prevSong){
        songURI = prevSong.getSongUri();
        songName = prevSong.getSongName();
        songArtist = prevSong.getAlbum().getAlbumArtists().get(0).getArtistName();
        songIMG = prevSong.getAlbum().getAlbumImages().get(0).getUrl();
        songDuration = prevSong.getSongAudioFeatures().getDuration_ms();
        songTempo = prevSong.getSongAudioFeatures().getTempo();

        chosenPlaylistUnsorted.add(prevSong);
        startMusic();
    }

    public void getNextSongForQueue(){
        if(chosenPlaylistUnsorted.size()== 0){
            return;
        }
        chosenPlaylistUnsorted = quickSort(chosenPlaylistUnsorted);
        int heartRate = 85;
        Iterator<Song> iterator = chosenPlaylistUnsorted.iterator();
        while (iterator.hasNext() && iterator.next().getSongAudioFeatures().getTempo() <= heartRate){
            Log.i(TAG, iterator.next().getSongName()+" Tempo: "+iterator.next().getSongAudioFeatures().getTempo());
        }
       queueNextSong(iterator.next());
        iterator.remove();
    }

    public void getPreviousSongForQueue(){
        if(previousSongs.size()== 0){
            return;
        }
        queuePreviousSong( previousSongs.get(previousSongs.size()-1));
        previousSongs.remove(previousSongs.size()-1);
    }

    private void connected(String id) {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(id);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

    public void pauseSpotifyRemote(){
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resumeSpotifyRemote(){
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    public void seekToSpotifyRemote(long position){
        mSpotifyAppRemote.getPlayerApi().seekTo(position);
    }

    protected void onStop() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void stopServiceGenerator(){isRunning=false;}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service Bound");
        return myBinder;
    }

    public class myBinder extends Binder {
        PlayerService getService(){
            return PlayerService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service UNBound");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServiceGenerator();
        Log.i(TAG, "Service Destroyed.");
    }


}
