package com.example.james.bpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity {

    SongService songService;
    private ArrayList<Song> playlistSongs;
    private ListView songList;
    private String playlistID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        playlistID = getIntent().getExtras().getString("PlaylistID");

        songService = new SongService(getApplicationContext());
        retrieveSongs();
    }

    public void retrieveSongs(){
        songService.getPlaylistSongs(() -> {
            this.playlistSongs = songService.getSongs();
            Log.i("Playlist size 1",playlistSongs.size()+"");
            songList = findViewById(R.id.SongListView);
            SongListAdapter adapter = new SongListAdapter(this, R.layout.adapter_view_layout, playlistSongs);
            songList.setAdapter(adapter);
            songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SongListActivity.this, OnBoardingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ChosenPlaylist",playlistSongs);
                    bundle.putSerializable("ChosenSong", playlistSongs.get(position));
                    intent.putExtras(bundle);
                    intent.putExtra("SongPosition", position);
                    startActivity(intent);
                }
            });
        },playlistID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


}
