package com.bachors.instadownloader.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Ican Bachors
 * @version 1.1
 * Source: https://github.com/bachors/Insta-Downloader
 */
class Insta {
    @JvmField
    @SerializedName("data")
    @Expose
    val data: MutableList<Data?>? = null
}
