package com.example.james.bpm;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongService {
    private ArrayList<Playlist> playlists= new ArrayList<>();
    private ArrayList<AudioFeatures> songFeatures= new ArrayList<>();
    private ArrayList<Song> playlistTracks = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
    public ArrayList<Song> getSongs() {
        return playlistTracks;
    }
    public ArrayList<AudioFeatures> getAudioFeatures() {
        return songFeatures;
    }

    /**
     *
     * @param callBack
     * @return
     */
    public ArrayList<Playlist> getUserPlaylists(final VolleyCallBack callBack) {
        String endpoint = "\thttps://api.spotify.com/v1/me/playlists?limit=35";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null, response -> {
            Gson gson = new Gson();
            JSONArray jsonArray = response.optJSONArray("items");
            for (int n = 0; n < jsonArray.length(); n++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(n);
                    Playlist playlist = gson.fromJson(object.toString(), Playlist.class);
                    playlists.add(playlist);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callBack.onSuccess();
        }, error -> {
            // TODO: Handle error

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return playlists;
    }

    public ArrayList<Song> getPlaylistSongs(final VolleyCallBack callBack, String id) {
        String endpoint = "https://api.spotify.com/v1/playlists/"+id+"/tracks";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null, response -> {
            Gson gson = new Gson();
            JSONArray jsonArray = response.optJSONArray("items");
            for (int n = 0; n < jsonArray.length(); n++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(n);
                    object = object.optJSONObject("track");
                    Song song = gson.fromJson(object.toString(), Song.class);
                    playlistTracks.add(song);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callBack.onSuccess();
        }, error -> {
            // TODO: Handle error

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return playlistTracks;
    }

    // PLAYING SONGS
    public void playSong(){
        //JSONObject payload = preparePutPayload(id);
        JsonObjectRequest jsonObjectRequest = preparePlaySongRequest();
        queue.add(jsonObjectRequest);
    }

    public void pauseSong(){
        //JSONObject payload = preparePutPayload(id);
        JsonObjectRequest jsonObjectRequest = preparePauseSongRequest();
        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest preparePlaySongRequest(){
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/player/play", null, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JsonObjectRequest preparePauseSongRequest(){
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/player/pause", null, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    public ArrayList<AudioFeatures> getSongsAudioFeatures(final VolleyCallBack callBack, String ids) {
        String endpoint = "https://api.spotify.com/v1/audio-features?ids="+ids;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null, response -> {
            Gson gson = new Gson();
            JSONArray jsonArray = response.optJSONArray("audio_features");
            for (int n = 0; n < jsonArray.length(); n++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(n);
                    AudioFeatures audioFeatures = gson.fromJson(object.toString(), AudioFeatures.class);
                    songFeatures.add(audioFeatures);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callBack.onSuccess();
        }, error -> {
            // TODO: Handle error

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songFeatures;
    }



}