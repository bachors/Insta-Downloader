package com.bachors.instadownloader.instadownloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bachors.instadownloader.R;
import com.bachors.instadownloader.instadownloader.adapters.InstaAdapter;
import com.bachors.instadownloader.instadownloader.interfaces.InstaListener;
import com.bachors.instadownloader.instadownloader.models.Instagram;
import com.bachors.instadownloader.instadownloader.models.Media;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */

public class InstaDownloader {
    private static final String TAG = InstaDownloader.class.getSimpleName();

    private Context inContext;
    private ProgressDialog inDialog;
    private InstaAdapter inAdapter;
    private RecyclerView inRv;

    private String inKey, inInput;

    public InstaDownloader(Context inContext){
        this.inContext = inContext;
        inAdapter = new InstaAdapter(inContext);
        inRv = (RecyclerView) ((Activity) inContext).findViewById(R.id.instadown);
    }

    // setting
    public void setAccessToken(String s) {
        this.inKey = s;
    }

    public void setDir(String s) {
        inAdapter.setDir(s);
    }

    public void get(String input) {
        inDialog = new ProgressDialog(inContext);
        inDialog.setMessage("Please wait...");
        inDialog.setCancelable(false);
        inDialog.show();
        inAdapter.clear();

        new InstaUrl().execute(input);

        LinearLayoutManager inLayout = new LinearLayoutManager(inContext, LinearLayoutManager.VERTICAL, false);
        inRv.setLayoutManager(inLayout);
        inRv.setItemAnimator(new DefaultItemAnimator());
        inRv.setAdapter(inAdapter);
    }

    // callback
    private class Loading implements InstaListener {
        @Override
        public void onResponse(Instagram instagram) {
            if (inDialog.isShowing())
                inDialog.dismiss();

            List<Media> media = instagram.getMedia();
            inAdapter.addAll(media);

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
    @SuppressLint("StaticFieldLeak")
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

                    String ctype = c.getString("type");

                    if (ctype.equals("carousel")) {
                        JSONArray carousel = c.getJSONArray("carousel_media");
                        for (int i = 0; i < carousel.length(); i++) {
                            JSONObject m = carousel.getJSONObject(i);
                            String type = m.getString("type");

                            String video = "";
                            String image;
                            if (type.equals("video")) {
                                JSONObject videos = m.getJSONObject("videos");
                                JSONObject videos_url = videos.getJSONObject("standard_resolution");
                                video = videos_url.getString("url");
                                image = video;
                            }else{
                                JSONObject images = m.getJSONObject("images");
                                JSONObject images_url = images.getJSONObject("standard_resolution");
                                image = images_url.getString("url");
                            }

                            JSONObject pos = new JSONObject();

                            pos.put("type", type);
                            pos.put("image", image);
                            pos.put("video", video);

                            oDataList.put(pos);
                        }
                    }else{
                        String type = c.getString("type");
                        String video = "";
                        JSONObject images = c.getJSONObject("images");
                        JSONObject images_url = images.getJSONObject("standard_resolution");
                        String image = images_url.getString("url");
                        if (type.equals("video")) {
                            JSONObject videos = c.getJSONObject("videos");
                            JSONObject videos_url = videos.getJSONObject("standard_resolution");
                            video = videos_url.getString("url");
                        }

                        JSONObject pos = new JSONObject();

                        pos.put("type", type);
                        pos.put("image", image);
                        pos.put("video", video);

                        oDataList.put(pos);
                    }

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

    @SuppressLint("StaticFieldLeak")
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

}
