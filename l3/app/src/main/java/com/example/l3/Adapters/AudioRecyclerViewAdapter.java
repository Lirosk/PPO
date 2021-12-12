package com.example.l3.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l3.Activities.MainActivity;
import com.example.l3.Models.Audio;
import com.example.l3.Listeners.OnItemClickListener;
import com.example.l3.R;

import java.util.ArrayList;

public class AudioRecyclerViewAdapter extends RecyclerView.Adapter<AudioRecyclerViewAdapter.ViewHolder> {
    Context context;
    public ArrayList<Audio> audios;
    public OnItemClickListener onItemClickListener;

    public ArrayList<Audio> getAudios() {
        return audios;
    }

    public AudioRecyclerViewAdapter(Context context, ArrayList<Audio> audios) {
        this.context = context;
        this.audios = audios;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewDetachedFromWindow(@NonNull AudioRecyclerViewAdapter.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (MainActivity.currentAudio == holder.getAdapterPosition()) {
            holder.rlAudio.setBackgroundColor(context.getColor(R.color.non_plying_background));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewAttachedToWindow(@NonNull AudioRecyclerViewAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (MainActivity.currentAudio == holder.getAdapterPosition()) {
            holder.rlAudio.setBackgroundColor(context.getColor(R.color.plying_background));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Audio audio = audios.get(position);
        holder.tvTitle.setText((CharSequence) audio.getTitle());
        holder.tvArtist.setText((CharSequence) audio.getArtist());
        Bitmap audioImage = audios.get(position).getImage();
        if (audioImage != null) {
            holder.image.setImageBitmap(audioImage);
        }
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvArtist;
        public ImageView image;
        public RelativeLayout rlAudio;
        public ImageView ivDelete;
        public ImageView ivEdit;

        public ViewHolder(View view) {
            super(view);
            rlAudio = view.findViewById(R.id.rlAudio);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvArtist = view.findViewById(R.id.tvArtist);
            image = view.findViewById(R.id.image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
