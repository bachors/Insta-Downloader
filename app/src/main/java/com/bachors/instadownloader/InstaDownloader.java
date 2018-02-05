package com.bachors.instadownloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author Ican Bachors
 * @version 1.0
 * Source: https://github.com/bachors/Insta-Downloader
 */

class InstaDownloader {
    private static final String TAG = InstaDownloader.class.getSimpleName();

    private Context inContext;
    private ProgressDialog inDialog;
    private String inDir;

    private DownloadManager downloadManager;
    private long Image_DownloadId, Video_DownloadId;
    private Uri inTmp;
    private String inTenTipe, inFileName;

    private String inKey, inInput;

    InstaDownloader(Context inContext){
        this.inContext = inContext;

        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        inContext.registerReceiver(downloadReceiver, filter);
    }

    // setting
    void setAccessToken(String s) {
        this.inKey = s;
    }

    void setDir(String s) {
        this.inDir = s;
    }

    void get(String input) {
        inDialog = new ProgressDialog(inContext);
        inDialog.setMessage("Please wait...");
        inDialog.setCancelable(false);
        inDialog.show();

        new InstaUrl().execute(input);
    }

    // callback
    private class Loading implements InstaListener {
        @Override
        public void onResponse(Instagram instagram) {

            final List<Media> media = instagram.getMedia();
            ImageView thumb = (ImageView) ((Activity) inContext).findViewById(R.id.image);
            Glide.with(inContext).load(media.get(0).getImage()).into(thumb);
            CardView cv = (CardView) ((Activity) inContext).findViewById(R.id.card_view);
            cv.setVisibility(View.VISIBLE);

            if (inDialog.isShowing())
                inDialog.dismiss();

            ImageView download = (ImageView) ((Activity) inContext).findViewById(R.id.download);
            ImageView repost = (ImageView) ((Activity) inContext).findViewById(R.id.repost);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inDialog = new ProgressDialog(inContext);
                    inDialog.setMessage("Please wait...");
                    inDialog.setCancelable(false);
                    inDialog.show();
                    String img = media.get(0).getImage();
                    String vid = media.get(0).getVideo();

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
                            i.setAction(Intent.ACTION_VIEW);
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
                            i.setAction(Intent.ACTION_VIEW);
                            i.setDataAndType(uri, "video/*");
                            inContext.startActivity(i);
                        } else {
                            inTmp = Uri.parse(vid);
                            Video_DownloadId = DownloadData(inTmp, "VIDEO_" + inFileName);
                        }
                    }
                }
            });

            repost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inDialog = new ProgressDialog(inContext);
                    inDialog.setMessage("Please wait...");
                    inDialog.setCancelable(false);
                    inDialog.show();
                    String img = media.get(0).getImage();
                    String vid = media.get(0).getVideo();
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
                            i.setAction(Intent.ACTION_SEND);
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
                            i.setAction(Intent.ACTION_SEND);
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
        public void onFailure(String message) {
            if (inDialog.isShowing())
                inDialog.dismiss();
            Log.e(TAG, message);
            Toast.makeText(inContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    // apis
    private class InstaApi extends AsyncTask<String, Void, Void> {
        private InstaListener inListener;
        private JSONObject response;

        private InstaApi(InstaListener inListener){
            this.inListener = inListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {

            response = reqMedia();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                if (response.getString("status").equals("success")) {
                    response.remove("status");
                    Gson gson = new Gson();
                    Instagram instagram = gson.fromJson(response.toString(), Instagram.class);
                    inListener.onResponse(instagram);
                } else {
                    inListener.onFailure(response.getString("message"));
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        }

        private JSONObject reqMedia() {

            JSONObject medias = new JSONObject();
            JSONArray oDataList = new JSONArray();
            String oMsg = "";
            String jsonPly;

            jsonPly = makeServiceCall("https://api.instagram.com/v1/media/" + inInput + "/?access_token=" + inKey);
            if (jsonPly != null) {
                try {
                    JSONObject _plyJsonObj = new JSONObject(jsonPly);

                    JSONObject c = _plyJsonObj.getJSONObject("data");

                    String id = c.getString("id");
                    String link = c.getString("link");
                    String date = c.getString("created_time");
                    String type = c.getString("type");

                    JSONObject user = c.getJSONObject("user");
                    String user_id = user.getString("id");
                    String user_name = user.getString("username");
                    String user_pic = user.getString("profile_picture");

                    String video = "";
                    JSONObject images = c.getJSONObject("images");
                    JSONObject images_url = images.getJSONObject("standard_resolution");
                    String image = images_url.getString("url");
                    if(type.equals("video")) {
                        JSONObject videos = c.getJSONObject("videos");
                        JSONObject videos_url = videos.getJSONObject("standard_resolution");
                        video = videos_url.getString("url");
                    }

                    String caption;
                    try{
                        JSONObject captions = c.getJSONObject("caption");
                        caption = captions.getString("text");
                    }catch(JSONException je){
                        caption = "";
                    }

                    JSONObject pos = new JSONObject();

                    pos.put("id", id);
                    pos.put("link", link);
                    pos.put("date", date);
                    pos.put("type", type);
                    pos.put("user_id", user_id);
                    pos.put("user_name", user_name);
                    pos.put("user_pic", user_pic);
                    pos.put("image", image);
                    pos.put("video", video);
                    pos.put("caption", caption);

                    oDataList.put(pos);

                } catch (final JSONException e) {
                    oMsg = "Data not found.";
                }
            } else {
                oMsg = "Couldn't get data from server.";
            }
            try {
                if (oMsg.equals("")) {
                    medias.put("status", "success");
                    medias.put("media", oDataList);
                } else {
                    medias.put("status", "error");
                    medias.put("message", oMsg);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
            return medias;
        }

    }

    private class InstaUrl extends AsyncTask<String, Void, Void> {
        private String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {

            response = reqId(arg0[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            inInput = response;

            new InstaApi(new Loading()).execute();

        }

        private String reqId(String input) {

            String oId = "";

            String jsonPly = makeServiceCall("https://api.instagram.com/oembed/?url=" + input);
            if (jsonPly != null) {
                try {
                    JSONObject _plyJsonObj = new JSONObject(jsonPly);

                    oId = _plyJsonObj.getString("media_id");

                } catch (final JSONException e) {
                    //Toast.makeText(inContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return oId;
        }

    }

    private String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    // models
    private class Instagram {
        @SerializedName("media")
        @Expose
        private List<Media> media = null;

        private List<Media> getMedia() {
            return media;
        }

        private void setMedia(List<Media> media) {
            this.media = media;
        }

    }

    private class Media {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("link")
        @Expose
        private String link;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("user_id")
        @Expose
        private String user_id;
        @SerializedName("user_name")
        @Expose
        private String user_name;
        @SerializedName("user_pic")
        @Expose
        private String user_pic;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("video")
        @Expose
        private String video;
        @SerializedName("caption")
        @Expose
        private String caption;

        private String getId() {
            return id;
        }

        private void setId(String id) {
            this.id = id;
        }

        private String getLink() {
            return link;
        }

        private void setLink(String link) {
            this.link = link;
        }

        private String getDate() {
            return date;
        }

        private void setDate(String date) {
            this.date = date;
        }

        private String getType() {
            return type;
        }

        private void setType(String type) {
            this.type = type;
        }

        private String getUser_id() {
            return user_id;
        }

        private void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        private String getUser_name() {
            return user_name;
        }

        private void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        private String getUser_pic() {
            return user_pic;
        }

        private void setUser_pic(String user_pic) {
            this.user_pic = user_pic;
        }

        private String getImage() {
            return image;
        }

        private void setImage(String image) {
            this.image = image;
        }

        private String getVideo() {
            return video;
        }

        private void setVideo(String video) {
            this.video = video;
        }

        private String getCaption() {
            return caption;
        }

        private void setCaption(String caption) {
            this.caption = caption;
        }

    }

    // interfaces
    private interface InstaListener {
        void onResponse(Instagram instagram);
        void onFailure(String message);
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
