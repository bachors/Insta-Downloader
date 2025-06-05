package com.bachors.instadownloader.adapter;

import static com.bachors.instadownloader.MainActivity.loading;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bachors.instadownloader.R;
import com.bachors.instadownloader.model.Data;
import com.bachors.instadownloader.util.AndroidDownloader;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InstaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Data> semuaData;
    private final Context inContext;
    private ExoPlayer player = null;
    AndroidDownloader adl;

    public InstaAdapter(Context context) {
        this.inContext = context;
        semuaData = new ArrayList<>();
    }

    public List<Data> getData() {
        return semuaData;
    }

    public void setData(List<Data> semuaData) {
        this.semuaData = semuaData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v = inflater.inflate(R.layout.instagram_list, parent, false);
        viewHolder = new inVH(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Data data = semuaData.get(position);

        final inVH inVH = (inVH) holder;

        adl = new AndroidDownloader(inContext);

        if(data.type.equals("Video")){
            inVH.image.setVisibility(View.GONE);
            inVH.video.setVisibility(View.VISIBLE);

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse(data.url))
                    .setMediaMetadata(new MediaMetadata.Builder().setTitle(data.filename).build())
                    .build();

            player = new ExoPlayer.Builder(inContext).build();
            inVH.video.setPlayer(player);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
        }else{
            inVH.video.setVisibility(View.GONE);
            inVH.image.setVisibility(View.VISIBLE);
            Glide.with(inContext).load(data.url).into(inVH.image);
        }

        inVH.repost.setOnClickListener(view -> {
            File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), data.filename);
            if(filePath.exists()){
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.setType(data.type.toLowerCase() + "/*");
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath.toString()));
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                inContext.startActivity(i);
            }else {
                loading.show();
                adl.downloadFile(data.url, data.filename, data.type.toLowerCase());
            }
        });

    }

    @Override
    public int getItemCount() {
        return semuaData == null ? 0 : semuaData.size();
    }

    public void add(Data r) {
        semuaData.add(r);
        notifyItemInserted(semuaData.size() - 1);
    }

    public void addAll(List<Data> semuaData) {
        for (Data data : semuaData) {
            add(data);
        }
    }

    public void remove(Data r) {
        int position = semuaData.indexOf(r);
        if (position > -1) {
            semuaData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public Data getItem(int position) {
        return semuaData.get(position);
    }

    protected class inVH extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final PlayerView video;
        private final TextView repost;

        public inVH(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);
            repost = itemView.findViewById(R.id.repost);

        }
    }

}