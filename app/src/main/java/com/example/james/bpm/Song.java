package com.example.james.bpm;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Song implements Serializable {

    @SerializedName("id")
    private String songID;

    @SerializedName("name")
    private String songName;

    @SerializedName("type")
    private String songType;

    @SerializedName("uri")
    private String songUri;

    @SerializedName("album")
    private TrackAlbum album;

    private AudioFeatures songAudioFeatures;


    public String getSongID() {
        return songID;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongType() {
        return songType;
    }

    public String getSongUri() {
        return songUri;
    }

    public TrackAlbum getAlbum() {
        return album;
    }

    public AudioFeatures getSongAudioFeatures(){return songAudioFeatures;}

    public void setSongAudioFeatures(AudioFeatures features){songAudioFeatures = features;}

    /**
     * TRACK ALBUM CLASS HOLDS DATA PARSED FROM SPOTIFY API HOLDING INFORMATION
     * ABOUT ALBUM ID, NAME AND URI
     */
    public class TrackAlbum implements Serializable{
         private String album_type;

         @SerializedName("id")
         private String albumID;

         @SerializedName("images")
         private List<TrackImage> albumImages;

         @SerializedName("name")
         private String albumName;

         @SerializedName("uri")
         private String albumUri;

        @SerializedName("artists")
         private List<Artist> albumArtists;

         // GETTERS FOR ALL INFORMATION IN ALBUM TO KEEP THINGS READ ONLY.
        public String getAlbum_type() {
            return album_type;
        }

        public String getAlbumID() {
            return albumID;
        }

        public List<TrackImage> getAlbumImages() {
            return albumImages;
        }

        public String getAlbumName() {
            return albumName;
        }

        public String getAlbumUri() {
            return albumUri;
        }

        public List<Artist> getAlbumArtists() {
            return albumArtists;
        }
    }
    /**
     * TRACK ARTIST CLASS HOLDS DATA PARSED FROM SPOTIFY API HOLDING INFORMATION
     * ABOUT ARTIST ID, NAME AND URI FOR SPECIFIC SONGS
     */
     public class Artist implements Serializable{
         @SerializedName("name")
         private String artistName;

         @SerializedName("type")
         private String artistType;

         @SerializedName("uri")
         private String artistUri;

         @SerializedName("id")
         private String artistID;

         public String getArtistName() {
             return artistName;
         }

         public String getArtistType() {
             return artistType;
         }

         public String getArtistUri() {
             return artistUri;
         }

         public String getArtistID() {
             return artistID;
         }
     }

     public int compareTo(Song song){
         double thisTempo = this.getSongAudioFeatures().getTempo();
         double comparedTempo = song.getSongAudioFeatures().getTempo();

         if(thisTempo >= comparedTempo){
             return 1;
         }else
             return -1;
     }

}