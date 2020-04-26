package com.example.james.bpm;

import android.os.Environment;

public class FilePaths {

    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String PICTURES = ROOT_DIR + "Pictures";
}
