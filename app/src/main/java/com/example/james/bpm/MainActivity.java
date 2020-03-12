package com.example.james.bpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Song song;

    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "7f5cfb2a85b145608e657b2a9bf88da7";
    private static final String REDIRECT_URI = "com.example.james.test://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private SongService songService;
    private ImageView playlistImage;
    private TextView playlistName, playlistOwner;
    private ArrayList<Song> recentlyPlayedTracks;
    private ArrayList<Playlist> mplaylists = new ArrayList<>();
    private ListView playlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songService = new SongService(getApplicationContext());
        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);


        getPlaylists();


    }

    private void getPlaylists(){
        songService.getUserPlaylists(() -> {
            this.mplaylists = songService.getPlaylists();
            Log.i("Playlist size 1",mplaylists.size()+"");
            playlistView = findViewById(R.id.PlaylistListView);
            PlaylistAdapter adapter = new PlaylistAdapter(this, R.layout.adapter_view_layout, mplaylists);
            playlistView.setAdapter(adapter);
            playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, SongListActivity.class);
                    intent.putExtra("PlaylistID",mplaylists.get(position).getId());
                    startActivity(intent);
                }
            });
        });
    }



}
