package com.bachors.instadownloader.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */
class Data {
    @JvmField
    @SerializedName("type")
    @Expose
    val type: String? = null

    @JvmField
    @SerializedName("url")
    @Expose
    val url: String? = null

    @JvmField
    @SerializedName("filename")
    @Expose
    val filename: String? = null
}
