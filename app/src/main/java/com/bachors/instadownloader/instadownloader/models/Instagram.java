package com.bachors.instadownloader.instadownloader.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */

public class Instagram {

    @SerializedName("media")
    @Expose
    private List<Media> media = null;

    public List<Media> getMedia() {
        return media;
    }

}
