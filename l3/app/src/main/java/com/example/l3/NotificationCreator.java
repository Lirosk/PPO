package com.example.l3;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.l3.Models.Audio;
import com.example.l3.Services.NotificationBroadcastReceiverService;

public class NotificationCreator {
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String TAG = "notificationtag";

    public static Notification notification;

    public static void createNotification(Context context, Audio audio, int imagePlay, boolean ongoing) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, TAG);

            Bitmap image = audio.getImage();//BitmapFactory.decodeResource(context.getResources(), audio.getImage());
            if (image == null) {
                image = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
            }

            PendingIntent pendingIntentPrevious;
            int imagePrev = R.drawable.ic_skip_previous_black_24dp;
            ;

            Intent intentPrevious = new Intent(context, NotificationBroadcastReceiverService.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentPlay = new Intent(context, NotificationBroadcastReceiverService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);


            PendingIntent pendingIntentNext;
            int imageNext = R.drawable.ic_skip_next_black_24dp;
            ;
            Intent intentNext = new Intent(context, NotificationBroadcastReceiverService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            //create notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_music)
                    .setOngoing(ongoing)
                    .setContentTitle(audio.getTitle())
                    .setContentText(audio.getArtist())
                    .setLargeIcon(image)
                    .setOnlyAlertOnce(false)//.setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .addAction(imagePrev, "Previous", pendingIntentPrevious)
                    .addAction(imagePlay, "Play", pendingIntentPlay)
                    .addAction(imageNext, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}