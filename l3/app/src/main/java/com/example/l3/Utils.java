package com.example.l3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.l3.Models.Audio;
import com.example.l3.Models.Media;
import com.example.l3.Models.Video;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.*;

public class Utils {
    public static MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

    public static final String MP3 = ".mp3";
    public static final String MP4 = ".mp4";

    public static Thread findingAudios = null;
    public static Thread findingVideos = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static<T extends Media> ArrayList<T> getMedia(String rootPath, String extension) {
        ArrayList<T> fileList = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            if (files != null)
            for (File file : files) {
                if (file.isDirectory() || file.getName().endsWith(".img")) {
                    ArrayList<T> res = getMedia(file.getAbsolutePath(), extension);
                    if (res != null) {
                        fileList.addAll(res);
                    }
                } else if (file.getName().endsWith(extension)) {
                    T media = null;
                    switch (extension) {
                        case Utils.MP3:
                            media = (T) processMP3(file);
                            break;
                        case Utils.MP4:
                            media = (T) processMP4(file);
                            break;
                    }
                    if (media != null) {
                        fileList.add(media);
                    }
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<File> findDirectories(File[] files) {
        ArrayList<File> res = new ArrayList<>();

        for (File file: files) {
            if (file.isDirectory()) {
                res.add(file);
            }
        }

        return res;
    }

    private static ArrayList<File> findMedia(File[] files, String extension) {
        ArrayList<File> res = new ArrayList<>();
        String fileName;

        for (File file: files) {
            fileName = file.getName();
            if (fileName.endsWith(extension)) {
                res.add(file);
            }
        }

        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static<T extends Media> T processMedia(File file, String extension) {
        if (extension.equals(MP3)) {
            return (T) processMP3(file);
        }
        else if (extension.equals(MP4)){
            return (T) processMP4(file);
        }
        return null;
    }

    private static Video processMP4(File file) {
        Video video = new Video();

        String fileName = file.getName();
        String title = fileName;
        Pattern pattern = Pattern.compile("^(.+)\\.mp4$");
        Matcher matcher = pattern.matcher(fileName);

        try {
            video.setImage(
                    ThumbnailUtils.createVideoThumbnail(
                            file.getAbsolutePath(),
                            MediaStore.Video.Thumbnails.MICRO_KIND
                    )
            );
        }
        catch (Exception e) {
            video.setImage(null);
        }

        if (matcher.matches()) {
            title = matcher.group(1);
        }

        long dateVal = file.lastModified();
        Date date = new Date(dateVal);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateString = sdf.format(date);
        String path = file.getAbsolutePath();

        mediaMetadataRetriever.setDataSource(path);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = timerConversion(Long.parseLong(duration));

        video.setDuration(duration);
        video.setDate(dateString);
        video.setTitle(title);
        video.setPath(path);
        return video;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Audio processMP3(File file) {
        Audio audio = new Audio();

        try {
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());

            byte[] artByte = mediaMetadataRetriever.getEmbeddedPicture();
            Bitmap image = null;
            if (artByte != null){
                image = BitmapFactory.decodeByteArray(artByte, 0, artByte.length);
            }

            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            // in the face of "Viber Message.mp3"
            if (Integer.parseInt(duration) < 10_000) {
                return null;
            }

            if (artist == null && title == null) {
                HashMap<String, String> parsed = parseAudioMetadataFromFileName(file.getName());

                artist = parsed.get("artist");
                title = parsed.get("title");
            }
            else {
                if (artist == null) {
                    artist = "unknown";
                }
                if (title == null) {
                    title = file.getName();
                }
            }

            audio.setPath(file.getAbsolutePath());
            audio.setArtist(artist);
            audio.setDuration(duration);
            audio.setTitle(title);
            audio.setImage(image);

            return audio;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static HashMap<String, String> parseAudioMetadataFromFileName(String fileName) {
        HashMap<String, String> res = new HashMap<>();
        ///storage/emulated/0/Download/Motoi Sakuraba - A Moment's Peace.mp3
        Pattern p = Pattern.compile("^\\s*(?<artist>.*)\\s+?-\\s+?(?<title>.*)(?:\\.mp3)+?$");
        Matcher m = p.matcher(fileName);

        res.put("artist", "unknown");
        res.put("title", fileName.substring(0, fileName.length() - MP3.length()));

        if (m.matches()) {
            // no m.group("artist")
            String artist = m.group(1);
            String title = m.group(2);

            res.put("artist", artist);
            res.put("title", title);
        }
        return res;
    }

    public static String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }

    @Override
    protected void finalize() throws Throwable {
        mediaMetadataRetriever.release();
        super.finalize();
    }
}
