package com.example.l3.Activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l3.Adapters.AudioRecyclerViewAdapter;
import com.example.l3.DataSaver;
import com.example.l3.Models.Audio;
import com.example.l3.NotificationCreator;
import com.example.l3.R;
import com.example.l3.Services.OnClearFromRecentService;
import com.example.l3.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity{

    private final int PERMISSION_READ = 0;

    static ArrayList audios = null;

    RecyclerView rv;
    TextView tvAudioCurrentPosition, tvAudioDuration, tvAudioName, tvAudioMedia, tvVideoMedia;
    ImageView imgPrev, imgNext, imgPause, imgShuffle, imgRepeat;

    SeekBar sb;
    ContentResolver contentResolver;

    AudioRecyclerViewAdapter audioAdapter;

    static Resources resources;
    static MediaPlayer mediaPlayer = new MediaPlayer();

    static NotificationManager notificationManager;

    static boolean shuffled = false, repeatAudio = false;

    static public int currentAudio = -1;
    double audioCurrentPosition, audioDuration;

    private static final int DELAY_MILLIS = 1000;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermisson()) {
            finish();
        }
        try {
            setAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (currentAudio > -1) {
            setAudioProgress();
        }
        createNotification();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setAll() throws InterruptedException {

        Utils.findingAudios = new Thread(MainActivity::setAudios);
        Utils.findingVideos = new Thread(VideoMediaActivity::setVideos);
        Utils.findingAudios.start();
        Utils.findingVideos.setDaemon(true);
        Utils.findingVideos.start();
        setNotifications();
        setFields();
        Utils.findingAudios.join();
        setListeners();
        setAudioAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotifications() {
        createNotificationChannel();
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(this, OnClearFromRecentService.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        mediaPlayer.setOnCompletionListener((MediaPlayer mp) -> playNext());
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioCurrentPosition = seekBar.getProgress();
                mediaPlayer.seekTo((int) audioCurrentPosition);
            }
        });

        if (!audios.isEmpty()) {
            imgPrev.setOnClickListener((View view) -> playPrev());
            imgNext.setOnClickListener((View view) -> playNext());
            imgPause.setOnClickListener((View view) -> playOrPauseAudio());
            imgShuffle.setOnClickListener((View view) -> shuffle());
            imgRepeat.setOnClickListener((View view) -> repeatAudio());
        }

        tvVideoMedia.setOnClickListener((View view) -> changeMedia());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeMedia() {
        Intent intent = new Intent(this, VideoMediaActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setFields() {
        rv = findViewById(R.id.rv);

        tvAudioCurrentPosition = findViewById(R.id.tvAudioCurrentPosition);
        tvAudioDuration = findViewById(R.id.tvAudioDuration);
        tvAudioName = findViewById(R.id.tvAudioName);
        tvAudioName.setText("");
        tvAudioMedia = findViewById(R.id.tvAudioMedia);
        tvVideoMedia = findViewById(R.id.tvVideoMedia);

        imgPrev = findViewById(R.id.imgPrev);
        imgNext = findViewById(R.id.imgNext);
        imgPause = findViewById(R.id.imgPause);
        imgRepeat = findViewById(R.id.imgRepeat);
        imgShuffle = findViewById(R.id.imgShuffle);

        sb = findViewById(R.id.sb);
        contentResolver = getContentResolver();
        resources = getResources();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAudioAdapter() {
        audioAdapter = new AudioRecyclerViewAdapter(this, audios);
        rv.setAdapter(audioAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        audioAdapter.setOnItemClickListener((v, position) -> playAudio(position));
    }

    private void createNotification() {
        if (currentAudio < 0) {
            return;
        }

        boolean ongoing = false;
        int imagePlay = R.drawable.ic_play_circle_filled_black_24dp;
        if (mediaPlayer.isPlaying()) {
            imagePlay = R.drawable.ic_pause_circle_filled_black_24dp;
            ongoing = true;
        }

        NotificationCreator.createNotification(
                this,
                audioAdapter.audios.get(currentAudio),
                imagePlay,
                ongoing
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
            NotificationCreator.CHANNEL_ID,
            "notification",
            NotificationManager.IMPORTANCE_LOW
        );

        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void repeatAudio() {
        if (repeatAudio) {
            mediaPlayer.setOnCompletionListener((MediaPlayer mp) -> playNext());
            repeatAudio = false;
        }
        else {
            mediaPlayer.setOnCompletionListener((MediaPlayer mp) -> {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            });
            repeatAudio = true;
        }
        setImgRepeat();
    }

    private void setImgRepeat() {
        if (repeatAudio) {
            imgRepeat.setImageResource(R.drawable.ic_repeat_on_icon);
        }
        else {
            imgRepeat.setImageResource(R.drawable.ic_repeat_off_icon);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void shuffle() {
        setNonPlayingAudioBackground(currentAudio);
        int newTrueCurrentAudio;

        if (shuffled) {
            newTrueCurrentAudio = audios.indexOf(audioAdapter.audios.get(currentAudio));
//            adapter = new AudioRecyclerViewAdapter(this, audios);
            audioAdapter.audios = audios;
            shuffled = false;
        }
        else {
            ArrayList<Audio> shuffledAudios = new ArrayList<Audio>(audios);
            Collections.shuffle(shuffledAudios);
            newTrueCurrentAudio = shuffledAudios.indexOf(audios.get(currentAudio));
            //adapter = new AudioRecyclerViewAdapter(this, shuffledAudios);
            audioAdapter.audios = shuffledAudios;
            shuffled = true;
        }
        currentAudio = newTrueCurrentAudio;
        if (mediaPlayer.isPlaying()) {
            setPlayingAudioBackground(currentAudio);
        }
        rv.setAdapter(audioAdapter);
        setImgShuffle();
    }

    private void setImgShuffle() {
        if (shuffled) {
            imgShuffle.setImageResource(R.drawable.ic_shuffle_on_icon);
        }
        else {
            imgShuffle.setImageResource(R.drawable.ic_shuffle_off_icon);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playOrPauseAudio() {
        if (currentAudio < 0) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

            setNonPlayingAudioBackground(currentAudio);
        }
        else {
            mediaPlayer.start();
            imgPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);

            setPlayingAudioBackground(currentAudio);
        }
        createNotification();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playNext() {
        if (currentAudio < 0) {
            return;
        }

        setNonPlayingAudioBackground(currentAudio);
        int position = currentAudio;
        if (position < (audioAdapter.audios.size()-1)) {
            position++;
        }
        else {
            position = 0;
        }
        playAudio(position);
        setPlayingAudioBackground(currentAudio);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playPrev() {
        if (currentAudio < 0) {
            return;
        }

        setNonPlayingAudioBackground(currentAudio);
        int position = currentAudio;
        if (position > 0) {
            position--;
        }
        else {
            position = audioAdapter.audios.size() - 1;
        }
        playAudio(position);
        setPlayingAudioBackground(currentAudio);
    }

    private void setPlayingAudioBackground(int position) {
        if (position < 0){
            return;
        }

        AudioRecyclerViewAdapter.ViewHolder viewHolder;
        viewHolder = (AudioRecyclerViewAdapter.ViewHolder) rv.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            viewHolder.rlAudio.setBackgroundColor(resources.getColor(R.color.plying_background));
        }
    }

    private void setNonPlayingAudioBackground(int position) {
        if (position < 0){
            return;
        }
        AudioRecyclerViewAdapter.ViewHolder viewHolder;
        viewHolder = (AudioRecyclerViewAdapter.ViewHolder) rv.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            viewHolder.rlAudio.setBackgroundColor(resources.getColor(R.color.non_plying_background));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playAudio(int position) {
        setNonPlayingAudioBackground(currentAudio);

        if (currentAudio != position) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioAdapter.audios.get(position).getPath());
                mediaPlayer.prepare();

                tvAudioName.setText(audioAdapter.audios.get(position).getTitle());

                currentAudio = position;

                setAudioProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playOrPauseAudio();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setAudioProgress() {
        audioCurrentPosition = mediaPlayer.getCurrentPosition();
        audioDuration = mediaPlayer.getDuration();

        tvAudioCurrentPosition.setText(Utils.timerConversion((long) audioCurrentPosition));
        tvAudioDuration.setText(Utils.timerConversion((long) audioDuration));

        sb.setMax((int) audioDuration);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    audioCurrentPosition = mediaPlayer.getCurrentPosition();
                    tvAudioCurrentPosition.setText(Utils.timerConversion((long) audioCurrentPosition));
                    sb.setProgress((int) audioCurrentPosition);
                    handler.postDelayed(this, DELAY_MILLIS);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, DELAY_MILLIS);
    }

    boolean checkPermisson() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setAudios() {
        if (audios == null) {
            audios = Utils.getMedia(Environment.getExternalStorageDirectory().getAbsolutePath(), Utils.MP3);
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        notificationManager.cancel(1);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getStringExtra("action");
            String action = intent.getExtras().getString("action");
            switch (action) {
                case NotificationCreator.ACTION_NEXT:
                    playNext();
                    break;
                case NotificationCreator.ACTION_PLAY:
                    playOrPauseAudio();
                    break;
                case NotificationCreator.ACTION_PREVIOUS:
                    playPrev();
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        finish();
    }
}