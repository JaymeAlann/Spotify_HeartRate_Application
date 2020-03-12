package com.example.james.bpm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistTracks {
    @SerializedName("track")
    private Track playlistTracks;

    public class Track{
        @SerializedName("href")
        private String songUrl;

        @SerializedName("name")
        private String songName;
    }
}
