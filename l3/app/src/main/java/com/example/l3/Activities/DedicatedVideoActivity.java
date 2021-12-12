package com.example.l3.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.l3.Listeners.DoubleClickListener;
import com.example.l3.R;

import java.util.concurrent.locks.ReentrantLock;

public class DedicatedVideoActivity extends AppCompatActivity {

    static MediaController mediaController;

    VideoView vv;
    RelativeLayout rlVVRight, rlVVLeft;

    static String videoPath;
    static final int doubleClickOffset = 5_000;
    static final ReentrantLock locker = new ReentrantLock();
    static int currentPosition = 0;
    static Context context;
    static Toast toastRigth;
    static Toast toastLeft;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        currentPosition = vv.getCurrentPosition();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        vv.seekTo(currentPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dedicated_video);

        setAll();
    }

    private void setAll() {
        setFields();
        setListeners();
        vv.start();
    }

    private void setFields() {
        context = this.getBaseContext();

        toastRigth = Toast.makeText(context, "+" + doubleClickOffset/1000 + " sec", Toast.LENGTH_SHORT);
        toastLeft = Toast.makeText(context, "-" + doubleClickOffset/1000 + " sec", Toast.LENGTH_SHORT);

        videoPath = getIntent().getExtras().getString("videoPath");
        vv = findViewById(R.id.vv);
        rlVVRight = findViewById(R.id.rlVVRight);
        rlVVLeft = findViewById(R.id.rlVVLeft);;

        vv.setVideoPath(videoPath);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);
    }

    private void setListeners() {
        vv.setOnTouchListener(new DoubleClickListener() {
            @Override
            public void onDoubleClickLeftSide() {
                toastLeft.cancel();
                toastRigth.cancel();
                if (locker.isLocked()) {
                    return;
                }
                locker.lock();
                try {
                    int currentPos = vv.getCurrentPosition();
                    int seekTo = currentPos - doubleClickOffset;
                    if (seekTo < 0) {
                        seekTo = 0;
                    }
                    vv.seekTo(seekTo);
                }
                finally {
                    locker.unlock();
                    toastLeft.show();
                }
            }

            @Override
            public void onDoubleClickRightSide() {
                toastLeft.cancel();
                toastRigth.cancel();
                if (locker.isLocked()) {
                    return;
                }
                locker.lock();
                try {
                    int currentPos = vv.getCurrentPosition();
                    int seekTo = currentPos + doubleClickOffset;
                    int duration = vv.getDuration();
                    if (seekTo > duration) {
                        seekTo = duration;
                    }
                    vv.seekTo(seekTo);
                }
                finally {
                    locker.unlock();
                    toastRigth.show();
                }
            }
        });
    }

    int i = 0;

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        startVideoMediaActivity();
    }

    private void startVideoMediaActivity() {
        Intent intent = new Intent(this, VideoMediaActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        currentPosition = vv.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        vv.seekTo(currentPosition);
        vv.start();
    }
}
