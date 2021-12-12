package com.example.l3.Listeners;

import android.os.Build;
import android.os.Handler;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class DoubleClickListener implements View.OnTouchListener {
    LocalDateTime lastClicked = LocalDateTime.now();
    public int lastX = 0, lastY = 0;

    public final float TOUCH_RADIUS_PERCENTAGE = 0.05f;
    public final int DOUBLE_CLICK_DELAY = 400;

    int touched = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LocalDateTime now =  LocalDateTime.now();

        boolean res = false;

        int width = v.getWidth();
        int heigth = v.getHeight();
        int x = (int) event.getX();
        int y = (int) event.getY();

        int dt = now.getSecond() - lastClicked.getSecond();

        float dx = 0, dy = 0;
        float radius = Math.max(width, heigth) * TOUCH_RADIUS_PERCENTAGE;

        touched++;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                touched = 0;
            }
        };

        if (touched == 1) {
            handler.postDelayed(runnable, DOUBLE_CLICK_DELAY);
        }
        if (touched == 2) {
            if (x > width*0.75) {
                onDoubleClickRightSide();
                res = true;
            }
            else if (x < width*0.25) {
                onDoubleClickLeftSide();
                res = true;
            }
        }

        lastX = x;
        lastY = y;
        lastClicked = now;

        return res;

//
//        if (dt < DOUBLECLICK_SECONDS) {
//            dx = Math.abs(x - lastX);
//            dy = Math.abs(y - lastY);
//            if (dx * dx + dy * dy < radius) {
//                if (x > width*0.75) {
//                    onDoubleClickRightSide();
//                    res = true;
//                }
//                else if (x < width*0.25) {
//                    onDoubleClickLeftSide();
//                    res = true;
//                }
//            }
//        }
//
//        lastX = x;
//        lastY = y;
//        return res;
    }

    public abstract void onDoubleClickLeftSide();

    public abstract void onDoubleClickRightSide();
}
