package com.bachors.instadownloader.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(
    context: Context
): Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String, name: String, type: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("""$type/*""")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(name)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        return downloadManager.enqueue(request)
    }
}