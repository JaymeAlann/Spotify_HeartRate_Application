package com.example.james.bpm;

import java.io.Serializable;

class TrackImage implements Serializable {
    private int height;
    private String url;
    private int width;

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }
}
