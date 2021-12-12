package com.example.l3.Listeners;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class SwipeGestureListener implements GestureDetector.OnGestureListener{

    private static final int SWIPE_MIN_DISTANCE = 150;
    private static final int SWIPE_MAX_OFF_PATH = 100;

    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private final Activity activity;
    protected MotionEvent mLastOnDownEvent = null;

    public SwipeGestureListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastOnDownEvent = e;
        System.err.println("ondown");
        //System.out.println(e);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.err.println("onFling");
        System.out.println(e1 + " " + e2);

        if (e1 == null) {
            if (Math.abs(velocityY) > Math.abs(velocityX)) {
                return false;
            }

            if (velocityX > 0) {
                onLeftSwipe();
            } else {
                onRightSwipe();
            }
            return true;
        }

        float dX = e2.getX() - e1.getX();
        float dY = e2.getY() - e1.getY();

        if (Math.abs(dY) < SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

            if (dX > 0) {
                onRightSwipe();
            } else {
                onLeftSwipe();
            }

            return true;
        }

        return false;
    }

    public abstract void onRightSwipe();

    public abstract void onLeftSwipe();
}