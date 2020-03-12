package com.example.james.bpm;


public class AudioFeatures {

    private double danceability;
    private double energy;
    private double tempo;
    private String id;
    private String uri;
    private long duration_ms;


    public AudioFeatures(double danceability, double energy, double tempo, String id, String uri,long duration_ms) {
        this.danceability = danceability;
        this.energy = energy;
        this.tempo = tempo;
        this.id = id;
        this.uri = uri;
        this.duration_ms = duration_ms;
    }

    public double getDanceability() {
        return danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public double getTempo() {
        return tempo;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public long getDuration_ms() {
        return duration_ms;
    }



}
