package com.example.l3;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.MediaController;

import java.util.HashMap;

public class DataSaver {
    public static HashMap<String, Object> data = new HashMap<>();
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static MediaController mediaController;

    public static void put(String name, Object obj) {
        data.put(name, obj);
    }

    public static Object get(String name) {
        Object res = data.get(name);
        data.remove(name);
        return res;
    }
}
