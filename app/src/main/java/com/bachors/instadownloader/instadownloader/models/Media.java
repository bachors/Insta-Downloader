package com.bachors.instadownloader.instadownloader.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */

public class Media {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("video")
    @Expose
    private String video;

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

}
