package com.example.james.bpm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private PlaylistOwner owner;
    private String ownerName;
    @SerializedName("images")
    private List<TrackImage> playlistImage;
    private ArrayList<Song> songs = new ArrayList<>();

    public Playlist(String id, String name, PlaylistOwner owner, List<TrackImage> playlistImage) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.playlistImage = playlistImage;
    }

    public String getId() {return id; }
    public String getName() {
        return name;
    }
    public PlaylistOwner getOwner() { return owner; }
    public List<TrackImage> getPlaylistImage() { return playlistImage; }
    public ArrayList<Song> getSongs() { return songs;}

    public class PlaylistOwner{
        @SerializedName("display_name")
        private String playlistOwnerName;
        @SerializedName("id")
        private String playlistOwnerID;
        @SerializedName("uri")
        private String playlistOwnderUri;

        public PlaylistOwner(String playlistOwnerName, String playlistOwnerID, String playlistOwnderUri) {
            this.playlistOwnerName = playlistOwnerName;
            this.playlistOwnerID = playlistOwnerID;
            this.playlistOwnderUri = playlistOwnderUri;
        }

        public String getPlaylistOwnerName() {
            return playlistOwnerName;
        }

        public String getPlaylistOwnerID() {
            return playlistOwnerID;
        }

        public String getPlaylistOwnderUri() {
            return playlistOwnderUri;
        }
    }
}
