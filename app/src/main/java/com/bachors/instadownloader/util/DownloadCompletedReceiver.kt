package com.bachors.instadownloader.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bachors.instadownloader.MainActivity

class DownloadCompletedReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if(id != -1L) {
                println("Download with ID $id finished!")
                if (MainActivity.loading.isShowing) {
                    MainActivity.loading.dismiss()
                }
                Toast.makeText(context, "Download with ID $id finished!", Toast.LENGTH_LONG).show()
            }
        }
    }
}