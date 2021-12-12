package com.example.l3.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l3.Models.Video;
import com.example.l3.Listeners.OnItemClickListener;
import com.example.l3.R;

import java.util.ArrayList;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.ViewHolder>{
    Context context;
    ArrayList<Video> videos;
    public OnItemClickListener onItemClickListener;

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public VideoRecyclerViewAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewDetachedFromWindow(@NonNull VideoRecyclerViewAdapter.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewAttachedToWindow(@NonNull VideoRecyclerViewAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @NonNull
    @Override
    public VideoRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        return new VideoRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRecyclerViewAdapter.ViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.tvDuration.setText(video.getDuration());
        holder.tvTitle.setText(video.getTitle());
        holder.tvDate.setText(video.getDate());

        Bitmap image = video.getImage();
        if (image == null) {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_video);
        }
        holder.image.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvDuration;
        public TextView tvDate;
        public ImageView image;
        public ConstraintLayout clVideo;

        public ViewHolder(View view) {
            super(view);
            clVideo = view.findViewById(R.id.clVideo);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDuration = view.findViewById(R.id.tvDuration);
            tvDate = view.findViewById(R.id.tvDate);
            image = view.findViewById(R.id.image);

            view.setOnClickListener((View v) -> onItemClickListener.onItemClick(v, getAdapterPosition()));
        }
    }
}
