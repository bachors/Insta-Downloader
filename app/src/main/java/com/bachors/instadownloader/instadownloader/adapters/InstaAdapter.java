package com.bachors.instadownloader.instadownloader.adapters;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bachors.instadownloader.R;
import com.bachors.instadownloader.instadownloader.models.Media;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */

public class InstaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = InstaAdapter.class.getSimpleName();

    private List<Media> media;
    private Context inContext;
    private ProgressDialog inDialog;
    private String inDir;

    private DownloadManager downloadManager;
    private long Image_DownloadId, Video_DownloadId;
    private Uri inTmp;
    private String inTenTipe, inFileName;

    public InstaAdapter(Context context) {
        this.inContext = context;
        this.media = new ArrayList<>();

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        inContext.registerReceiver(downloadReceiver, filter);
    }

    public void setDir(String dir) {
        this.inDir = dir;
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
        View v1 = inflater.inflate(R.layout.insta_downloader, parent, false);
        viewHolder = new InstaVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Media m = media.get(position);

        final InstaVH inVH = (InstaVH) holder;

        if(m.getType().equals("video")){
            Glide.with(inContext).load("").placeholder(R.drawable.thumbnail).into(inVH.hImage);
            inVH.hVideoImg.setVisibility(View.VISIBLE);
            inVH.hVideoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inVH.hVideoImg.setVisibility(View.GONE);
                    inVH.hVideo.setVisibility(View.VISIBLE);
                    inVH.hVideo.setVideoPath(m.getVideo());
                    inVH.hVideo.setMediaController(new MediaController(inContext));
                    inVH.hVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            inVH.hVideo.start();
                        }
                    });
                }
            });
        }else{
            inVH.hVideo.setVisibility(View.GONE);
            inVH.hVideoImg.setVisibility(View.GONE);
            Glide.with(inContext).load(m.getImage()).into(inVH.hImage);
        }

        inVH.hDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inDialog = new ProgressDialog(inContext);
                inDialog.setMessage("Please wait...");
                inDialog.setCancelable(false);
                inDialog.show();
                String img = m.getImage();
                String vid = m.getVideo();

                inTenTipe = "download";
                if(vid.equals("")) {
                    inFileName = img.substring(img.lastIndexOf('/') + 1);

                    File sdcard = Environment.getExternalStorageDirectory();
                    File filex = new File(sdcard, inDir + "/" + "IMAGE_" + inFileName);
                    if(filex.exists()) {
                        if (inDialog.isShowing())
                            inDialog.dismiss();

                        Uri uri = Uri.parse("file://" + filex.toString());
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "image/*");
                        inContext.startActivity(i);
                    } else {
                        inTmp = Uri.parse(img);
                        Image_DownloadId = DownloadData(inTmp, "IMAGE_" + inFileName);
                    }
                }else {
                    inFileName = vid.substring(vid.lastIndexOf('/') + 1);

                    File sdcard = Environment.getExternalStorageDirectory();
                    File filex = new File(sdcard, inDir + "/" + "VIDEO_" + inFileName);
                    if(filex.exists()) {
                        if (inDialog.isShowing())
                            inDialog.dismiss();

                        Uri uri = Uri.parse("file://" + filex.toString());
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "video/*");
                        inContext.startActivity(i);
                    } else {
                        inTmp = Uri.parse(vid);
                        Video_DownloadId = DownloadData(inTmp, "VIDEO_" + inFileName);
                    }
                }
            }
        });

        inVH.hRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inDialog = new ProgressDialog(inContext);
                inDialog.setMessage("Please wait...");
                inDialog.setCancelable(false);
                inDialog.show();
                String img = m.getImage();
                String vid = m.getVideo();
                inTenTipe = "repost";
                if(vid.equals("")) {
                    inFileName = img.substring(img.lastIndexOf('/') + 1);

                    File sdcard = Environment.getExternalStorageDirectory();
                    File filex = new File(sdcard, inDir + "/" + "IMAGE_" + inFileName);
                    if(filex.exists()) {
                        if (inDialog.isShowing())
                            inDialog.dismiss();

                        Uri uri = Uri.parse("file://" + filex.toString());
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("image/*");
                        inContext.startActivity(i);
                    } else {
                        inTmp = Uri.parse(img);
                        Image_DownloadId = DownloadData(inTmp, "IMAGE_" + inFileName);
                    }
                }else {
                    inFileName = vid.substring(vid.lastIndexOf('/') + 1);

                    File sdcard = Environment.getExternalStorageDirectory();
                    File filex = new File(sdcard, inDir + "/" + "VIDEO_" + inFileName);
                    if(filex.exists()) {
                        if (inDialog.isShowing())
                            inDialog.dismiss();

                        Uri uri = Uri.parse("file://" + filex.toString());
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("video/*");
                        inContext.startActivity(i);
                    } else {
                        inTmp = Uri.parse(vid);
                        Video_DownloadId = DownloadData(inTmp, "VIDEO_" + inFileName);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return media == null ? 0 : media.size();
    }

    private void add(Media r) {
        media.add(r);
        notifyItemInserted(media.size() - 1);
    }

    public void addAll(List<Media> media) {
        for (Media m : media) {
            add(m);
        }
    }

    private void remove(Media r) {
        int position = media.indexOf(r);
        if (position > -1) {
            media.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem());
        }
    }

    private Media getItem() {
        return media.get(0);
    }

    private class InstaVH extends RecyclerView.ViewHolder {
        private ImageView hImage;
        private ImageView hRepost;
        private ImageView hDownload;
        private ImageView hVideoImg;
        private VideoView hVideo;

        private InstaVH(View itemView) {
            super(itemView);

            hImage = (ImageView) itemView.findViewById(R.id.image);
            hRepost = (ImageView) itemView.findViewById(R.id.repost);
            hDownload = (ImageView) itemView.findViewById(R.id.download);
            hVideoImg = (ImageView) itemView.findViewById(R.id.imagevideo);
            hVideo = (VideoView) itemView.findViewById(R.id.pvideo);
        }
    }


    // download manager
    private long DownloadData (Uri uri, String fileName) {

        long downloadReference;

        downloadManager = (DownloadManager)inContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(TAG);

        //Setting description of request
        request.setDescription("Start...");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(inDir, fileName);

        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == Image_DownloadId) {

                DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
                //set the query filter to our previously Enqueued download
                ImageDownloadQuery.setFilterById(Image_DownloadId);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = downloadManager.query(ImageDownloadQuery);
                if(cursor.moveToFirst()){
                    int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    String filename = cursor.getString(filenameIndex);
                    Uri uri = Uri.parse("file://" + filename);

                    Intent i = new Intent();
                    if(inTenTipe.equals("repost")) {
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("image/*");
                    }else {
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "image/*");
                    }

                    try {
                        if (inDialog.isShowing())
                            inDialog.dismiss();
                        inContext.startActivity(i);
                    } catch (android.content.ActivityNotFoundException ex) {
                        File file = new File(filename);
                        boolean deleted = file.delete();
                        Image_DownloadId = DownloadData(inTmp, "IMAGE_" + inFileName);
                    }

                }

            }
            else if(referenceId == Video_DownloadId) {

                DownloadManager.Query VideoDownloadQuery = new DownloadManager.Query();
                //set the query filter to our previously Enqueued download
                VideoDownloadQuery.setFilterById(Video_DownloadId);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = downloadManager.query(VideoDownloadQuery);
                if(cursor.moveToFirst()){
                    int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    String filename = cursor.getString(filenameIndex);
                    Intent i = new Intent();
                    Uri uri = Uri.parse("file://" + filename);
                    if(inTenTipe.equals("repost")) {
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("video/*");
                    }else {
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(uri, "video/*");
                    }

                    try {
                        if (inDialog.isShowing())
                            inDialog.dismiss();
                        inContext.startActivity(i);
                    } catch (android.content.ActivityNotFoundException ex) {
                        File file = new File(filename);
                        boolean deleted = file.delete();
                        Image_DownloadId = DownloadData(inTmp, "VIDEO_" + inFileName);
                    }
                }
            }

        }
    };

}
