package com.example.l3.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3.Adapters.VideoRecyclerViewAdapter;
import com.example.l3.Models.Video;
import com.example.l3.R;
import com.example.l3.Utils;

import java.util.ArrayList;

public class VideoMediaActivity extends AppCompatActivity {

    static ArrayList<Video> videos = null;
    static VideoRecyclerViewAdapter adapter;

    static int currentVideo = -1;

    RecyclerView rv;
    TextView tvAudioMedia, tvVideoMedia;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_media);

        try {
            setAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            finish();
        }

        if (currentVideo > 0) {
            rv.scrollTo(currentVideo, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAll() throws InterruptedException {
        setFields();
        setListeners();

        Utils.findingVideos.join();
        setAdapter();
    }

    private void setFields() {
        rv = findViewById(R.id.rv);
        tvAudioMedia = findViewById(R.id.tvAudioMedia);
        tvVideoMedia = findViewById(R.id.tvVideoMedia);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        tvAudioMedia.setOnClickListener((v) -> changeMedia());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeMedia() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setAdapter() {
        adapter = new VideoRecyclerViewAdapter(this, videos);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener((View v, int position) -> {
            playVideo(position);
        });
    }

    private void playVideo(int position) {
        currentVideo = position;

        Intent intent = new Intent(this, DedicatedVideoActivity.class);
        intent.putExtra("videoPath", videos.get(position).getPath());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setVideos() {
        if (videos == null) {
//            videos = Utils.getMedia(Environment.getExternalStorageDirectory().getAbsolutePath(), Utils.MP4);
            videos = Utils.getMedia(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).getAbsolutePath(),
                Utils.MP4
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        changeMedia();
    }
}